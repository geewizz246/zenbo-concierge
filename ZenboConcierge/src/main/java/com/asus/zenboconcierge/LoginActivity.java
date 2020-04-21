package com.asus.zenboconcierge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.asus.robotframework.API.RobotFace;
import com.asus.zenboconcierge.dtos.Customer;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.zenboconcierge.utils.HttpUtils;
import com.github.ybq.android.spinkit.style.Circle;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends RobotActivity {
    private static final String TAG = "LoginActivity";
    public final static String DOMAIN = "39F7332D02E04D9FAE24D185B009EE01";
    public final static String PLAN = "launchZenboConcierge";

    private final Context context = LoginActivity.this;
    private String alertMsg = null;

    private EditText editTextEmail, editTextPin;
    private Button btnLogin;
    private ProgressBar progress;

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

    public LoginActivity() {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, TAG + " created");

        // Get views
        editTextEmail = findViewById(R.id.edittext_login_email);
        editTextPin = findViewById(R.id.edittext_login_pin);
        btnLogin = findViewById(R.id.btn_login);
        progress = findViewById(R.id.progress);

        // Set default values for testing
        editTextEmail.setText("test@domain.com");
        editTextPin.setText("1111");

        // By default, button disabled if editTextEmail is empty
        if (editTextEmail.getText().length() == 0) {
            btnLogin.setEnabled(false);
        }


        // Enable button if input for email is not empty
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    btnLogin.setEnabled(false);
                } else {
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, TAG + " resumed");

        // Close face
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);

        // Jump to dialog domain plan
        // Dunno if this works
        robotAPI.robot.jumpToPlan(DOMAIN, PLAN);

//        robotAPI.robot.speak(getString(R.string.zenbo_speak_login_prompt));
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

    public void onLogin(View view) {
        // Disable views to prevent input
        btnLogin.setEnabled(false);
        editTextEmail.setEnabled(false);
        editTextPin.setEnabled(false);

        // Get the input values
        String email = editTextEmail.getText().toString();
        String pin = editTextPin.getText().toString();
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("pin", pin);

        HttpUtils.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Customer customer = new Customer();

                try {
                    customer.setEmail(response.getString("email"));
                    customer.setFirstName(response.getString("firstName"));
                    customer.setLastName(response.getString("lastName"));
                    customer.setPhone(response.getString("phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Stop progress bar
                progress.setVisibility(View.GONE);

                // Create the menu intent and attach the customer object
                Intent menuIntent = new Intent(context, MenuActivity.class);
                menuIntent.putExtra("customer", customer);
                context.startActivity(menuIntent);
                finish();
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

                // Stop progress bar
                progress.setVisibility(View.GONE);

                createAlertDialog();

                // Make Zenbo speak
                robotAPI.robot.speak(getString(R.string.zenbo_speak_login_fail));

                // Enable views again
                btnLogin.setEnabled(true);
                editTextEmail.setEnabled(true);
                editTextPin.setEnabled(true);
            }
        });

        // Set progress bar
        progress.setVisibility(View.VISIBLE);
        Circle circleProgress = new Circle();
        progress.setIndeterminateDrawable(circleProgress);
    }



    // ******************************************************
    // HELPER FUNCTIONS
    // ******************************************************

    private void createAlertDialog() {
        // Set up alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
