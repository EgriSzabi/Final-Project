package com.example.traveljournal20.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.traveljournal20.CreateTripActivity;
import com.example.traveljournal20.EditTripActivity;
import com.example.traveljournal20.R;
import com.example.traveljournal20.ShowcaseActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment{

    private CoordinatorLayout coordinatorLayout;
    private  View HomeView;
    private  RecyclerView recyclerView;
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private CollectionReference tripsReference=fStore.collection("trips");
    private TripAdapter adapter;
    public  HomeFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
        container.removeAllViews();
    }

        HomeView = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = HomeView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateTripActivity.class));
            }
        });

        Query query =tripsReference.orderBy("destination");
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Trip>().setQuery(query,Trip.class)
                .build();
        adapter = new TripAdapter(options);
        recyclerView = (RecyclerView) HomeView.findViewById(R.id.home_recyclerView);
        coordinatorLayout=(CoordinatorLayout) HomeView.findViewById(R.id.coordinator);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Trip deletedItem=adapter.getItem(viewHolder.getAdapterPosition());
                adapter.deleteItem(viewHolder.getAdapterPosition());
                showSnackBar(deletedItem);
            }
        }).attachToRecyclerView(recyclerView);

            adapter.setOnItemClickListener(new TripAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position, int id) {
                    String tripId= documentSnapshot.getId();
                    if(id==0){
                         Intent intent = new Intent(getActivity(), EditTripActivity.class);
                         intent.putExtra("Trip ID",tripId);
                         startActivity(intent);
                    }
                    else if(id==1){
                        DocumentReference tripRef=tripsReference.document(tripId);
                        boolean favouriteState = (boolean) documentSnapshot.get(getString(R.string.favourite));
                        if(favouriteState==false) {
                            Map<String, Object> information = new HashMap<>();
                            information.put("favourite", true);
                            tripRef.update(information);
                        }
                        else if(favouriteState){
                            Map<String, Object> information = new HashMap<>();
                            information.put("favourite", false);
                            tripRef.update(information);
                        }


                    }
                    else if(id==2){
                        Intent intent = new Intent(getActivity(), ShowcaseActivity.class);
                        intent.putExtra("Trip ID",tripId);
                        startActivity(intent);
                    }

                }
            });



        return HomeView;
}


    public void showSnackBar(final Trip deletedTrip){
        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Are you sure you want to delete it?", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tripsReference.add(deletedTrip);
                    }
                });
        snackbar.show();
    }

    public void onStart(){
        super.onStart();
        adapter.startListening();
    }
    public void onStop(){
        super.onStop();
        adapter.startListening();
    }






}
