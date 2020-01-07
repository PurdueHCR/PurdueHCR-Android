/**
 * Created By Brian Johncox on 7/14/19
 *
 * This class is designed to hold all information about Listeners for the app. At initialization of
 * the app, the listeners will be set to call the run all method for that specific collection or
 * record. As a page is loaded, if needed it can add a ListenerCallback to the queue to be called.
 * Please make sure that when the page is killed, the Callback is removed from the active list.
 */


package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners.FirebaseCollectionListener;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners.FirestoreDocumentListener;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SnapshotInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FirebaseListenerUtil {

    private Context context;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected CacheManager cacheManager;
    static FirebaseListenerUtil fluListener;
    private FirebaseCollectionListener userPointLogListener;
    private FirebaseCollectionListener rhpNotificationListener;
    private FirestoreDocumentListener pointLogListener;
    private FirestoreDocumentListener systemPreferenceListener;

    /**
     * Get the instance of the FirebaseUtilListener to be used in the app.
     * @return
     */
    public static FirebaseListenerUtil getInstance(Context c){
        if(fluListener == null){
            fluListener = new FirebaseListenerUtil(c);
        }
        return fluListener;
    }

    public FirebaseListenerUtil(Context c){
        context = c;
        cacheManager = CacheManager.getInstance(context);
        createPersistantListeners();
    }

    /**
     * Create all listeners that are designed to persist for the lifetime of the app
     */
    private void createPersistantListeners(){
        createUserPointLogListener();
        createSystemPreferencesListener();
        if(cacheManager.getPermissionLevel() == 1){
            //Create RHP only listeners
            createRHPNotificationListener();
        }
    }

    /*------------------------USER POINT LOG LISTENER---------------------------------------------*/

    /**
     * Create the listener that updates when a point log for this user updates
     */
    private void createUserPointLogListener(){
        Query userPointLogQuery = db.collection("House")
                .document(cacheManager.getHouseName())
                .collection("Points")
                .whereEqualTo("ResidentId", cacheManager.getUserId());
        SnapshotInterface si = new SnapshotInterface() {
            @Override
            public void handleQuerySnapshots(QuerySnapshot queryDocumentSnapshots, Exception e) {
                if( e == null){
                    List<PointLog> userLogs = new ArrayList<>();
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        userLogs.add(new PointLog(doc.getId(),doc.getData(),context));
                    }
                    Collections.sort(userLogs);
                    cacheManager.setPersonalPointLogs(userLogs);
                }
            }
        };
        userPointLogListener = new FirebaseCollectionListener(context,userPointLogQuery,si);
    }

    public FirebaseCollectionListener getUserPointLogListener(){
        return userPointLogListener;
    }

    /*------------------------RHP NOTIFICATION LISTENER-------------------------------------------*/

    private void createRHPNotificationListener(){
        Query rhpNotificationQuery = db.collection("House")
                .document(cacheManager.getHouseName())
                .collection("Points")
                .whereGreaterThan("RHPNotifications", 0);
        SnapshotInterface si = new SnapshotInterface() {
            @Override
            public void handleQuerySnapshots(QuerySnapshot queryDocumentSnapshots, Exception e) {
                cacheManager.setNotificationCount(queryDocumentSnapshots.size());
            }
        };
        rhpNotificationListener = new FirebaseCollectionListener(context,rhpNotificationQuery,si);
    }

    public FirebaseCollectionListener getRHPNotificationListener(){
        return this.rhpNotificationListener;
    }

    /*--------------------------INDIVIDUAL POINT LOG LISTENER-------------------------------------*/

    /**
     * Create a listener for a specific point log document. Please kill this listener after you are done with it.
     * @param log The Point Log for which to listen for updates
     */
    public void createPointLogListener(PointLog log){
        if(pointLogListener != null){
            pointLogListener.killListener();
            pointLogListener = null;
        }
        DocumentReference documentReference = db.collection("House")
                .document(cacheManager.getHouseName())
                .collection("Points")
                .document(log.getLogID());
        SnapshotInterface si = new SnapshotInterface() {
            @Override
            public void handleDocumentSnapshot(DocumentSnapshot documentSnapshot, Exception e) {
                    if( e == null){
                        PointLog relatedLog = (PointLog) pointLogListener.getUpdatingObject();
                        relatedLog.updateValues(new PointLog(documentSnapshot.getId(),documentSnapshot.getData(),context));
                    }
            }
        };
        pointLogListener = new FirestoreDocumentListener(context,documentReference,si,log);
    }

    public FirestoreDocumentListener getPointLogListener() {
        return pointLogListener;
    }

    /*---------------------------SYSTEM PREFERENCES LISTENER--------------------------------------*/

    public void createSystemPreferencesListener(){
        SystemPreferences systemPreferences = cacheManager.getSystemPreferences();
        DocumentReference documentReference = db.collection("SystemPreferences")
                .document("Preferences");
        SnapshotInterface si = new SnapshotInterface() {
            @Override
            public void handleDocumentSnapshot(DocumentSnapshot documentSnapshot, Exception e) {
                if(e == null){
                    SystemPreferences sp = (SystemPreferences) systemPreferenceListener.getUpdatingObject();
                    sp.updateValues(documentSnapshot.getData());
                }
            }
        };
        systemPreferenceListener = new FirestoreDocumentListener(context,documentReference,si,systemPreferences);
    }

    public FirestoreDocumentListener getSystemPreferenceListener(){
        return systemPreferenceListener;
    }
}
