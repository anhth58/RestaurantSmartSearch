package com.me.restaurantsmartsearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class Restaurant extends RealmObject implements Parcelable{
    @PrimaryKey
    private int id;
    private String name;
    private String address;
    private String description;
    private String time;
    private String type;
    private double latitude;
    private double longitude;
    private String price;
    private String image;
    private double pLocation;
    private double pSpace;
    private double pServe;
    private double pQuality;
    private double pCost;

    protected Restaurant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        description = in.readString();
        time = in.readString();
        type = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        price = in.readString();
        image = in.readString();
        pLocation = in.readDouble();
        pSpace = in.readDouble();
        pServe = in.readDouble();
        pQuality = in.readDouble();
        pCost = in.readDouble();
    }

    public Restaurant(){

    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getpCost() {
        return pCost;
    }

    public void setpCost(double pCost) {
        this.pCost = pCost;
    }

    public double getpLocation() {
        return pLocation;
    }

    public void setpLocation(double pLocation) {
        this.pLocation = pLocation;
    }

    public double getpQuality() {
        return pQuality;
    }

    public void setpQuality(double pQuality) {
        this.pQuality = pQuality;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getpServe() {
        return pServe;
    }

    public void setpServe(double pServe) {
        this.pServe = pServe;
    }

    public double getpSpace() {
        return pSpace;
    }

    public void setpSpace(double pSpace) {
        this.pSpace = pSpace;
    }

    public String getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(time);
        dest.writeString(type);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeDouble(pLocation);
        dest.writeDouble(pSpace);
        dest.writeDouble(pServe);
        dest.writeDouble(pQuality);
        dest.writeDouble(pCost);
    }
}
