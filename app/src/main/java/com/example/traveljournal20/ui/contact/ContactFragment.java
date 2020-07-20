package com.example.traveljournal20.ui.contact;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.traveljournal20.R;

public class ContactFragment extends Fragment {



private ImageButton facebookImageButton;
private ImageButton instagramImageButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        facebookImageButton=(ImageButton) root.findViewById(R.id.facebookImageButton);
        instagramImageButton=(ImageButton) root.findViewById(R.id.instagrammImageButton);
        facebookImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl ="https://www.facebook.com/AndroidOfficial";
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });

        instagramImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instagramIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl ="https://www.instagram.com/egriszabolss/";
                instagramIntent.setData(Uri.parse(facebookUrl));
                startActivity(instagramIntent);
            }
        });


        return root;


    }


}
