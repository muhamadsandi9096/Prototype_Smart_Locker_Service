package com.example.smartlokerservicejavabetaver;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PembayaranRazorPay extends AppCompatActivity implements PaymentResultListener {
    TextView mValuePembayaran;
    EditText mEmail, mNohp,mNama;
    Button mBayar;
    Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_razor_pay);
        mValuePembayaran = findViewById(R.id.hargaview);
        mEmail           = findViewById(R.id.email);
        mBayar           = findViewById(R.id.buttonbayar);
        mNohp           = findViewById(R.id.nohp);
        mNama          = findViewById(R.id.namapengguna);

        String urlbase = "https://sanslokerpenyimpananumun-default-rtdb.asia-southeast1.firebasedatabase.app/";
        String ambillokasi = getIntent().getStringExtra("Lokasi");
        String noLoker = getIntent().getStringExtra("Loker");
        String urlLokasi = urlbase + ambillokasi;
        String urlLoker = noLoker + "/statusKotak";
        mRef = new Firebase(urlLokasi);

        int harga = getIntent().getIntExtra("pembayaran", 0);

        String viewHarga = "Rp " + harga;
        mValuePembayaran.setText(viewHarga);
        mBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PembayaranRazorPay.this.mRef.child(urlLoker).setValue(2);
                startPayment();
            }
        });
        // Inisialisasi Razorpay
        Checkout.preload(getApplicationContext());
    }
    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_bPlvOQunDBwL1I"); // Ganti dengan Razorpay Key ID Anda

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Smart Loker Service"); // Nama toko Anda
            options.put("description", "Biaya Loker Penyimpanan");
//            options.put("image", "https://your-image-url.com/logo.png"); // URL logo toko Anda
            options.put("currency", "IDR"); // Kode mata uang
            options.put("amount", getIntent().getIntExtra("pembayaran", 0)); // Jumlah pembayaran dalam paise (contoh: 10000 = 100.00 INR)
            options.put("prefill.email", mEmail.getText().toString());
            options.put("prefill.contact",  mNohp.getText().toString());

            checkout.open(PembayaranRazorPay.this, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // Tindakan yang dilakukan saat pembayaran berhasil
        Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int code, String description) {
        // Tindakan yang dilakukan saat pembayaran gagal
        Toast.makeText(this, "Payment Error: " + description, Toast.LENGTH_SHORT).show();
    }

}