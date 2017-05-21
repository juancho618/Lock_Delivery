package com.example.juancho.lockdelivery;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.design.widget.TextInputLayout;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by Juancho on 3/18/2017.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtLogin;
    private TextView mTvRegister;
    private TextView mTvForgotPassword;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize elements
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.btn_login);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mTvRegister = (TextView) findViewById(R.id.tv_register);
        mTvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);

        mBtLogin.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);



    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.tv_register:
                goToRegister();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    // should stop login activity and start register activity!
    private void goToRegister(){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    // login method
    private void login(){
        setError();
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        int err = 0;

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Email should be valid !");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            loginProcess(email,password);
            mProgressBar.setVisibility(View.VISIBLE);

        } else {
             Toast.makeText(getBaseContext(), "Enter Valid Data!", Toast.LENGTH_SHORT).show();
        }
    }

    // validate is the mail is good with the pattern
    public static boolean validateEmail(String string) {

        if (TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches()) {

            return false;

        } else {

            return  true;
        }
    }

    // validate if the input values are not empty
    public static boolean validateFields(String name){

        if (TextUtils.isEmpty(name)) {

            return false;

        } else {

            return true;
        }
    }

    private void loginProcess(String email, String password) {
        // Initiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://juanjosesoriano.com/login";

        // Enconde the messge toh JWT
        String msg = "|" + email + "|" + password;
        String compactJws =  Jwts.builder()
                .setSubject(msg)
                .signWith(SignatureAlgorithm.HS256, "keytest123")
                .compact();
        Map<String, String> params = new HashMap<>();
        params.put("encode", compactJws);

        // Login success message
        final SweetAlertDialog loginSuccess =  new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Logged in!")
                                                .setContentText("Success!");

        final  SweetAlertDialog loginError = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("Invalid email or password");

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        try {

                            Log.d("login", response.toString());
                            if (response.getInt("status") == 200){
                                loginSuccess.show();
                                new CountDownTimer(1500, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onFinish() {
                                        // TODO Auto-generated method stub

                                        loginSuccess.dismiss();
                                        goToLock();
                                    }
                                }.start();
                            }
                            else{
                                loginError.show();
                            }


                        } catch (JSONException e){
                            Log.d("error", e.toString());
                            loginError.show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        loginError.show();
                        // TODO Auto-generated method stub

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    private void setError() {

        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void goToLock(){
        Intent intent = new Intent(getApplicationContext(), LockActivity.class);
        startActivity(intent);
    }

}
