package com.speedhack.plat.platscanner.manager;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.speedhack.plat.platscanner.interfaces.FirebaseCallBacks;
import com.speedhack.plat.platscanner.model.User;

public class FirebaseManager implements ValueEventListener {
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseCallBacks callBacks;
    private String id;
    private String plat;

    public FirebaseManager(String id, FirebaseCallBacks callBacks){
        this.id = id;
        this.database = FirebaseDatabase.getInstance();
        this.ref = this.database.getReference("users").child(id);
        this.callBacks = callBacks;
        ref.keepSynced(true);
    }
    public FirebaseManager(String id){
        this.id = id;
        this.database = FirebaseDatabase.getInstance();
        this.ref = this.database.getReference("users").child(id);
    }

    public FirebaseManager(String plat, int a){
        this.plat = plat;
        this.database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        callBacks.onDataChanged(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }

    public void addListeners(){
        ref.addValueEventListener(this);
    }

    public void onCreateUser(String fullname, String email, String dateBirth, String gender, String phoneNumber, String brand, String plat){
        User user = new User(fullname, email, dateBirth, gender, phoneNumber, brand, plat);
        ref.setValue(user);
    }
}
