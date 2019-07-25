package com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners;

import android.content.Context;

import com.google.firebase.firestore.Query;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SnapshotInterface;

public class FirebaseCollectionListener extends FirebaseListener {

    public FirebaseCollectionListener(Context c, Query query, SnapshotInterface si){
        super(c, si);
        listener = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(si != null){
                si.handleQuerySnapshots(queryDocumentSnapshots, e);
            }
            handleCallbacks();
        });
    }
}
