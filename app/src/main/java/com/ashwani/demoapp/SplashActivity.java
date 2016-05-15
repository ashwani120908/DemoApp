package com.ashwani.demoapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.ashwani.Helpers.AppConstants;
import com.ashwani.Helpers.AppController;
import com.ashwani.Helpers.AppPreferences;
import com.ashwani.Helpers.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kenBurnsSplash.KenBurnsView;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2500;
    private KenBurnsView mKenBurns;
    ProgressBar progressBar;
    AppPreferences appPreferences;
    String TAG = "SplashActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appPreferences = new AppPreferences(SplashActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.bringToFront();

        setAnimation();
        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mKenBurns.setImageResource(R.drawable.splash_background);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                attemptLogin();
            }
        }, SPLASH_TIME_OUT);
    }

    private void setAnimation() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(findViewById(R.id.welcome_text), "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(findViewById(R.id.welcome_text), "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(findViewById(R.id.welcome_text), "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();

        findViewById(R.id.imagelogo).setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        findViewById(R.id.imagelogo).startAnimation(anim);
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
                VolleyLog.d(TAG, "Error: " + error.toString());
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppConstants.PARAM_USERNAME_KEY, appPreferences.getUserUniqueNamePref());
                params.put(AppConstants.PARAM_PASSWORD_KEY, appPreferences.getUserPasswordPref());
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
                    startActivity(new Intent(SplashActivity.this, BaseActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
