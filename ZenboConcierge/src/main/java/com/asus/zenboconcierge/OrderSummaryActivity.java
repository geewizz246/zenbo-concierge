package com.asus.zenboconcierge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.zenboconcierge.dtos.Customer;
import com.asus.zenboconcierge.dtos.Order;
import com.asus.zenboconcierge.dtos.OrderItem;
import com.asus.zenboconcierge.utils.HttpUtils;
import com.asus.zenboconcierge.utils.OrderSummaryListAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.robot.asus.robotactivity.RobotActivity;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Date;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class OrderSummaryActivity extends RobotActivity {
    private static final String TAG = "OrderSummaryActivity";
    public static Boolean isOrderSummaryActive = false;

    private Customer customer;
    private Order order;
    private List<OrderItem> orderItems;
    private final Context context = OrderSummaryActivity.this;
    private String alertMsg = null;

    private AlertDialog alertDialog;
    private TimePickerDialog timePicker;
    private Button btnConfirm, btnSetPickupTime;
    private TextView textViewPickupTime;

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

        }
    };

    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {
            Log.d(TAG, "Zenbo dialog complete");
        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public OrderSummaryActivity() { super(robotCallback, robotListenCallback); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        Log.d(TAG, TAG + " created");

        // Get the intent that started this activity and extract extras
        Intent intent = getIntent();
        customer = intent.getParcelableExtra("customer");
        order = intent.getParcelableExtra("finalOrder");
        orderItems = order.getOrderItems();

        setUpOrderSummary();

        btnConfirm = findViewById(R.id.btn_confirm_order);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Declare order a success
                order.setOrderSuccessful(true);
                saveOrder();

                isOrderSummaryActive = false;

                // Go to Success Activity
                Intent successIntent = new Intent(context, OrderSuccessActivity.class);
                successIntent.putExtra("customer", customer);
                successIntent.putExtra("order", order);
                context.startActivity(successIntent);

                // Finish this and all previous activities
                finishAffinity();
            }
        });

        // Disable finish order button
        btnConfirm.setEnabled(false);

        robotAPI.robot.speak(getString(R.string.zenbo_speak_order_summary));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, TAG + " resumed");
        isOrderSummaryActive = true;

        if (order != null) {
            // Assume the order is still being performed
            Log.d(TAG, "Order " + order.getOrderId() + " not aborted");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, TAG + " paused");

        if (order != null && isOrderSummaryActive) {
            // Assume the order was aborted
            Log.d(TAG, "Order " + order.getOrderId() + " aborted");
            // Send an update to the server
            saveOrder();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, TAG + " stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Application destroyed @" + TAG);
    }

    private void setUpOrderSummary() {
        // Display the order ID
        TextView textViewOrderId = findViewById(R.id.textview_order_summary_order_id);
        textViewOrderId.setText(Long.toString(order.getOrderId()));

        // Set up ListView for order summary
        ListView listViewOrderSummary = findViewById(R.id.listview_order_summary);
        final OrderSummaryListAdapter orderSummaryListAdapter = new OrderSummaryListAdapter(context, R.layout.list_item_order_summary, orderItems);
        listViewOrderSummary.setAdapter(orderSummaryListAdapter);

        // Display the order total
        TextView textViewOrderTotal = findViewById(R.id.textview_order_summary_total);
        textViewOrderTotal.setText(String.format("$ %.2f", order.getTotal()));

        // Set up TextView for time
        textViewPickupTime = findViewById(R.id.textview_pickup_time);
        textViewPickupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpTimePickerDialog();
            }
        });

        // Set up TimePickerDialog
        btnSetPickupTime = findViewById(R.id.btn_set_pickup_time);
        btnSetPickupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpTimePickerDialog();
            }
        });
    }

    private void saveOrder() {
        // Get the time when the order was stopped and set new duration
        final Date now = new Date();
        long duration = (now.getTime() - order.getOrderDate().getTime()) / 1000;
        order.setOrderDurationInSeconds(duration);
        Log.d(TAG, order.toString());

        // Disable buttons
        btnSetPickupTime.setEnabled(false);
        btnConfirm.setEnabled(false);

        StringEntity orderEntity = null;
        String url = "order/update/" + order.getOrderId();

        String strOrder = StringEscapeUtils.unescapeJava(order.toString());
        try {
            orderEntity = new StringEntity(strOrder);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.put(OrderSummaryActivity.this, url, orderEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
                Order newOrder = new Order(response);
                order = newOrder;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, errorResponse.toString());

                // Enable buttons
                btnSetPickupTime.setEnabled(true);
                if (btnSetPickupTime.getVisibility() == View.GONE) {
                    btnConfirm.setEnabled(true);
                }
            }
        });
    }


    // ******************************************************
    // HELPER FUNCTIONS
    // ******************************************************

    private void createAlertDialog() {
        // Set up alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderSummaryActivity.this);
        builder.setTitle(getString(R.string.login_fail_alert_title));
        builder.setMessage(alertMsg);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Close the alert dialog
                dialog.cancel();
            }
        });

        // Create and show the alert dialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void setUpTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Format time and set it in TextView
                SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");

                // Get date from time in TimePicker (assumed to be the same day)
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date date = calendar.getTime();

                textViewPickupTime.setText(displayFormat.format(date));

                // Convert to timestamp and set field in order
                Timestamp requestedPickUpTime = new Timestamp(date.getTime());
                order.setRequestedPickUpTime(requestedPickUpTime);
                Log.d(TAG, requestedPickUpTime.toString());
                Log.d(TAG, order.toString());

                // Make button invisible and TextView visible
                btnSetPickupTime.setVisibility(View.GONE);
                textViewPickupTime.setVisibility(View.VISIBLE);

                // Enable finish order button
                btnConfirm.setEnabled(true);
            }
        };

        // Time Picker Dialog
        timePicker = new TimePickerDialog(context, R.style.MyTimePickerDialogStyle, onTimeSetListener, hour, minutes, false);

        timePicker.show();
    }
}
