package com.example.smartlokerservicejavabetaver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.prefs.Preferences;

public class LokerActivity extends AppCompatActivity {

    ActionBar actionBar;
    CardView mLoker1, mLoker2, mLoker3, mLoker4, mLoker5, mLoker6, mLoker7, mLoker8, mLoker9, mLoker10, mLoker11, mLoker12;
    TextView mStartL1, mStartL2, mStartL3, mStartL4, mStartL5, mStartL6, mStartL7, mStartL8, mStartL9, mStartL10, mStartL11, mStartL12;
    TextView mJam, mLokasi;
    Firebase mRef;

    private static final String TAG = LokerActivity.class.getSimpleName();
    int Hnow = 0;
    private Preferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loker);

        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLokasi = findViewById(R.id.Lokasi);

        mLoker1 = findViewById(R.id.kotak_1);
        mLoker2 = findViewById(R.id.kotak_2);
        mLoker3 = findViewById(R.id.kotak_3);
        mLoker4 = findViewById(R.id.kotak_4);
        mLoker5 = findViewById(R.id.kotak_5);
        mLoker6 = findViewById(R.id.kotak_6);
        mLoker7 = findViewById(R.id.kotak_7);
        mLoker8 = findViewById(R.id.kotak_8);
        mLoker9 = findViewById(R.id.kotak_9);
        mLoker10 = findViewById(R.id.kotak_10);
        mLoker11 = findViewById(R.id.kotak_11);
        mLoker12 = findViewById(R.id.kotak_12);

        mStartL1 = findViewById(R.id.Loker1Mulai);
        mStartL2 = findViewById(R.id.Loker2Mulai);
        mStartL3 = findViewById(R.id.Loker3Mulai);
        mStartL4 = findViewById(R.id.Loker4Mulai);
        mStartL5 = findViewById(R.id.Loker5Mulai);
        mStartL6 = findViewById(R.id.Loker6Mulai);
        mStartL7 = findViewById(R.id.Loker7Mulai);
        mStartL8 = findViewById(R.id.Loker8Mulai);
        mStartL9 = findViewById(R.id.Loker9Mulai);
        mStartL10 = findViewById(R.id.Loker10Mulai);
        mStartL11 = findViewById(R.id.Loker11Mulai);
        mStartL12 = findViewById(R.id.Loker12Mulai);

        mJam = findViewById(R.id.jam);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                String dateformatted = format.format(date);
                mJam.setText(dateformatted);

                handler.postDelayed(this, 1000);
            }
        });

        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        Hnow = hours;

//        mJam.setText(hours + ":" + minutes + ":" + seconds );

        Firebase.setAndroidContext(this);
        String urlbase = "https://sanslokerpenyimpananumun-default-rtdb.asia-southeast1.firebasedatabase.app/";
        String ambillokasi = getIntent().getStringExtra("Lokasi");
        String urlLokasi = urlbase + ambillokasi;

        mLokasi.setText(ambillokasi);
        mRef = new Firebase(urlLokasi);

        mRef.child("Loker1/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL1.getText().toString();
                    mRef.child("Loker1/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL1.setText(String.valueOf(mTimeStart));
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {}
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker1/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker1/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    int mTotal = 0;
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker1");
                                            startActivity(bayar);
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(FirebaseError firebaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {}
                    });
                    //set kondisi kotak
                    mLoker1.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker1/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker1.setCardBackgroundColor(Color.YELLOW);
                            }
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {}
                    });
                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL1.getText().toString();
                    if (value != "") {
                        mStartL1.setText("");
                        LokerActivity.this.mRef.child("Loker1/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker1/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker1.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker1.setCardBackgroundColor(Color.LTGRAY);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        //Loker 2
        mRef.child("Loker2/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL2.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker2/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker2/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker2/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL2.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker2/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker2/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker2");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker2.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker2/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker2.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL2.getText().toString();
                    if (value != "") {
                        mStartL2.setText("");
                        LokerActivity.this.mRef.child("Loker2/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker2/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker2.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker2.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 3
        mRef.child("Loker3/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL3.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker3/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker3/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker3/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL3.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker3/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker3/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker3");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker3.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker3/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker3.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL3.getText().toString();
                    if (value != "") {
                        mStartL3.setText("");
                        LokerActivity.this.mRef.child("Loker3/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker3/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker3.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker3.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 4
        mRef.child("Loker4/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL4.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker4/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker4/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker4/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL4.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker4/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker4/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker4");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker4.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker4/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker4.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL4.getText().toString();
                    if (value != "") {
                        mStartL4.setText("");
                        LokerActivity.this.mRef.child("Loker4/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker4/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker4.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker4.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker5
        mRef.child("Loker5/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL5.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker5/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker5/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker5/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL5.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker5/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker5/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker5.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker5");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker5.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker5/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker5.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL5.getText().toString();
                    if (value != "") {
                        mStartL5.setText("");
                        LokerActivity.this.mRef.child("Loker5/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker5/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker5.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker5.setCardBackgroundColor(Color.LTGRAY);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //Loker 6
        mRef.child("Loker6/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL6.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker6/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker6/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker6/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL6.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker6/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker6/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker6.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker6");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker6.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker6/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker6.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL6.getText().toString();
                    if (value != "") {
                        mStartL6.setText("");
                        LokerActivity.this.mRef.child("Loker6/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker6/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker6.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker6.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 7
        mRef.child("Loker7/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL7.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker7/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker7/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker7/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL7.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker7/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker7/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker7.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker7");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker7.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker7/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker7.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL7.getText().toString();
                    if (value != "") {
                        mStartL7.setText("");
                        LokerActivity.this.mRef.child("Loker7/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker7/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker7.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker7.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 8
        mRef.child("Loker8/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL8.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker8/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker8/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker8/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL8.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker8/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker8/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker8.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker8");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker8.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker8/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker8.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL8.getText().toString();
                    if (value != "") {
                        mStartL8.setText("");
                        LokerActivity.this.mRef.child("Loker8/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker8/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker8.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker8.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 9
        mRef.child("Loker9/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL9.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker9/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker9/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker9/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL9.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker9/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker9/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker9.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker9");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker9.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker9/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker9.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL9.getText().toString();
                    if (value != "") {
                        mStartL9.setText("");
                        LokerActivity.this.mRef.child("Loker9/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker9/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker9.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker9.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 10
        mRef.child("Loker10/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL10.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker10/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker10/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker10/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL10.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker10/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker10/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker10.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker10");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker10.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker10/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker10.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL10.getText().toString();
                    if (value != "") {
                        mStartL10.setText("");
                        LokerActivity.this.mRef.child("Loker10/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker10/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker10.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker10.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 11
        mRef.child("Loker11/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL11.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker11/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker11/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker11/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL11.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker11/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker11/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker11.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker11");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker11.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker11/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker11.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL11.getText().toString();
                    if (value != "") {
                        mStartL11.setText("");
                        LokerActivity.this.mRef.child("Loker11/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker11/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker11.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker11.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //Loker 12
        mRef.child("Loker12/statusKotak").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int statuskotak1data = dataSnapshot.getValue(int.class);
                if (statuskotak1data == 1) {
                    //set waktu mulai
                    String value = mStartL12.getText().toString();
                    if (value == "") {
                        LokerActivity.this.mRef.child("Loker12/startHours").setValue(hours);
                        String TimeStart = hours + ":" + minutes;
                        LokerActivity.this.mRef.child("Loker12/startTime").setValue(TimeStart);
                    }
                    mRef.child("Loker12/startTime").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String mTimeStart = dataSnapshot.getValue(String.class);
                            mStartL12.setText(String.valueOf(mTimeStart));
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set Perhitungan Pembayaran
                    mRef.child("Loker12/startHours").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mstartHours = dataSnapshot.getValue(int.class);
                            mRef.child("Loker12/startday").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int mTotal = 0;
                                    int mstartday = dataSnapshot.getValue(int.class);
                                    if (mstartday == today) {
                                        if (Hnow >= mstartHours) {
                                            mTotal = (hours - mstartHours + 1) * 1000;
                                        }
                                    } else if (mstartday != today) {
                                        mTotal = 30000;
                                    }
                                    int Total = mTotal;

                                    mLoker12.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(LokerActivity.this, "click", Toast.LENGTH_SHORT).show();
                                            Intent bayar = new Intent(LokerActivity.this, PembayaranActivity.class);
                                            bayar.putExtra("pembayaran", Total);
                                            bayar.putExtra("Lokasi", ambillokasi);
                                            bayar.putExtra("Loker", "Loker12");
                                            startActivity(bayar);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                    //set kondisi kotak
                    mLoker12.setCardBackgroundColor(Color.RED);
                    //set kondisi ketika sudah melakukan pembayaran
                    mRef.child("Loker12/statusPembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int statusPembayaran = dataSnapshot.getValue(int.class);
                            if (statusPembayaran == 1) {
                                mLoker12.setCardBackgroundColor(Color.YELLOW);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                } else if (statuskotak1data == 2) {
                    //reset waktu mulai
                    String value = mStartL12.getText().toString();
                    if (value != "") {
                        mStartL12.setText("");
                        LokerActivity.this.mRef.child("Loker12/startHours").setValue(0);
                        LokerActivity.this.mRef.child("Loker12/startTime").setValue("");
                    }
                    //reset kondisi kotak
                    mLoker12.setCardBackgroundColor(Color.GREEN);
                } else if (statuskotak1data == 0) {
                    mLoker12.setCardBackgroundColor(Color.LTGRAY);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}