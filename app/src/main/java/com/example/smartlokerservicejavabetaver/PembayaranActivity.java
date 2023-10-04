package com.example.smartlokerservicejavabetaver;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.ShippingAddress;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.corekit.models.snap.Authentication;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;
import java.util.UUID;

public class PembayaranActivity extends AppCompatActivity implements TransactionFinishedCallback {
    TextView mValuePembayaran;
    EditText mEmail, mNohp,mNama;
    Button mBayar,mbtnBack;
    Firebase mRef;
    String urlLoker, urlStatusPay;
    private static final String SERVER_KEY = "SB-Mid-server-hxvnYFC_bH_PVKRz2ibI91gJ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran2);
        mValuePembayaran = findViewById(R.id.hargaview);
        mbtnBack         = findViewById(R.id.btn_back);
        mEmail           = findViewById(R.id.email);
        mBayar           = findViewById(R.id.buttonbayar);
        mNohp           = findViewById(R.id.nohp);
        mNama          = findViewById(R.id.namapengguna);

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

        mBayar.setOnClickListener(v -> {
            String email    = mEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Masukan Email");
                }
                else {
                    initMidtransSkd();

                    // Mendapatkan detail pembayaran dari input teks
                    String ID = generateRandomOrderId();
                    String itemName = "Loker";
                    int price = getIntent().getIntExtra("pembayaran", 0);
                    int quantity = 1;

                    // Membuat objek item detail
                    ItemDetails details = new ItemDetails(ID, (double) price, quantity, itemName);

                    // Membuat daftar item detail
                    ArrayList<ItemDetails> itemDetails = new ArrayList<>();
                    itemDetails.add(details);

                    // Membuat objek transaction request
                    TransactionRequest transactionRequest = new TransactionRequest(generateRandomOrderId(), price);
                    transactionRequest.setItemDetails(itemDetails);
                    transactionRequest.setCustomerDetails(customerDetails());

                    // Membuat objek credit card
                    CreditCard creditCard = new CreditCard();
                    creditCard.setSaveCard(false);
                    creditCard.setAuthentication(Authentication.AUTH_3DS);

                    // Mengatur credit card ke transaction request
                    transactionRequest.setCreditCard(creditCard);

                    // Memulai pembayaran menggunakan Midtrans
                    MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
                    MidtransSDK.getInstance().startPaymentUiFlow(PembayaranActivity.this, PaymentMethod.GO_PAY);
//                    MidtransSDK.getInstance().startPaymentUiFlow(PembayaranActivity.this, PaymentMethod.SHOPEEPAY);
                }
            });
        mbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(PembayaranActivity.this, Home.class);
                startActivity(back);
            }
        });
    }


    public CustomerDetails customerDetails() {
        CustomerDetails cd = new CustomerDetails();
        cd.setFirstName(mNama.getText().toString());
        cd.setLastName("Pengguna");
        cd.setEmail(mEmail.getText().toString());
        cd.setPhone(mNohp.getText().toString());
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setAddress("Depok");
        shippingAddress.setCity("Depok");
        shippingAddress.setPostalCode("16920");
        cd.setShippingAddress(shippingAddress);

        return cd;
    }

    private void initMidtransSkd() {
        SdkUIFlowBuilder.init()
                .setClientKey(BuildConfig.CLIENT_KEY)
                .setContext(PembayaranActivity.this)
                .setTransactionFinishedCallback(PembayaranActivity.this)
                .setMerchantBaseUrl(BuildConfig.BASE_URL)
                .enableLog(true)
                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
                .buildSDK();

    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        TransactionResponse response = result.getResponse();
        if (response != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction Finished. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    PembayaranActivity.this.mRef.child(urlStatusPay).setValue(1);
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending. ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed. ID: " + result.getResponse().getTransactionId() +
                            ". Message: " + result.getResponse().getStatusMessage(), Toast.LENGTH_LONG).show();
                    break;
            }
            String validationMessage = result.getResponse().getStatusMessage();
        } else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show();
            }
        }
        if (response != null && response.getStatusCode().equals("200")) {
            // Payment is successful
            String orderId = response.getOrderId();
            String transactionStatus = response.getTransactionStatus();

            if (transactionStatus.equalsIgnoreCase("success")) {
                // Payment via bank is successful
                PembayaranActivity.this.mRef.child(urlLoker).setValue(2);
            }
        } else {
            // Payment is failed
            Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show();
        }

    }
    private String generateRandomOrderId() {
        // Generate random UUID as order ID
        return UUID.randomUUID().toString();
    }

}