package com.asus.zenboconcierge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.RobotUtil;
import com.asus.robotframework.API.SpeakConfig;
import com.asus.zenboconcierge.dtos.User;
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

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends RobotActivity {
    public static int currentApiVersion;
    private static final String TAG = "LoginActivity";
    public final static String DOMAIN = "4BF19BF00E604E61AC287ED125FB48C7";
    public final static String PLAN = "launchZenboConcierge";
    private static String STATE_MSG;
    private static LoginActivity thisActivity;

    // For testing
    private static TextView textViewResponse;
    private static ImageView imageViewAppLogo;

    private final Context context = LoginActivity.this;
    private String alertMsg = null;
    private int numRepetitionsLogin = 0;

    private EditText editTextEmail, editTextPin;
    private Button btnLogin;
    private ProgressBar progress;

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            Log.d(TAG, "WOAH from onresult");
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            if (state.getDescription().equals("ROBOT_CMD_STATE_FAILED")) {
                STATE_MSG = "I cannot access the dialog server!";
            }
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            Log.d(TAG, "WOAH from robotCallback.initComplete()");
            super.initComplete();
        }
    };

    public static final RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {
            Log.d(TAG, "Zenbo dialog complete");
            Log.d(TAG, s);
            Log.d(TAG, s1);
        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {
            // Testing purposes
            textViewResponse.setText(jsonObject.toString());
            imageViewAppLogo.setVisibility(View.GONE);
            textViewResponse.setVisibility(View.VISIBLE);
            Log.d(TAG, "robotListenCallback.onResult: " + jsonObject.toString());

            String sIntentionID = RobotUtil.queryListenResultJson(jsonObject, "IntentionId");
            Log.d(TAG, "IntentionID = " + sIntentionID);

            if (sIntentionID.equals("loginToApp")) {
                // Testing purposes
                textViewResponse.setText(textViewResponse.getText() + "\n\nLogin command registered");
                Log.d(TAG, "Login command registered");

                // Call login function
                thisActivity.btnLogin.callOnClick();
            }
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

        // Set static context
        thisActivity = LoginActivity.this;

        // Testing elements
        imageViewAppLogo = findViewById(R.id.imageview_app_logo);
        textViewResponse = findViewById(R.id.textview_test_dde_json_res);

        // Hide navigation bar
        currentApiVersion = Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
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

        // Greet user and wait for them to say "Login"
        SpeakConfig speakConfig = new SpeakConfig();
        speakConfig.timeout(30);    // 30s
        robotAPI.robot.speakAndListen(getString(R.string.zenbo_speak_login_prompt), speakConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (STATE_MSG != null) {
            robotAPI.robot.speak(STATE_MSG);
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

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void onLogin() {
        // Disable views to prevent input
        btnLogin.setEnabled(false);
        editTextEmail.setEnabled(false);
        editTextPin.setEnabled(false);

        // Get the input values
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("email", editTextEmail.getText().toString());
            loginData.put("pin", editTextPin.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity loginEntity = null;
        try {
            loginEntity = new StringEntity(loginData.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtils.post(this, "login", loginEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = new User();

                try {
                    user.setEmail(response.getString("email"));
                    user.setFirstName(response.getString("firstName"));
                    user.setLastName(response.getString("lastName"));
                    user.setPhone(response.getString("phone"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Stop progress bar
                progress.setVisibility(View.GONE);

                // Create the menu intent and attach the user object
                Intent menuIntent = new Intent(context, MenuActivity.class);
                menuIntent.putExtra("user", user);
                menuIntent.putExtra("numRepetitionsLogin", numRepetitionsLogin);
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

                // Increase number of repetitions at login page
                numRepetitionsLogin++;
                Log.d(TAG, "Login Repeats: " + numRepetitionsLogin);

                // Stop progress bar
                progress.setVisibility(View.GONE);

                createAlertDialog();

                // Inform user of error and prompt re-enter of login credentials
                SpeakConfig speakConfig = new SpeakConfig();
                speakConfig.timeout(30);    // 30s
                robotAPI.robot.speakAndListen(getString(R.string.zenbo_speak_login_fail), speakConfig);

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
