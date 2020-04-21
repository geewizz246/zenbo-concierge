package com.asus.zenboconcierge.dtos;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public Customer() {}

    public Customer(String email, String firstName, String lastName, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    
    public Customer(Parcel inParcel) {
        this.email = inParcel.readString();
        this.firstName = inParcel.readString();
        this.lastName = inParcel.readString();
        this.phone = inParcel.readString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel outParcel, int flags) {
        outParcel.writeString(this.email);
        outParcel.writeString(this.firstName);
        outParcel.writeString(this.lastName);
        outParcel.writeString(this.phone);
    }

    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
