package com.asus.zenboconcierge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.zenboconcierge.dtos.*;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class OrderSuccessActivity extends RobotActivity {
    private static final String TAG = "OrderSuccessActivity";

    private Customer customer;
    private Order order;

    private Context context = OrderSuccessActivity.this;

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

    public OrderSuccessActivity() {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        Intent intent = getIntent();
        customer = intent.getParcelableExtra("customer");
        order = intent.getParcelableExtra("order");

        TextView textViewOrderID = findViewById(R.id.textview_order_success_order_id);
        textViewOrderID.setText(String.format("ORDER ID: %d", order.getOrderId()));

        SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
        TextView textViewPickupTime = findViewById(R.id.textview_order_success_pickup_time);
        textViewPickupTime.setText("Pickup @ " + displayFormat.format(order.getRequestedPickUpTime()));

        Button btnFinish = findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        robotAPI.robot.speak(String.format(getString(R.string.zenbo_speak_order_success), customer.getFirstName()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, TAG + " resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, TAG + " paused");
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
}
