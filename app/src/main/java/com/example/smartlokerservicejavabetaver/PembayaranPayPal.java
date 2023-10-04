package com.example.smartlokerservicejavabetaver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlokerservicejavabetaver.PaypalConfirmation.PaymentDetail;
import com.firebase.client.Firebase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class PembayaranPayPal extends AppCompatActivity {

    TextView mValuePembayaran;
    Button mBayar;
    Firebase mRef;

    String amount = "";
    String urlLoker, urlStatusPay;

    private static final int PAYPAL_REQUEST_CODE = 123;
    private static final String PAYPAL_CLIENT_ID = "AWdTdsHJzlOKCOHNyJBjDjb79TYXKAAHvkvIsUlSwSUq3w1X_7JIcS8hPrYcZQajYWqBFTY1rOOYL80V";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID);

    private double kursDollarToRupiah = 14500;
    double dollar;

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_pay_pal);

        //Start Paypal Service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        mValuePembayaran = findViewById(R.id.hargaview);
        mBayar           = findViewById(R.id.buttonbayar);

        String urlbase = "https://sanslokerpenyimpananumun-default-rtdb.asia-southeast1.firebasedatabase.app/";
        String ambillokasi = getIntent().getStringExtra("Lokasi");
        String noLoker = getIntent().getStringExtra("Loker");
        String urlLokasi = urlbase + ambillokasi;
        urlLoker = noLoker + "/statusKotak";
        urlStatusPay = noLoker + "/statusPembayaran";
        mRef = new Firebase(urlLokasi);

        int harga = getIntent().getIntExtra("pembayaran", 0);

        String viewHarga = "Rp " + harga;
        mValuePembayaran.setText(viewHarga);

        mBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PembayaranPayPal.this.mRef.child(urlLoker).setValue(2);
                PembayaranPayPal.this.mRef.child(urlStatusPay).setValue(1);
                processPayment();
            }
        });
    }

    private void processPayment() {
        amount = String.valueOf(getIntent().getIntExtra("pembayaran", 0));
        int amountValue = getIntent().getIntExtra("pembayaran", 0);
        dollar = amountValue / kursDollarToRupiah;
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(dollar)), "USD", "Pembayaran dengan PayPal",
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(PembayaranPayPal.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        PembayaranPayPal.this.mRef.child(urlLoker).setValue(2);
                        startActivity(new Intent(this, PaymentDetail.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
            }
        }

    }

}