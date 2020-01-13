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
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Models.User;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners.FirebaseCollectionListener;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners.FirebaseListener;
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
    private FirebaseCollectionListener pointTypeListener;
    private FirestoreDocumentListener pointLogListener;
    private FirestoreDocumentListener systemPreferenceListener;
    private FirestoreDocumentListener userAccountListener;

    /**
     * Call this from the AppInitializationActivity to initialize the Listeners
     * @param c
     */
    public static void initializeListeners(Context c){
        FirebaseListenerUtil.getInstance(c);
    }

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
        createPointTypeListener();
        createUserAccountListener(cacheManager.getUser());
        if(cacheManager.getPermissionLevel() == UserPermissionLevel.RHP){
            //Create RHP only listeners
            createRHPNotificationListener();
        }
    }

    public void removeCallbacks(String key){
        if(userPointLogListener != null){
            userPointLogListener.removeCallback(key);
        }
        if(rhpNotificationListener != null){
            rhpNotificationListener.removeCallback(key);
        }
        if(pointTypeListener != null){
            pointTypeListener.removeCallback(key);
        }
        if(pointLogListener != null){
            pointLogListener.removeCallback(key);
        }
        if(systemPreferenceListener != null){
            systemPreferenceListener.removeCallback(key);
        }
        if(userAccountListener != null){
            userAccountListener.removeCallback(key);
        }
    }

    public void resetFirebaseListeners(){
        killUserAccountListener();
        killPointLogListener();
        killRHPNotificationListener();
        killUserPointLogListener();
        killSystemPreferencesListener();
        killPointTypeListener();
        fluListener = null;
    }

    /*--------------------------User Account LISTENER-------------------------------------*/


    public void createUserAccountListener(User user){
        if(userAccountListener != null){
            userAccountListener.killListener();
            userAccountListener = null;
        }
        DocumentReference documentReference = db.collection("Users")
                .document(cacheManager.getUserId());
        SnapshotInterface si = new SnapshotInterface() {
            @Override
            public void handleDocumentSnapshot(DocumentSnapshot documentSnapshot, Exception e) {
                if( e == null){
                    User user = (User) userAccountListener.getUpdatingObject();
                    user.setTotalPoints(((Long) documentSnapshot.getData().get(User.TOTAL_POINTS_KEY)).intValue());
                }
            }
        };
        userAccountListener = new FirestoreDocumentListener(context,documentReference,si,user);
    }

    public FirestoreDocumentListener getUserAccountListener() {
        return userAccountListener;
    }

    private void killUserAccountListener(){
        userAccountListener.killListener();
        userAccountListener = null;
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

    private void killUserPointLogListener(){
        userPointLogListener.killListener();
        userPointLogListener = null;
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
                if( e == null){
                    List<PointLog> notifiedLog = new ArrayList<>();
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        notifiedLog.add(new PointLog(doc.getId(),doc.getData(),context));
                    }
                    Collections.sort(notifiedLog);
                    cacheManager.setRHPNotificationLogs(notifiedLog);
                    cacheManager.setNotificationCount(queryDocumentSnapshots.size());
                }

            }
        };
        rhpNotificationListener = new FirebaseCollectionListener(context,rhpNotificationQuery,si);
    }

    public FirebaseCollectionListener getRHPNotificationListener(){
        if(rhpNotificationListener == null){
            createRHPNotificationListener();
        }
        return this.rhpNotificationListener;
    }

    private void killRHPNotificationListener(){
        if(rhpNotificationListener != null){
            rhpNotificationListener.killListener();
            rhpNotificationListener = null;
        }
    }

    /*--------------------------INDIVIDUAL POINT LOG LISTENER-------------------------------------*/

    /**
     * Create a listener for a specific point log document. Please kill this listener after you are done with it.
     * @param log The Point Log for which to listen for updates
     */
    public void createPointLogListener(PointLog log){
        if(pointLogListener != null){
            killPointLogListener();
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

    private void killPointLogListener(){
        if(pointLogListener != null){
            pointLogListener.killListener();
            pointLogListener = null;
        }
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

    private void killSystemPreferencesListener(){
        if(systemPreferenceListener != null){
            systemPreferenceListener.killListener();
            systemPreferenceListener = null;
        }
    }

    /*------------------------POINT TYPE LISTENER---------------------------------------------*/

    /**
     * Create the listener that updates when a point type is updated
     */
    private void createPointTypeListener(){
        Query pointTypeQuery = db.collection("PointTypes");
        SnapshotInterface si = new SnapshotInterface() {
            @Override
            public void handleQuerySnapshots(QuerySnapshot queryDocumentSnapshots, Exception e) {
                if( e == null){
                    List<PointType> types = new ArrayList<>();
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        types.add(new PointType(Integer.parseInt(doc.getId()),doc.getData()));
                    }
                    Collections.sort(types);
                    cacheManager.setPointTypeList(types);
                }
            }
        };
        pointTypeListener = new FirebaseCollectionListener(context,pointTypeQuery,si);
    }

    public FirebaseCollectionListener getPointTypeListener(){
        return pointTypeListener;
    }

    private void killPointTypeListener(){
        if(pointTypeListener != null){
            pointTypeListener.killListener();
            pointTypeListener = null;
        }
    }
}
