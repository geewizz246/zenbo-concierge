package com.asus.zenboconcierge.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Order implements Parcelable {
    private long orderId;
    private Timestamp orderDate;
    private Boolean orderSuccessful = false;
    private int numOfRepetitions = 0;
    private long orderDurationInSeconds = 0L;
    private Timestamp requestedPickUpTime;
    private Timestamp actualPickUpTime;
    private Boolean isPaid = false;
    private Timestamp timePaid;
    private Boolean isFulfilled = false;
    private Timestamp timeFulfilled;
    private double total = 0.00;
    private String customer;
    private List<OrderItem> orderItems = new ArrayList<>();

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Boolean getOrderSuccessful() {
        return orderSuccessful;
    }

    public void setOrderSuccessful(Boolean orderSuccessful) {
        this.orderSuccessful = orderSuccessful;
    }

    public int getNumOfRepetitions() {
        return numOfRepetitions;
    }

    public void setNumOfRepetitions(Integer numOfRepetitions) {
        this.numOfRepetitions = numOfRepetitions;
    }

    public long getOrderDurationInSeconds() {
        return orderDurationInSeconds;
    }

    public void setOrderDurationInSeconds(Long orderDurationInSeconds) {
        this.orderDurationInSeconds = orderDurationInSeconds;
    }

    public Timestamp getRequestedPickUpTime() {
        return requestedPickUpTime;
    }

    public void setRequestedPickUpTime(Timestamp requestedPickUpTime) {
        this.requestedPickUpTime = requestedPickUpTime;
    }

    public Timestamp getActualPickUpTime() {
        return actualPickUpTime;
    }

    public void setActualPickUpTime(Timestamp actualPickUpTime) {
        this.actualPickUpTime = actualPickUpTime;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public Timestamp getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Timestamp timePaid) {
        this.timePaid = timePaid;
    }

    public Boolean getFulfilled() {
        return isFulfilled;
    }

    public void setFulfilled(Boolean fulfilled) {
        isFulfilled = fulfilled;
    }

    public Timestamp getTimeFulfilled() {
        return timeFulfilled;
    }

    public void setTimeFulfilled(Timestamp timeFulfilled) {
        this.timeFulfilled = timeFulfilled;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;

        this.total = 0.00;
        for (OrderItem item : orderItems) {
            this.total += item.getFoodItem().getPrice() * item.getQuantityOrdered();
        }
    }

    public Order() {}

    public Order(Long orderId, Timestamp orderDate, Boolean orderSuccessful, Integer numOfRepetitions, Long orderDurationInSeconds,Timestamp requestedPickUpTime, Timestamp actualPickUpTime, Boolean isPaid, Timestamp timePaid, Boolean isFulfilled, Timestamp timeFulfilled, Double total, String customer, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderSuccessful = orderSuccessful;
        this.numOfRepetitions = numOfRepetitions;
        this.orderDurationInSeconds = orderDurationInSeconds;
        this.requestedPickUpTime = requestedPickUpTime;
        this.actualPickUpTime = actualPickUpTime;
        this.isPaid = isPaid;
        this.timePaid = timePaid;
        this.isFulfilled = isFulfilled;
        this.timeFulfilled = timeFulfilled;
        this.total = total;
        this.customer = customer;
        this.orderItems = orderItems;
    }

    public Order(Parcel inParcel) {
        Long longDate = null;
        this.orderId = inParcel.readLong();
        longDate = inParcel.readLong();
        this.orderDate = longDate != 0 ? new Timestamp(longDate) : null;
        this.orderSuccessful = inParcel.readByte() != 0;
        this.numOfRepetitions = inParcel.readInt();
        this.orderDurationInSeconds = inParcel.readLong();
        longDate = inParcel.readLong();
        this.requestedPickUpTime = longDate != 0 ? new Timestamp(longDate) : null;
        longDate = inParcel.readLong();
        this.actualPickUpTime = longDate != 0 ? new Timestamp(longDate) : null;
        this.isPaid = inParcel.readByte() != 0;
        longDate = inParcel.readLong();
        this.timePaid = longDate != 0 ? new Timestamp(longDate) : null;
        this.isFulfilled = inParcel.readByte() != 0;
        longDate = inParcel.readLong();
        this.timeFulfilled = longDate != 0 ? new Timestamp(longDate) : null;
        this.total = inParcel.readDouble();
        this.customer = inParcel.readString();
        inParcel.readTypedList(this.orderItems, OrderItem.CREATOR);
    }
    
    public Order(JSONObject json) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss.SSSZ", Locale.ENGLISH);
        try {
            this.orderId = json.getLong("orderId");
            this.orderSuccessful = json.getBoolean("orderSuccessful");
            this.numOfRepetitions = json.getInt("numOfRepetitions");
            this.orderDurationInSeconds = json.getLong("orderDurationInSeconds");
            this.isPaid = json.getBoolean("isPaid");
            this.isFulfilled = json.getBoolean("isFulfilled");
            this.total = json.getDouble("total");
            this.customer = json.getString("customer");

            // Handle dates
            String orderDate = json.getString("orderDate"),
                    requestedPickUpTime = json.getString("requestedPickUpTime"),
                    actualPickUpTime = json.getString("actualPickUpTime"),
                    timePaid = json.getString("timePaid"),
                    timeFulfilled = json.getString("timeFulfilled");

            this.orderDate = !orderDate.equals("null") ? new Timestamp(dateFormat.parse(orderDate).getTime()) : null;
            this.requestedPickUpTime = !requestedPickUpTime.equals("null") ? new Timestamp(dateFormat.parse(requestedPickUpTime).getTime()) : null;
            this.actualPickUpTime = !actualPickUpTime.equals("null") ? new Timestamp(dateFormat.parse(actualPickUpTime).getTime()) : null;
            this.timePaid = !timePaid.equals("null") ? new Timestamp(dateFormat.parse(timePaid).getTime()) : null;
            this.timeFulfilled = !timeFulfilled.equals("null") ? new Timestamp(dateFormat.parse(timeFulfilled).getTime()) : null;

            JSONArray orderItems = json.getJSONArray("orderItems");
            for (int i = 0; i < orderItems.length(); i++) {

                this.orderItems.add(new OrderItem(orderItems.getJSONObject(i)));
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss.SSSZ", Locale.ENGLISH);

        try {
            json.put("orderId", this.orderId);
            json.put("orderDate", this.orderDate != null ? dateFormat.format(this.orderDate) : JSONObject.NULL);
            json.put("orderSuccessful", this.orderSuccessful);
            json.put("numOfRepetitions", this.numOfRepetitions);
            json.put("orderDurationInSeconds", this.orderDurationInSeconds);
            json.put("requestedPickUpTime", this.requestedPickUpTime != null ? dateFormat.format(this.requestedPickUpTime) : JSONObject.NULL);
            json.put("actualPickUpTime", this.actualPickUpTime != null ? dateFormat.format(this.actualPickUpTime) : JSONObject.NULL);
            json.put("isPaid", this.isPaid);
            json.put("timePaid", this.timePaid != null ? dateFormat.format(this.timePaid) : JSONObject.NULL);
            json.put("isFulfilled",  this.isFulfilled);
            json.put("timeFulfilled", this.timeFulfilled != null ? dateFormat.format(this.timeFulfilled) : JSONObject.NULL);
            json.put("total", this.total);
            json.put("customer", this.customer);

            // Handle orderItems
            JSONArray orderItems = new JSONArray();
            for (OrderItem orderItem : this.orderItems) { orderItems.put(orderItem.toJson()); }
            json.put("orderItems", orderItems);
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
        outParcel.writeLong(this.orderDate != null ? this.orderDate.getTime() : 0);
        outParcel.writeByte((byte) (this.orderSuccessful ? 1 : 0));
        outParcel.writeInt(this.numOfRepetitions);
        outParcel.writeLong(this.orderDurationInSeconds);
        outParcel.writeLong(this.requestedPickUpTime != null ? this.requestedPickUpTime.getTime() : 0);
        outParcel.writeLong(this.actualPickUpTime != null ? this.actualPickUpTime.getTime() : 0);
        outParcel.writeByte((byte) (this.isPaid ? 1 : 0));
        outParcel.writeLong(this.timePaid != null ? this.timePaid.getTime() : 0);
        outParcel.writeByte((byte) (this.isFulfilled ? 1 : 0));
        outParcel.writeLong(this.timeFulfilled != null ? this.timeFulfilled.getTime() : 0);
        outParcel.writeDouble(this.total);
        outParcel.writeString(this.customer);
        outParcel.writeTypedList(this.orderItems);
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}