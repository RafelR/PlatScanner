package com.speedhack.plat.platscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {

    private ArrayList<String> platNomor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        platNomor = new ArrayList<String>();
//        System.out.println("TEST2");
        platNomor = (ArrayList<String>) getIntent().getSerializableExtra("plat");
        for(String plat:platNomor){
            System.out.println("Plat: " + plat);
        }
    }
}
