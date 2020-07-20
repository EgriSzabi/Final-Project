package com.example.traveljournal20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowcaseActivity extends AppCompatActivity {

    private TextView tripNameTextView, destinationTextView, priceTextView, descriptionTextView, dateTextView,tripTypeTextView;
    private ImageView tripImage;
    private ImageButton isFavouriteButton;
    private FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private CollectionReference tripsReference=fStore.collection("trips");
    private String tripId;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);


        linearLayout=findViewById(R.id.showcaseLinearLayout);
        tripNameTextView=findViewById(R.id.showcaseTripNameTextView);
        tripTypeTextView=findViewById(R.id.showcaseTripTypeTextView);
        descriptionTextView=findViewById(R.id.showcaseDescriptionTextView);
        destinationTextView=findViewById(R.id.showcaseDestinationTextView);
        priceTextView=findViewById(R.id.showcasePriceTextView);
        dateTextView=findViewById(R.id.showcaseDateTextView);
        tripImage=findViewById(R.id.showcaseImageView);
        isFavouriteButton=findViewById(R.id.showcaseIsFavouriteButton);

        getTripId();
        loadTrip();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.showcase_menu, menu);
        return true;
    }

    public void shareTripOnClick(MenuItem item){
        Bitmap screenShot=takeScreenShot(linearLayout);
        Uri imageUri =saveImage(screenShot);
        shareImageUri(imageUri);
    }

    private Bitmap takeScreenShot(View v) {
        View view=v.getRootView();
        Bitmap screenShot = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenShot);
        view.draw(canvas);
        return screenShot;
    }

    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }

    private Uri saveImage(Bitmap image) {
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.mydomain.fileprovider", file);

        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void getTripId(){
        if(getIntent().hasExtra("Trip ID")){
            tripId=getIntent().getStringExtra("Trip ID");
        }
        else {
            throw new IllegalArgumentException("Activity cannot find  extras " + "Trip ID");
        }
    }

    private void loadTrip(){
        final DocumentReference tripRef=tripsReference.document(tripId);
        tripRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot document=task.getResult();
                    tripNameTextView.setText(document.get("trip_name").toString());
                    descriptionTextView.setText(document.get("description").toString());
                    destinationTextView.setText(document.get("destination").toString());
                    priceTextView.setText(document.get("price").toString());
                    String tripType= document.get("tripType").toString();
                    if(!tripType.isEmpty()){
                        if (tripType.equals(getString(R.string.sea))){
                            tripTypeTextView.setText("(Sea Side)");
                        }
                        else if (tripType.equals(getString(R.string.mountain))){
                            tripTypeTextView.setText("(Mountains)");
                        }
                       else if (tripType.equals(getString(R.string.city))){
                            tripTypeTextView.setText("(City Break)");
                        }
                    }

                    String startDate=document.get(getString(R.string.startDate)).toString();
                    String endDate=document.get(getString(R.string.endDate)).toString();
                    if(startDate.equals(getString(R.string.start_date))){
                        startDate="";
                    }
                    if(endDate.equals(getString(R.string.end_date))){
                        endDate="";
                    }
                    String finalDate ="From: " + startDate + " To: "+ endDate;
                    dateTextView.setText(finalDate);

                    boolean favouriteState = (boolean) document.get(getString(R.string.favourite));
                    if(favouriteState){
                        isFavouriteButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    }else if(!favouriteState){
                        isFavouriteButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    }


                    String imageUri=document.get("image").toString();
                    Picasso.get().load(imageUri).into(tripImage);

                }else {
                    Log.d("TAG", "get failed with ", task.getException());
                }



            }
        });
    }
}
