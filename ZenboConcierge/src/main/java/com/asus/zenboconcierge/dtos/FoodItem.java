package com.asus.zenboconcierge.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class FoodItem implements Parcelable {
    private String itemId;
    private String name;
    private double price;
    private Boolean isAvailable;
    private int quantity;
    private String imgPath;

    public FoodItem() {}

    public FoodItem(String itemId, String name, Double price, Boolean isAvailable, Integer quantity, String imgPath) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.isAvailable = isAvailable;
        this.quantity = quantity;
        this.imgPath = imgPath;
    }

    public FoodItem(Parcel inParcel) {
        this.itemId = inParcel.readString();
        this.name = inParcel.readString();
        this.price = inParcel.readDouble();
        this.isAvailable = inParcel.readByte() != 0;
        this.quantity = inParcel.readInt();
        this.imgPath = inParcel.readString();
    }

    public FoodItem(JSONObject json) {
        try {
            this.itemId = json.getString("itemId");
            this.name = json.getString("name");
            this.price = json.getDouble("price");
            this.isAvailable = json.getBoolean("isAvailable");
            this.quantity = json.getInt("quantity");
            this.imgPath = json.getString("imgPath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean isIsAvailable() {
        return this.isAvailable;
    }

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("itemId", this.itemId);
            json.put("name", this.name);
            json.put("price", this.price);
            json.put("isAvailable", this.isAvailable);
            json.put("quantity", this.quantity);
            json.put("imgPath", this.imgPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outParcel, int flags) {
        outParcel.writeString(this.itemId);
        outParcel.writeString(this.name);
        outParcel.writeDouble(this.price);
        outParcel.writeByte((byte) (this.isAvailable ? 1 : 0));
        outParcel.writeInt(this.quantity);
        outParcel.writeString(this.imgPath);
    }

    public static final Parcelable.Creator<FoodItem> CREATOR = new Parcelable.Creator<FoodItem>() {
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };
}