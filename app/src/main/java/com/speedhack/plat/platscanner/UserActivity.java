package com.speedhack.plat.platscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.speedhack.plat.platscanner.interfaces.FirebaseCallBacks;
import com.speedhack.plat.platscanner.manager.FirebaseManager;
import com.speedhack.plat.platscanner.model.Komentar;
import com.speedhack.plat.platscanner.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity implements FirebaseCallBacks {
    CircleImageView imgProfile;
    TextView tvFullname, tvRaby, tvRatedBy, tvRato, tvRatedTo;
    Button btComment, btScan;
    RatingBar ratingBar;
    FirebaseManager manager;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViews();
        manager = new FirebaseManager(FirebaseAuth.getInstance().getUid(),this);
        manager.addListeners();

        btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, CommentActivity.class));
            }
        });

        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, OpenCamera.class));
            }
        });
    }

    private void initViews() {
        tvFullname = findViewById(R.id.tv_fullname);
        tvRaby = findViewById(R.id.tv_raby);
        tvRatedBy = findViewById(R.id.tv_rated_by);
        tvRato = findViewById(R.id.tv_rato);
        tvRatedTo = findViewById(R.id.tv_rated_to);
        btComment = findViewById(R.id.bt_to_comment);
        btScan = findViewById(R.id.bt_to_scan);
        ratingBar = findViewById(R.id.rating);
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        tvFullname.setText(user.getFullname());
        ArrayList<Komentar> comments = new ArrayList<>();
        comments = user.getComments();

        if (comments == null){
            tvRatedBy.setText("0");
            ratingBar.setRating(0);
        }else {
            String size = String.valueOf(comments.size());
            tvRatedBy.setText(size);
            float rating = 0;
            for (int i=0; i<comments.size();i++){
                rating += comments.get(i).getRating();
            }
            rating = rating/comments.size();
            ratingBar.setRating(rating);
        }
        ArrayList<Komentar> toComments = new ArrayList<>();
        toComments = user.getToComments();
        if (toComments == null){
            tvRatedTo.setText("0");
        }else {
            String size = String.valueOf(toComments.size());
            tvRatedTo.setText(size);
        }
    }
}
