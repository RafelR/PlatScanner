package com.speedhack.plat.platscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.speedhack.plat.platscanner.adapter.platAdapter;

import java.util.ArrayList;

public class ChooseActivity extends AppCompatActivity {

    private ArrayList<String> platNomor;
    RecyclerView rv1;
    platAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        platNomor = new ArrayList<String>();
        platNomor = (ArrayList<String>) getIntent().getSerializableExtra("plat");

        rv1 = findViewById(R.id.rv1);
        linearLayoutManager = new LinearLayoutManager(this);
        rv1.setLayoutManager(linearLayoutManager);
        rv1.setHasFixedSize(true);
        adapter = new platAdapter(platNomor,this);
        rv1.setAdapter(adapter);
    }
}
