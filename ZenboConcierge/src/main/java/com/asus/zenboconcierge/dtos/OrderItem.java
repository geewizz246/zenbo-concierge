package com.asus.zenboconcierge.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class OrderItem implements Parcelable{
    private Long orderId;
    private FoodItem foodItem;
    private int quantityOrdered = 1;

    public OrderItem() {}

    public OrderItem(Long orderId, FoodItem foodItem, Integer quantityOrdered) {
        this.orderId = orderId;
        this.foodItem = foodItem;
        this.quantityOrdered = quantityOrdered;
    }

    public OrderItem(Parcel inParcel) {
        this.orderId = inParcel.readLong();
        this.foodItem = inParcel.readParcelable(FoodItem.class.getClassLoader());
        this.quantityOrdered = inParcel.readInt();
    }

    public OrderItem(JSONObject json) {
        try {
            this.orderId = json.getLong("orderId");
            this.foodItem = new FoodItem(json.getJSONObject("foodItem"));
            this.quantityOrdered = json.getInt("quantityOrdered");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return this.orderId.equals(orderItem.orderId) &&
                this.foodItem.getItemId().equals(orderItem.foodItem.getItemId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.orderId, this.foodItem.getItemId());
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("orderId", this.orderId);
            json.put("itemId", this.foodItem.getItemId());
            json.put("quantityOrdered", this.quantityOrdered);
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
        outParcel.writeLong(this.orderId);
        outParcel.writeParcelable(this.foodItem, flags);
        outParcel.writeInt(this.quantityOrdered);
    }

    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
}