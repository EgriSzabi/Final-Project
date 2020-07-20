package com.example.traveljournal20.ui.home;

public class Trip {

    protected String trip_name,destination,image,price,description, startDate, endDate, tripType;
    protected boolean isFavourite;

    public  Trip(){

    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setImageUri(String imageUri) {
        this.image = image;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public String getDestination() {
        return destination;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription(){return description;}


    public boolean isFavourite() {
        return isFavourite;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public Trip(String trip_name, String destination, String description, String price, String image, String startDate, String endDate, String tripType, boolean isFavourite) {
        this.trip_name = trip_name;
        this.destination = destination;
        this.description=description;
        this.price = price;
        this.image=image;
        this.isFavourite = isFavourite;
        this.startDate=startDate;
        this.endDate=endDate;
        this.tripType=tripType;
    }
}

