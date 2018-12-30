package com.speedhack.plat.platscanner.interfaces;

import com.google.firebase.database.DataSnapshot;

public interface FirebaseCallBacks {
    void onDataChanged(DataSnapshot dataSnapshot);
}
