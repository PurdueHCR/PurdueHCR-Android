package com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners;

import android.content.Context;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SnapshotInterface;

import javax.annotation.Nullable;

public class FirestoreDocumentListener extends FirebaseListener {

    private DocumentReference documentReference;
    private Object updatingObject;

    public FirestoreDocumentListener(Context c, DocumentReference documentReference, SnapshotInterface si, Object updatingObject){
        super(c, si);
        this.updatingObject = updatingObject;
        this.documentReference = documentReference;
        listener = this.documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if(si != null){
                si.handleDocumentSnapshot(documentSnapshot,e);
            }
            handleCallbacks();
        });
    }

    public Object getUpdatingObject() {
        return updatingObject;
    }
}
