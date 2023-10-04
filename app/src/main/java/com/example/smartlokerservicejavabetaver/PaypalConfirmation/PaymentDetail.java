package com.example.smartlokerservicejavabetaver.PaypalConfirmation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlokerservicejavabetaver.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetail extends AppCompatActivity {
    TextView txtId, txtAmount, txtStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();
        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setTextColor(Integer.parseInt(response.getString("id")));
            txtStatus.setTextColor(Integer.parseInt(response.getString("state")));
            txtAmount.setTextColor(Integer.parseInt(response.getString(String.format("$%s", paymentAmount))));

        } catch (JSONException e) {
             e.printStackTrace();
        }

    }
}