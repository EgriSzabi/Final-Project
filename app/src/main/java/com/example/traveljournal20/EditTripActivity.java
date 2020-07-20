package com.example.traveljournal20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.traveljournal20.datePicker.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private int datePickerId=0;
    private String tripId;
    private EditText tripNameEditText, destinationEditText, descriptionEditText,priceEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private TextView startDateTextView, endDateTextView;
    private CollectionReference tripsReference=fStore.collection("trips");
    private RadioGroup radioGroup;
    private RadioButton cityBreakRadioButton, seeSideRadioButton, mountainsRadioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        tripNameEditText=findViewById(R.id.editTripNameEditText);
        destinationEditText=findViewById(R.id.editDestinationEditText);
        priceEditText=findViewById(R.id.editPriceEditText);
        descriptionEditText=findViewById(R.id.editDescriptionEditText);
        progressBar=findViewById(R.id.editTripProgressBar);
        startDateTextView=findViewById(R.id.editStartDateTextView);
        endDateTextView=findViewById(R.id.editEndDateTextView);
        radioGroup=findViewById(R.id.radioGroup);
        cityBreakRadioButton=findViewById(R.id.cityBreakRadioButton);
        seeSideRadioButton=findViewById(R.id.seeSideRadioButton);
        mountainsRadioButton=findViewById(R.id.mountainRadioButton);

        getTripId();
        loadTrip();

    }



    private void loadTrip() {
        final DocumentReference tripRef=tripsReference.document(tripId);
        tripRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    tripNameEditText.setText(document.get("trip_name").toString());
                    descriptionEditText.setText(document.get("description").toString());
                    destinationEditText.setText(document.get("destination").toString());
                    priceEditText.setText(document.get("price").toString());
                    String tripType= document.get("tripType").toString();
                    if(tripType.equals(getString(R.string.city))){
                        radioGroup.check(R.id.cityBreakRadioButton);
                    }else if(tripType.equals(getString(R.string.mountain))){
                        radioGroup.check(R.id.mountainRadioButton);

                    }else if(tripType.equals(getString(R.string.sea))){
                        radioGroup.check(R.id.seeSideRadioButton);
                    }

                    String startDate=document.get(getString(R.string.startDate)).toString();
                    String endDate=document.get(getString(R.string.endDate)).toString();
                    if(!startDate.isEmpty()){
                        startDateTextView.setText(startDate);
                    }
                    if(!endDate.isEmpty()){
                        endDateTextView.setText(endDate);
                    }



                }else {
                    Log.d("TAG", "get failed with ", task.getException());
                }

            }
        });
    }

    private void getTripId() {
        if(getIntent().hasExtra("Trip ID")){
            tripId=getIntent().getStringExtra("Trip ID");
        }
        else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "Trip ID");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        if(datePickerId==1){
            String currentDateString=DateFormat.getDateInstance().format(c.getTime());
            startDateTextView.setText(currentDateString);
        }
        else if(datePickerId==2){
            String currentDateString=DateFormat.getDateInstance().format(c.getTime());
            endDateTextView.setText(currentDateString);
        }
    }

    public void editTripOnClick(View view) {
        DocumentReference tripRef=tripsReference.document(tripId);
        String tripName=tripNameEditText.getText().toString();
        String destination=destinationEditText.getText().toString();
        String description=descriptionEditText.getText().toString();
        String price=priceEditText.getText().toString();
        String startDate=startDateTextView.getText().toString();
        String endDate=endDateTextView.getText().toString();
        String tripType="";
        if (cityBreakRadioButton.isChecked()){
            tripType=getString(R.string.city);
        }else if(seeSideRadioButton.isChecked()){
            tripType=getString(R.string.sea);
        }else if(mountainsRadioButton.isChecked()){
            tripType=getString(R.string.mountain);
        }
        Map<String, Object> information = new HashMap<>();
        information.put("trip_name",tripName);
        information.put("destination",destination);
        information.put("description",description);
        information.put("price",price);
        information.put("startDate",startDate);
        information.put("endDate",endDate);
        information.put("tripType",tripType);
        tripRef.update(information).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(EditTripActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditTripActivity.this,getString(R.string.update_failed),Toast.LENGTH_LONG).show();
            }
        });



    }

    public void setStartDateOnClick(View view) {
        datePickerId=1;
        DialogFragment datePicker= new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(),"date picker");

    }

    public void setEndDateOnClick(View view) {
        datePickerId=2;
        DialogFragment datePicker= new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(),"date picker");

    }
}
