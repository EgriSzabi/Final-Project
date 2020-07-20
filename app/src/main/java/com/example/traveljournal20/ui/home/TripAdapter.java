package com.example.traveljournal20.ui.home;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.traveljournal20.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class TripAdapter extends FirestoreRecyclerAdapter<Trip, TripAdapter.TripsViewHolder> {

    public TripAdapter(@NonNull FirestoreRecyclerOptions<Trip> options) {
        super(options);

    }

    private OnItemClickListener listener;
    @Override
    protected void onBindViewHolder(@NonNull TripsViewHolder holder, int position, @NonNull Trip model) {


        CardView mCardView=(CardView)holder.itemView;
        mCardView.setCardBackgroundColor(Color.parseColor("#f5f5f0"));
        
        holder.tripNameTextView.setText(model.getTrip_name());
        holder.destinationTextView.setText(model.getDestination());
        holder.priceTextView.setText(model.getPrice());
        holder.descriptionTextView.setText(model.getDescription());
        Picasso.get().load(model.getImage()).into(holder.tripImageView);
        boolean isFavourite=model.isFavourite;
        if(isFavourite==true)
        {
            holder.isFavouriteImageButton.setBackgroundResource(android.R.drawable.btn_star_big_on);
        }
        else {
            holder.isFavouriteImageButton.setBackgroundResource(android.R.drawable.btn_star_big_off);
        }
    }

    @NonNull
    @Override
    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item,
                parent, false);
        return new TripsViewHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }




    class TripsViewHolder extends  RecyclerView.ViewHolder{

        TextView tripNameTextView, destinationTextView, priceTextView, descriptionTextView;
        ImageView tripImageView;
        ImageButton isFavouriteImageButton;

        public TripsViewHolder(@NonNull final View itemView) {
            super(itemView);

            tripNameTextView = itemView.findViewById(R.id.tripNameTextView);
            destinationTextView= itemView.findViewById(R.id.destinationTextView);
            priceTextView=itemView.findViewById(R.id.priceTextView);
            isFavouriteImageButton=itemView.findViewById(R.id.isFavouriteButton);
            descriptionTextView=itemView.findViewById(R.id.descriptionTextView);
            tripImageView= itemView.findViewById(R.id.tripImageView);

            isFavouriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position= getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position,1);
                    }
                }
            });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position= getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener!=null){
                            listener.onItemClick(getSnapshots().getSnapshot(position),position,2);
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener!=null){
                            listener.onItemClick(getSnapshots().getSnapshot(position),position,0);
                        }

                        return false;
                    }
                });

        }

    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position, int id);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
