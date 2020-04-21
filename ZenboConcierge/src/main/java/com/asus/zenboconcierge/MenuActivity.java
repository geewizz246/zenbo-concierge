package com.asus.zenboconcierge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.zenboconcierge.dtos.*;

import com.asus.zenboconcierge.utils.FoodItemGridAdapter;
import com.asus.zenboconcierge.utils.HttpUtils;
import com.asus.zenboconcierge.utils.OrderPreviewListAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MenuActivity extends RobotActivity {
    private static final String TAG = "MenuActivity";
    private static Boolean isMenuActive = false;

    private Customer customer;
    private Order order;
    private final List<FoodItem> foodItemList = new ArrayList<>();
    private final List<OrderItem> orderItemList = new ArrayList<>();
    private final Context context = MenuActivity.this;
    private String alertMsg = null;

    private AlertDialog alertDialog;
    private Button btnCheckout;
    private Button btnCancel;

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

    public MenuActivity() { super(robotCallback, robotListenCallback); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.d(TAG, TAG + " created");

        // Get the intent that started this activity and extract the customer
        Intent intent = getIntent();
        customer = intent.getParcelableExtra("customer");

        setUpMenuAndOrder();
        registerButtons();

        // Disable the checkout button
        btnCheckout.setEnabled(false);

        // Zenbo Greeting
        robotAPI.robot.speak(String.format(getString(R.string.zenbo_speak_menu_greeting), customer.getFirstName()));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("customer", customer);
        outState.putParcelable("order", order);
        outState.putParcelableArrayList("orderItems", (ArrayList<? extends Parcelable>) orderItemList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, TAG + " resumed");
        isMenuActive = true;

        enableButtons();

        if (order != null) {
            if (order.getOrderAborted() && !order.isOrderComplete) {
                // Assume the order is still being performed
                order.setOrderAborted(false);
                Log.d(TAG, "Order " + order.getOrderId() + " not aborted");
                // Send an update to the server
                saveOrder();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, TAG + " paused");

        if (order != null && isMenuActive) {
            if (!order.getOrderAborted() && !order.isOrderComplete) {
                // Assume the order was aborted
                order.setOrderAborted(true);
                Log.d(TAG, "Order " + order.getOrderId() + " aborted");
                // Send an update to the server
                saveOrder();
            }
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

    @Override
    public void onBackPressed() {
        cancelOrder();
    }

    private void setUpMenuAndOrder() {
        // Get food items
        HttpUtils.get("items", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        FoodItem item = new FoodItem(obj);
                        foodItemList.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setUpMenuItemsGridView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    alertMsg = errorResponse != null
                            ? errorResponse.get("message").toString()
                            : "Connection timeout occurred";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createAlertDialog();
            }
        });

        // Initialise the order
        JSONObject orderParams = new JSONObject();
        StringEntity orderEntity = null;
        final Date today = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss.SSSZ", Locale.ENGLISH);
        try {
            orderParams.put("requestedPickUpTime", dateFormat.format(today));
            orderParams.put("orderDate", dateFormat.format(today));
            JSONObject jsonCust = new JSONObject();
            jsonCust.put("email", customer.getEmail());
            orderParams.put("customer", jsonCust);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            orderEntity  = new StringEntity(orderParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.post(this, "order/create", orderEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                order = new Order(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    alertMsg = errorResponse != null
                            ? errorResponse.get("message").toString()
                            : "Connection timeout occurred";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createAlertDialog();
            }
        });
    }

    private void setUpMenuItemsGridView() {
        GridView gridViewMenuItems = findViewById(R.id.gridview_item_menu);
        final FoodItemGridAdapter foodItemAdapter = new FoodItemGridAdapter(this, foodItemList);
        gridViewMenuItems.setAdapter(foodItemAdapter);

        gridViewMenuItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                FoodItem foodItem = foodItemAdapter.getItem(pos);

                // Create the OrderItem
                OrderItem orderItem = new OrderItem(order.getOrderId(), foodItem, 1);

                // Add it to the list
                if (!orderItemList.contains(orderItem)) {
                    orderItemList.add(orderItem);
                }
                setUpOrderPreviewListView();
                updateOrder();
            }
        });
    }

    private void setUpOrderPreviewListView() {
        ListView listViewOrderPreview = findViewById(R.id.listview_order_preview);
        listViewOrderPreview.setVisibility(View.VISIBLE);
        findViewById(R.id.textview_order_preview_placeholder).setVisibility(View.GONE);

        final OrderPreviewListAdapter adapter = new OrderPreviewListAdapter(MenuActivity.this, R.layout.list_item_order_preview, orderItemList);
        listViewOrderPreview.setAdapter(adapter);
    }

    public void updateOrder() {
        order.setOrderItems(orderItemList);

        // Update total
        TextView textViewTotal = findViewById(R.id.textview_order_preview_total);
        textViewTotal.setText(String.format("$ %.2f", order.getTotal()));

        enableButtons();
    }

    private void saveOrder() {
        Log.d(TAG, order.toString());
        // Disable buttons to prevent double submission
        btnCheckout.setEnabled(false);
        btnCancel.setEnabled(false);

        StringEntity orderEntity = null;
        String url = "order/update/" + order.getOrderId();

        String strOrder = StringEscapeUtils.unescapeJava(order.toString());
        try {
            orderEntity  = new StringEntity(strOrder);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.put(MenuActivity.this, url, orderEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
                order = new Order(response);

                enableButtons();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, errorResponse.toString());

                enableButtons();
            }
        });
    }

    private void showOrderSummary() {
        // Set menu to be inactive
        isMenuActive = false;

        // Create the order summary intent and attach the customer and order
        Intent orderSummaryIntent = new Intent(context, OrderSummaryActivity.class);
        orderSummaryIntent.putExtra("customer", customer);
        orderSummaryIntent.putExtra("finalOrder", order);
        context.startActivity(orderSummaryIntent);
    }

    private void registerButtons() {
        btnCheckout = findViewById(R.id.btn_order_checkout);
        btnCancel = findViewById(R.id.btn_order_cancel);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Submit order
                saveOrder();
                // Go to OrderSummaryActivity
                showOrderSummary();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel order
                cancelOrder();
            }
        });
    }

    private void cancelOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this, R.style.MyAlertDialogStyle);

        builder.setTitle(getString(R.string.cancel_order_title));
        builder.setMessage(getString(R.string.cancel_order_message));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Record the order being aborted
                order.setOrderAborted(true);
                Log.d(TAG, "Order " + order.getOrderId() + " aborted");
                // Send an update to the server for this order
                saveOrder();
                // Close the app
                MenuActivity.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // Close the alert dialog
                dialog.cancel();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

        robotAPI.robot.speak(getString(R.string.zenbo_speak_cancel_warning));
    }



    // ******************************************************
    // HELPER FUNCTIONS
    // ******************************************************

    private void createAlertDialog() {
        // Set up alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this, R.style.MyAlertDialogStyle);
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

    private void enableButtons() {
        btnCancel.setEnabled(true);
        if (orderItemList.size() > 0) {
            btnCheckout.setEnabled(true);
        } else {
            btnCheckout.setEnabled(false);
        }
    }
}
