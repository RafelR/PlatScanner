package com.speedhack.plat.platscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.speedhack.plat.platscanner.interfaces.FirebaseCallBacks;
import com.speedhack.plat.platscanner.manager.FirebaseManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverActivity extends AppCompatActivity implements FirebaseCallBacks {
    CircleImageView imgProfile;
    TextView tvPlat, tvBrand, tvKeterangan, tvFullname, tvRaby, tvRatedBy, tvRato, tvRatedTo;
    Button btComment, btScan;
    RatingBar ratingBar;
    FirebaseManager manager;
    String plat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initViews();
        final Intent intent = getIntent();
        plat = intent.getStringExtra("plat");

        tvFullname.setText("Wina");
        tvPlat.setText(plat);
        tvRatedBy.setText("17");
        tvRatedTo.setText("10");
        tvBrand.setText("Honda Jazz");
        btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DriverActivity.this, CommentActivity.class));
            }
        });
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DriverActivity.this, RateActivity.class));
            }
        });
    }

    private void initViews() {
        tvFullname = findViewById(R.id.tv_fullname);
        tvKeterangan = findViewById(R.id.tv_keterangan);
        tvRaby = findViewById(R.id.tv_raby);
        tvBrand = findViewById(R.id.tv_brand);
        tvPlat = findViewById(R.id.tv_plat);
        tvRatedBy = findViewById(R.id.tv_rated_by);
        tvRato = findViewById(R.id.tv_rato);
        tvRatedTo = findViewById(R.id.tv_rated_to);
        btComment = findViewById(R.id.bt_to_comment);
        btScan = findViewById(R.id.bt_to_scan);
        ratingBar = findViewById(R.id.rating);
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot) {

    }
}
