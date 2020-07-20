package com.example.traveljournal20;


import android.content.Intent;

import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import android.widget.TextView;


import com.example.traveljournal20.ui.aboutUs.AboutUsFragment;
import com.example.traveljournal20.ui.contact.ContactFragment;
import com.example.traveljournal20.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fStore;
    private String firstName;
    private String lastName;
    private String userID;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        currentUser=mAuth.getCurrentUser();
        recyclerView=findViewById(R.id.home_recyclerView);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNewHeader();

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_logOut) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            openFragment(new HomeFragment());
        } else if (id == R.id.nav_about) {
            openFragment(new AboutUsFragment());
        } else if (id == R.id.nav_contact) {
            openFragment(new ContactFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void openFragment(Fragment fragment) {
        // 4 steps to add dynamically a fragment inside of an activity
        // step 1: create an instance of FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // step 2: create an instance of FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // step 3: replace container content with the fragment content
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        // step 4: commit transaction
        fragmentTransaction.commit();
    }

    public void updateNewHeader(){
        userID=currentUser.getUid();
        DocumentReference documentReference =fStore.collection(getString(R.string.users)).document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               firstName=documentSnapshot.getString(getString(R.string.firstName));
               lastName=documentSnapshot.getString(getString(R.string.lastName));
                NavigationView navigationView= (NavigationView) findViewById(R.id.nav_view);
                View headerView =navigationView.getHeaderView(0);
                TextView nameTextView= headerView.findViewById(R.id.navHeaderNameTextView);
                TextView emailTextView = headerView.findViewById(R.id.navHeaderEmailTextView);
                nameTextView.setText(firstName+" "+ lastName);
                emailTextView.setText(currentUser.getEmail());
            }
        });



    }

    public void logOutOnClick(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

}
