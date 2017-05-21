package com.example.juancho.lockdelivery;

/**
 * Created by Juancho on 3/17/2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.design.widget.TextInputLayout;
import android.view.View.OnClickListener;

// validation text and mail
import android.text.TextUtils;
import android.util.Patterns;

// User Model
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juancho.lockdelivery.model.User;
import com.fasterxml.jackson.core.JsonParser;

// Print messages
import android.widget.Toast;

// HashMaps
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Json web Token
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button   mBtRegister;
    private TextView mTvLogin;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // initialize elements
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtRegister = (Button) findViewById(R.id.btn_register);
        mTvLogin = (TextView) findViewById(R.id.tv_login);
        mTiName = (TextInputLayout) findViewById(R.id.ti_name);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mProgressbar = (ProgressBar) findViewById(R.id.progress);

        mBtRegister.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.btn_register:
                register();
                break;
            case R.id.tv_login:
                goToLogin();
                break;
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

    // validate is the mail is good with the pattern
    public static boolean validateEmail(String string) {

        if (TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches()) {

            return false;

        } else {

            return  true;
        }
    }
    // initialize the error validation
    private void setError() {

        mTiName.setError(null);
        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    // send User
    // Request to a page
    public void registerProcess(final User user){
        // Request with Volley
        final TextView mTextView = (TextView) findViewById(R.id.text_Request);

        // Initiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://juanjosesoriano.com/register";

        // construct parameters

        String msg = "|" + user.getName() + "|" + user.getEmail() + "|" + user.getPassword();


        String compactJws =  Jwts.builder()
                .setSubject(msg)
                .signWith(SignatureAlgorithm.HS256, "keytest123")
                .compact();
        Map<String, String> params = new HashMap<>();
        params.put("encode", compactJws);

        // Success SWAL
       final SweetAlertDialog registerSuccess =  new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Good job!")
                                                .setContentText("User Successfuly registered!");

        final  SweetAlertDialog registerError = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("It wasn't possible to create the new user");

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mProgressbar.setVisibility(View.INVISIBLE);
                        try {
                            Log.d("response", response.getString("message"));
                            // add sweet alert success and redirect to login
                            if (response.getInt("status") == 201){
                                registerSuccess.show();
                                new CountDownTimer(1500, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onFinish() {
                                        // TODO Auto-generated method stub

                                        registerSuccess.dismiss();
                                        goToLogin();
                                    }
                                }.start();
                            }
                            else {
                                registerError.show();
                            }

                        } catch (JSONException e){
                            Log.d("error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressbar.setVisibility(View.INVISIBLE);
                        registerError.show();
                        // TODO Auto-generated method stub

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsObjRequest);

    }


    private void register(){
        setError();

        String name = mEtName.getText().toString();
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        int err = 0;

        if (!validateFields(name)) {

            err++;
            mTiName.setError("Name should not be empty !");
        }

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Email should be valid !");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);

            mProgressbar.setVisibility(View.VISIBLE);
            /*SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();*/
            registerProcess(user);

        } else {
            Toast.makeText(getBaseContext(), "Enter Valid Data!", Toast.LENGTH_SHORT).show();
        }
    }

    // go to Login function
    private void goToLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


}
