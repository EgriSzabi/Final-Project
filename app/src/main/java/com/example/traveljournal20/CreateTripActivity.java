package com.example.traveljournal20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.traveljournal20.ui.home.Trip;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class CreateTripActivity extends AppCompatActivity {


    private ImageView tripImage;
    private static final int PICK_IMAGE_REQUEST=1;
    private EditText tripNameEditText, destinationEditText, descriptionEditText,priceEditText;
    private ProgressBar progressBar;
    private Uri imageUri;
    private Uri newImageUri;
    private StorageReference storageReference;
    private  FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private CollectionReference tripsReference;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        tripNameEditText=findViewById(R.id.addTripNameEditText);
        destinationEditText=findViewById(R.id.addDestinationEditText);
        descriptionEditText=findViewById(R.id.addDescriptionEditText);
        priceEditText=findViewById(R.id.addPriceEditText);
        tripImage=findViewById(R.id.addTripImageView);
        progressBar=findViewById(R.id.addTripProgressBar);

        storageReference= FirebaseStorage.getInstance().getReference("images");
        tripsReference = fStore.collection("trips");

    }

    public void addTripOnClick(View view) {
        if(mUploadTask != null && mUploadTask.isInProgress())
        {
            Toast.makeText(CreateTripActivity.this,getString(R.string.upload_in_progress),Toast.LENGTH_SHORT);
        }
        else {
            uploadFile();
        }
    }

    public void addImageOnClick(View view) {
        openFileChooser();
    }
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            imageUri=data.getData();
            tripImage.setImageURI(imageUri);
    }

    private void uploadFile() {

        final String tripName=tripNameEditText.getText().toString();
        final String destination=destinationEditText.getText().toString();
        final String price=priceEditText.getText().toString();
        final String description =descriptionEditText.getText().toString();

        if(tripName.isEmpty()){
            tripNameEditText.setError(getString(R.string.trip_name_missing));
        }
        else if (destination.isEmpty()){
            destinationEditText.setError(getString(R.string.destination_missing));
        }
        else if(description.isEmpty()){
            descriptionEditText.setError(getString(R.string.description_missing));
        }
        else if(price.isEmpty()){
            priceEditText.setError(getString(R.string.price_missing));
        }
        else if(!tripName.isEmpty() && !description.isEmpty() && !description.isEmpty() && !price.isEmpty() && imageUri != null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()
                    + "."+getFileExtension(imageUri));
                mUploadTask= fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newImageUri=uri;
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 1000);
                                    Toast.makeText(CreateTripActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                    tripsReference.add(new Trip(tripName,destination,description,price,newImageUri.toString(),"", "", "",false));
                                    finish();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateTripActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int)progress);
                        }
                    });
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
