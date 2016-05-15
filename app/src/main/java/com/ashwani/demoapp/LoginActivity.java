package com.ashwani.demoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ashwani.Helpers.AppConstants;
import com.ashwani.Helpers.AppController;
import com.ashwani.Helpers.AppPreferences;
import com.ashwani.Helpers.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    EditText userNameEt, passwordEt;
    String userNameStr, passwordStr;
    ProgressBar progressBar;
    AppPreferences appPreferences;

    String TAG = "LoginActivitasdfsdy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        setupComponents();
        addListeners();

    }

    private void initComponents() {
        loginBtn = (Button) findViewById(R.id.btn_login);
        userNameEt = (EditText) findViewById(R.id.username);
        passwordEt = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        appPreferences = new AppPreferences(LoginActivity.this);

    }

    private void setupComponents() {
        progressBar.bringToFront();
    }

    private void addListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateLogin();
            }
        });

    }

    private void validateLogin() {
        userNameStr = userNameEt.getText().toString().trim();
        passwordStr = passwordEt.getText().toString().trim();
        if (userNameStr.equals("")) {
            userNameEt.setError("Username required");
        } else if (passwordStr.equals("")) {
            passwordEt.setError("Password required");
        } else if (passwordStr.length() < 7) {
            passwordEt.setError("Enter a valid password");
        } else {
            if (!Utils.isNetworkAvailable(LoginActivity.this)) {
                Snackbar snackbar = Snackbar.make(userNameEt, "No Internet Connection!", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                validateLogin();
                            }
                        });
                snackbar.show();
            } else {
                loginUIChange();
                attemptLogin();
            }
        }
    }

    private void attemptLogin() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConstants.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                parseJson(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loginUIRevert();
                VolleyLog.d(TAG, "Error: " + error.toString());
                Utils.showErrorDialog("Error", getResources().getString(R.string.error_message), LoginActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppConstants.PARAM_USERNAME_KEY, userNameStr);
                params.put(AppConstants.PARAM_PASSWORD_KEY, passwordStr);
                params.put(AppConstants.PARAM_DEVICE_ID_KEY, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, TAG);

    }

    private void parseJson(String response) {
        try {
            JSONObject responseObj = new JSONObject(response);
            if (responseObj.has(AppConstants.RESPONSE_STATUS_KEY)) {
                if (responseObj.getBoolean(AppConstants.RESPONSE_STATUS_KEY)) {
                    JSONObject userDataObj = responseObj.getJSONObject(AppConstants.RESPONSE_USER_DATA_KEY);
                    appPreferences.setUserUniqueNamePref(userNameStr);
                    appPreferences.setUserPasswordPref(passwordStr);
                    startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                    finish();
                } else {
                    loginUIRevert();
                    Utils.showErrorDialog("Error", responseObj.getString("error"), LoginActivity.this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginUIChange() {
        loginBtn.setText("");
        progressBar.setVisibility(View.VISIBLE);
        userNameEt.setEnabled(false);
        passwordEt.setEnabled(false);
    }

    private void loginUIRevert() {
        loginBtn.setText("LOGIN");
        progressBar.setVisibility(View.INVISIBLE);
        userNameEt.setEnabled(true);
        passwordEt.setEnabled(true);
    }
}
