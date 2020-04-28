package com.asus.zenboconcierge.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderMetadata implements Parcelable {
    private long orderMetadataId;
    private long orderDurationInSeconds = 0L;
    private int numRepetitionsLogin = 0;
    private int numRepetitionsMenu = 0;
    private int numRepetitionsSummary = 0;
    private int numTouchInputs = 0;
    private int numVoiceInputs = 0;

    public OrderMetadata() {}

    public OrderMetadata(long orderMetadataId, long orderDurationInSeconds, int numRepetitionsLogin, int numRepetitionsMenu, int numRepetitionsSummary, int numTouchInputs, int numVoiceInputs, long orderId) {
        this.orderMetadataId = orderMetadataId;
        this.orderDurationInSeconds = orderDurationInSeconds;
        this.numRepetitionsLogin = numRepetitionsLogin;
        this.numRepetitionsMenu = numRepetitionsMenu;
        this.numRepetitionsSummary = numRepetitionsSummary;
        this.numTouchInputs = numTouchInputs;
        this.numVoiceInputs = numVoiceInputs;
    }

    public OrderMetadata(Parcel inParcel) {
        this.orderMetadataId = inParcel.readLong();
        this.orderDurationInSeconds = inParcel.readLong();
        this.numRepetitionsLogin = inParcel.readInt();
        this.numRepetitionsMenu = inParcel.readInt();
        this.numRepetitionsSummary = inParcel.readInt();
        this.numTouchInputs = inParcel.readInt();
        this.numVoiceInputs = inParcel.readInt();
    }

    public OrderMetadata(JSONObject json) {
        try {
            this.orderMetadataId = json.getLong("orderMetadataId");
            this.orderDurationInSeconds = json.getLong("orderDurationInSeconds");
            this.numRepetitionsLogin = json.getInt("numRepetitionsLogin");
            this.numRepetitionsMenu = json.getInt("numRepetitionsMenu");
            this.numRepetitionsSummary = json.getInt("numRepetitionsSummary");
            this.numTouchInputs = json.getInt("numTouchInputs");
            this.numVoiceInputs = json.getInt("numVoiceInputs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getOrderMetadataId() {
        return orderMetadataId;
    }

    public void setOrderMetadataId(long orderMetadataId) {
        this.orderMetadataId = orderMetadataId;
    }

    public long getOrderDurationInSeconds() {
        return orderDurationInSeconds;
    }

    public void setOrderDurationInSeconds(long orderDurationInSeconds) {
        this.orderDurationInSeconds = orderDurationInSeconds;
    }

    public int getNumRepetitionsLogin() {
        return numRepetitionsLogin;
    }

    public void setNumRepetitionsLogin(int numRepetitionsLogin) {
        this.numRepetitionsLogin = numRepetitionsLogin;
    }

    public int getNumRepetitionsMenu() {
        return numRepetitionsMenu;
    }

    public void setNumRepetitionsMenu(int numRepetitionsMenu) {
        this.numRepetitionsMenu = numRepetitionsMenu;
    }

    public int getNumRepetitionsSummary() {
        return numRepetitionsSummary;
    }

    public void setNumRepetitionsSummary(int numRepetitionsSummary) {
        this.numRepetitionsSummary = numRepetitionsSummary;
    }

    public int getNumTouchInputs() {
        return numTouchInputs;
    }

    public void setNumTouchInputs(int numTouchInputs) {
        this.numTouchInputs = numTouchInputs;
    }

    public int getNumVoiceInputs() {
        return numVoiceInputs;
    }

    public void setNumVoiceInputs(int numVoiceInputs) {
        this.numVoiceInputs = numVoiceInputs;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("orderMetadataId", this.orderMetadataId);
            json.put("orderDurationInSeconds", this.orderDurationInSeconds);
            json.put("numRepetitionsLogin", this.numRepetitionsLogin);
            json.put("numRepetitionsMenu", this.numRepetitionsMenu);
            json.put("numRepetitionsSummary", this.numRepetitionsSummary);
            json.put("numTouchInputs", this.numTouchInputs);
            json.put("numVoiceInputs", this.numVoiceInputs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @NonNull
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
        outParcel.writeLong(orderMetadataId);
        outParcel.writeLong(orderDurationInSeconds);
        outParcel.writeInt(numRepetitionsLogin);
        outParcel.writeInt(numRepetitionsMenu);
        outParcel.writeInt(numRepetitionsSummary);
        outParcel.writeInt(numTouchInputs);
        outParcel.writeInt(numVoiceInputs);
    }

    public static final Parcelable.Creator<OrderMetadata> CREATOR = new Parcelable.Creator<OrderMetadata>() {
        public OrderMetadata createFromParcel(Parcel in) {
            return new OrderMetadata(in);
        }

        public OrderMetadata[] newArray(int size) {
            return new OrderMetadata[size];
        }
    };
}
