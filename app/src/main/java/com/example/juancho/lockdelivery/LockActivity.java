package com.example.juancho.lockdelivery;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LockActivity extends AppCompatActivity implements OnClickListener {

    private Button mBtLocker;
    private Button mBtDistance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mBtLocker = (Button) findViewById(R.id.btn_locker);
        mBtDistance = (Button) findViewById(R.id.btn_distance);

        mBtLocker.setOnClickListener(this);
        mBtDistance.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.btn_locker:
                page_Request();
                toggleBtn();
                break;
            case R.id.btn_distance:
                distanceBtn();
                break;
        }
    }

    //Calculate the distance
    private void distanceBtn(){
        final SweetAlertDialog boxEmpty =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("The Locker is Occupied!")
                .setContentText("There is a package in the locker!");
        boxEmpty.show();

    }
    // Toggle Lock button status
    private void toggleBtn( ){
        final SweetAlertDialog unlockSuccess =  new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Unlocked!")
                .setContentText("Locker Box Unlocked!");

        final SweetAlertDialog lockSuccess =  new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Locked!")
                .setContentText("Locker Box Locked!");

        if(mBtLocker.getText().toString() == "Unlock"){
            lockSuccess.show();
            mBtLocker.setText("Lock");
        }
        else{
            unlockSuccess.show();
            mBtLocker.setText("Unlock");
        }
    }

    // Toast a message with a button
    public void send_Request(View v) {
        Toast.makeText(getBaseContext(),"Hola", Toast.LENGTH_SHORT).show();
    }



    // Request to a page
    public void page_Request(){
        // Request with Volley
        final TextView mTextView = (TextView) findViewById(R.id.text_Request);

        // Initiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://juanjosesoriano.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }




}
