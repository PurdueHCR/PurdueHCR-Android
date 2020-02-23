package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.HouseCode;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Models.User;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.FirebaseUtilInterface;

public class FirebaseUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

    public static String ROOT_HOUSE_KEY = "House";
    public static String ROOT_HOUSE_POINTS_KEY = "Points";
    public static String ROOT_HOUSE_POINTS_MESSAGES_KEY = "Messages";


    public static String ROOT_USERS_KEY = "Users";

    public static String ROOT_REWARDS_KEY = "Rewards";

    public static String ROOT_SYSTEM_PREFERENCES_KEY = "SystemPreferences";
    public static String ROOT_SYSTEM_PREFERENCES_PREFERENCE_DOCUMENT_KEY = "Preferences";

    public static String ROOT_POINT_TYPES_KEY = "PointTypes";

    public static String ROOT_LINKS_KEY = "Links";

    public static String ROOT_CODES_KEY = "HouseCodes";



    protected void setApplicationContext(Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Submits the log to Firebase. It will save the log in House/'House'/Points/ and then will save a reference to the point in Users/'user'/Points/
     *
     * @param log         PointLog:   The PointLog that you want to save to the database
     * @param documentID  String:     If you want the pointlog to have a specific ID save it here, otherwise leave empty or null
     * @param preapproved boolean:    true if it does not require RHP approval, false otherwise
     * @param fui         FirebaseUtilInterface: Implement the CompleteWithErrors(Exception e). Exception will be null if no Exception is recieved
     */
    public void submitPointLog(PointLog log, String documentID, String house, String userID, boolean preapproved, SystemPreferences sysPrefs, final FirebaseUtilInterface fui) {

        if(preapproved) {
            log.updateApprovalStatus(preapproved, context);
        }

        if(sysPrefs.isHouseEnabled() && log.getPointType() != null && log.getPointType().isEnabled()) {
            //Create the data to be put into the object in the database
            Map<String, Object> data = log.convertToDict();


            // If the pointLog does not care about its id, add value to the database with random ID
            if (TextUtils.isEmpty(documentID)) {
                //Add a value to the Points collection in a house
                db.collection(ROOT_HOUSE_KEY).document(house).collection(ROOT_HOUSE_POINTS_KEY)
                        .add(data)
                        // add an action listener to handle the event when it goes Async
                        .addOnSuccessListener(documentReference -> {
                            log.setLogID(documentReference.getId());
                            //Now that it is written to the house, check if we need to add points
                            if (preapproved) {
                                updateHouseAndUserPoints(log, house,false,false, fui);
                            } else {
                                fui.onSuccess();
                            }
                        })
                        .addOnFailureListener(e -> fui.onError(e, context));
            } else {
                //Add a value ot the Points collection in the house with the id: documentID
                DocumentReference ref = db.collection(ROOT_HOUSE_KEY).document(house).collection(ROOT_HOUSE_POINTS_KEY).document(log.getResidentId() + documentID);
                ref.get()
                        .addOnSuccessListener(task -> {
                            if (!task.exists()) {
                                ref.set(data)
                                        // add an action listener to handle the event when it goes Async
                                        .addOnSuccessListener(aVoid -> {
                                            //Set the log id
                                            log.setLogID(log.getResidentId() + documentID);
                                            //if the point is preapproved, update the house and user points
                                            if (preapproved) {
                                                updateHouseAndUserPoints(log, house,false,false, fui);
                                            } else {
                                                fui.onSuccess();
                                            }
                                        })
                                        .addOnFailureListener(e -> fui.onError(e, context));
                            } else {
                                fui.onError(new IllegalStateException("You have already submitted points for this QR Code."), context);
                            }
                        })
                        .addOnFailureListener(e -> fui.onError(e, context));
            }
        }
        else if(!log.getPointType().isEnabled()) {
            fui.onError(new Exception("This point type is not currently enabled for submissions."),context);
        }
        else
        {
            fui.onError(new Exception("The house competition is not currently active."),context);
        }
    }

    public DocumentReference getUserReference(String userID) {

        return db.collection(ROOT_USERS_KEY).document(userID);
    }

    /**
     * Handles the updating the database for approving or denying points. It will update the point in the house and the TotalPoints for both user and house
     *
     * @param log                    PointLog:   The PointLog that is to be either approved or denied
     * @paramad approved               boolean:    Was the log approved?
     * @param house                  String:     The house that the pointlog belongs to
     * @param fui                    FirebaseUtilInterface: Implement the OnError and onSuccess methods
     */
    public void handlePointLog(PointLog log, boolean approved, String house, FirebaseUtilInterface fui) {
        updatePointLogStatus(log,approved,house,false,false,fui);
    }

    /**
     * Update the status of the point log if it has been approved, rejected, or updated.
     * This method handles both initial approve/reject and Updating
     *
     * @param log   - PointLog to be updated. (Do not set updateApprovalStatus on the log.)
     * @param approved  - Boolean if the point is approved
     * @param house     - String for the house that the point belongs to
     * @param updating              - Boolean for if this point is being updated or set for the first time
     * @param fui   - FirebaseUtilInterface that implements on Success and On Error
     */
    public void updatePointLogStatus(PointLog log, boolean approved, String house, boolean updating, boolean isRECGrantingAward, FirebaseUtilInterface fui) {
        DocumentReference housePointRef = db.collection(ROOT_HOUSE_KEY).document(house).collection(ROOT_HOUSE_POINTS_KEY).document(log.getLogID());
        log.updateApprovalStatus(approved,context);
        housePointRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = task.getResult().getData();
                        if (data != null){
                            PointLog oldLog = new PointLog(task.getResult().getId(),data,context);
                            //If this is the first handling of this log, check to make sure it was not already approved
                            if(!updating && oldLog.wasHandled()){
                                fui.onError(new Exception("Point request has already been handled."),context);
                            }
                            else{
                                //It has either not been approved yet, or is updating, so we are good
                                // First, check if it is being updated and status are the same
                                if(updating && (log.wasRejected() == oldLog.wasRejected())){
                                    fui.onError(new Exception("Status has already been changed."),context);
                                }
                                else{
                                    housePointRef.update(log.convertToDict()).addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            if((approved || updating)){
                                                updateHouseAndUserPoints(log,house,updating,isRECGrantingAward,fui);
                                                if(updating && log.getResidentId() != CacheManager.getInstance(context).getUserId()){
                                                    //If the point has been updated, and the point was not updated by the user who submitted it, notifiy the user that they have something new!
                                                    updatePointLogNotificationCount(log, house, true, false, new FirebaseUtilInterface() {});
                                                }
                                            }
                                            else{
                                                fui.onSuccess();
                                            }
                                        }
                                        else{
                                            fui.onError(task1.getException(), context);
                                        }
                                    });
                                }
                            }
                        }

                        else {
                            fui.onError(new IllegalStateException("Data is null"), context);
                        }
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context)
                );
    }

    /**
     * This encapsulates the update House points and user points methods to increase readability
     *
     * @param log   PointLog:   log that was approved and needs to see points added to user and house
     * @param house String:     The house that contains the PointLog
     * @param fui   FirebaseUtilInterface:  Implement the onError and the onSuccess
     */
    private void updateHouseAndUserPoints(PointLog log, String house, boolean isUpdating, boolean isRECGrantingAward, FirebaseUtilInterface fui) {
        //Update the house points first. We want to update one at a time, so there is no concurrent calling of fui methods
        updateHousePoints(log, house, isUpdating, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                //If house is updated sucessfully, update the user's points.
                //The reason we update house points first is if one of the writes
                // fails, we want either no one gets the points, or the house gets the points.

                //If the REC is granting points, don't update any user point
                if(!isRECGrantingAward){
                    updateUserPoints(log, isUpdating, new FirebaseUtilInterface() {
                        @Override
                        public void onSuccess() {
                            fui.onSuccess();
                        }
                    });
                }

            }
        });
    }

    /**
     * Update the Total Points value in the house with the points earned from approving a PointLog.
     *
     * @param log   PointLog:   Log that was approved and contains the point value to be added to Total points for house
     * @param house String:     House to add the points to
     * @param fui   FirebaseUtilInterface:  Implement the onError and the onSuccess
     */
    private void updateHousePoints(PointLog log, String house, Boolean isUpdating, FirebaseUtilInterface fui) {
        //Create the reference for the house
        final DocumentReference houseRef = db.collection(ROOT_HOUSE_KEY).document(house);

        //To avoid race conditions, we use transactions
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(houseRef);
            //Get the old value, update it, then put back into the document
            Long oldCount = snapshot.getLong("TotalPoints");
            if (oldCount == null) {
                oldCount = 0L;
            }
            Long newTotal = oldCount;
            //Update a local value with the correct point value
            if(isUpdating){
                //If log was already approved or rejected, and the status has been changed perform logic.
                if(log.wasRejected()){
                    newTotal -= log.getType().getValue(); //Log was approved but is now rejected, so remove points
                }
                else{
                    newTotal += log.getType().getValue(); // Log was rejected, but is now approved
                }
            }
            else{
                //This is the first time the point has been handled, so just add the point
                newTotal += log.getType().getValue();
            }

            transaction.update(houseRef, "TotalPoints", newTotal);

            // Success, kill the transaction with a return null
            return null;
        })
                .addOnSuccessListener(aVoid -> fui.onSuccess())
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    /**
     * Update the Total Points value in the user with the points earned from approving a PointLog.
     *
     * @param log PointLog:   Log that was approved and contains the point value and the ResidentReference
     * @param fui FirebaseUtilInterface:  Implement the onError and the onSuccess
     */
    private void updateUserPoints(PointLog log, Boolean isUpdating, FirebaseUtilInterface fui) {
        //Create reference for resident
        final DocumentReference residentRef = db.collection(ROOT_USERS_KEY).document(log.getResidentId());

        //Avoid a race condition with transactions
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(residentRef);
            //Get the old value, update it, then put back into the document
            Long oldCount = snapshot.getLong("TotalPoints");
            if (oldCount == null) {
                oldCount = 0L;
            }
            Long newTotal = oldCount;
            //Update a local value with the correct point value
            if(isUpdating){
                //If log was already approved or rejected, and the status has been changed perform logic.
                if(log.wasRejected()){
                    newTotal -= log.getType().getValue(); //Log was approved but is now rejected, so remove points
                }
                else{
                    newTotal += log.getType().getValue(); // Log was rejected, but is now approved
                }
            }
            else{
                //This is the first time the point has been handled, so just add the point
                newTotal += log.getType().getValue();
            }
            transaction.update(residentRef, "TotalPoints", newTotal);

            // Success, kill the transaction with a return null
            return null;
        })
                .addOnSuccessListener(aVoid -> fui.onSuccess())
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    /**
     * Retrieve the point types for the competition
     * @param fui
     */
    public void getPointTypes(final FirebaseUtilInterface fui) {
        db.collection(ROOT_POINT_TYPES_KEY).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PointType> pointTypeList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    pointTypeList.add(new PointType(Integer.parseInt(document.getId()),data));
                }
                Collections.sort(pointTypeList);
                fui.onPointTypeComplete(pointTypeList);
            } else {
                fui.onError(task.getException(), context);
            }
        });
    }

    /**
     * Retrieve the data for the specific user
     * @param id
     * @param fui
     */
    public void getUserData(String id, final FirebaseUtilInterface fui) {
        db.collection(ROOT_USERS_KEY).document(id)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Map<String, Object> data = task.getResult().getData();
                                if (data != null) {
                                    User user = new User(id, data);
                                    fui.onUserGetSuccess(user);
                                }
                                else {
                                    fui.onError(new IllegalStateException("User does not exist."), context);
                                }
                            } else {
                                fui.onError(task.getException(), context);
                            }
                        }
                )
                .addOnFailureListener(e -> fui.onError(e, context)
                );
    }

    /**
     * Get all the points which have not been confirmed so that RHPS can approve/reject them
     * @param house
     * @param floorId
     * @param fui
     */
    public void getUnconfirmedPoints(String house, String floorId, final FirebaseUtilInterface fui) {
        CollectionReference housePointRef = db.collection(ROOT_HOUSE_KEY).document(house).collection(ROOT_HOUSE_POINTS_KEY);
        housePointRef.whereLessThan("PointTypeID", 0).get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {
                        ArrayList<PointLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String logFloorId = (String) document.get("FloorID");
                            if (!floorId.equals("6N") && !floorId.equals("6S")) {
                                if (floorId.equals(logFloorId)) {
                                    String logId = document.getId();
                                    PointLog log = new PointLog(logId, document.getData(), context);
                                    logs.add(log);
                                }
                            } else {
                                if (floorId.equals(logFloorId) || "Shreve".equals(logFloorId)) {
                                    String logId = document.getId();
                                    PointLog log = new PointLog(logId, document.getData(), context);
                                    logs.add(log);
                                }
                            }
                        }
                        fui.onGetUnconfirmedPointsSuccess(logs);
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    /**
     * Get the Link model for an id from firebase
     *
     * @param id  String id that you want to check the firebase for
     * @param fui firebaseutilInterface, implement onError, and onGetLinkWithIdSuccess
     */
    public void getLinkWithId(String id, final FirebaseUtilInterface fui) {
        DocumentReference linkRef = this.db.collection(ROOT_LINKS_KEY).document(id);
        linkRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        Map<String, Object> data = doc.getData();
                        if (doc.exists() && data != null) {
                            String descr = (String) data.get("Description");
                            int pointId = Objects.requireNonNull(doc.getLong("PointID")).intValue();
                            boolean single = (boolean) data.get("SingleUse");
                            boolean enabled = (boolean) data.get("Enabled");
                            boolean archived = (boolean) data.get("Archived");
                            Link link = new Link(id, descr, single, pointId, enabled, archived);
                            fui.onGetLinkWithIdSuccess(link);
                        } else {
                            fui.onError(new IllegalStateException("No Link"), context);
                        }
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    /**
     * Get the list of QRCodes that were created by the User with userId.
     *
     * @param userId    String that represents the Firebase ID of the User who is getting QR codes.
     * @param fui       Firebase Util Interface that has the methods onError and onGetQRCodesForUserSuccess implemented.
     */
    public void getQRCodesForUser(String userId, FirebaseUtilInterface fui){
        //Run Firebase call to retrieve all Links where the CreatorID is equal to the userID
        db.collection(ROOT_LINKS_KEY).whereEqualTo("CreatorID",    userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                ArrayList<Link> qrCodes = new ArrayList<>();
                //If task is successful, FB returns an array of QueryDocumentSnapshot that matches the request
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Pull values from document and save them into an object
                    String id = document.getId();
                    String description = ((String) document.get("Description"));
                    boolean singleUse = ((boolean) document.get("SingleUse"));
                    int pointTypeId = ((Long)document.get("PointID")).intValue();
                    boolean isEnabled = ((boolean) document.get("Enabled"));
                    boolean isArchived = ((boolean) document.get("Archived"));
                    Link newCode = new Link(id, description, singleUse, pointTypeId, isEnabled,isArchived);
                    //Add code to the list
                    qrCodes.add(newCode);
                }
                // Call the Success handler that the caller defines
                fui.onGetQRCodesForUserSuccess(qrCodes);
            }
            else{
                //If there was an issue, print this problem
                System.out.println("GET QRCODE ERROR: Firebase was unsucessful");
                //Call the Error handler
                fui.onError(task.getException(),context);
            }
        });
    }


    /**
     * Create a new QRCode in the database. If the call is succesful, the new LinkId will be saved into the Link object
     *
     * @param link  Link object to be created in the database and the object that will be updated on success
     * @param fui   Firebase Util Interface with methods onError and onSuccess.
     */
    public void createQRCode(Link link, FirebaseUtilInterface fui){
        //turn the Link object into a Map with the Keys being the Firebase Keys
        Map<String, Object> data = new HashMap<>();
        data.put("Description", link.getDescription());
        data.put("PointID", link.getPointTypeId());
        data.put("SingleUse", link.isSingleUse());
        data.put("CreatorID", CacheManager.getInstance(context).getUserId());
        data.put("Enabled", link.isEnabled());
        data.put("Archived", link.isArchived());

        //Call Firebase to add the data
        db.collection(ROOT_LINKS_KEY).add(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                System.out.println("Added Document to: "+task.getResult().getId());
                //If successful, save the document ID as the LinkID so that the caller has a reference to it.
                link.setLinkId(task.getResult().getId());
                // Call the on success
                fui.onSuccess();
            }
            else {
                System.out.println("Failed to add QR Code");
                //Error Handler
                fui.onError(task.getException(),context);
            }
        });
    }

    /**
     * Update a Link object in the database with a new Enabled Status
     *
     * @param link  Link object to be updated
     * @param fui   FirebaseUtilInterface with method OnError and onSuccess implemented
     */
    public void setQRCodeEnabledStatus(Link link, boolean isEnabled, FirebaseUtilInterface fui){
        //Create a map with all the keys that are going to be updated
        Map<String, Object> data = new HashMap<>();
        data.put("Enabled", isEnabled);

        //Tell the document with path Links/<LinkID> to update the values stored in the map
        db.collection(ROOT_LINKS_KEY).document(link.getLinkId()).update(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                System.out.println("Link Update Success");
                //Make sure local copy is updated
                link.setEnabled(isEnabled);
                fui.onSuccess();
            }
            else{
                //Handle Errors
                fui.onError(task.getException(),context);
            }
        });
    }

    /**
     * Update a Link object in the database with a new Archived Status
     *
     * @param link  Link object to be updated
     * @param fui   FirebaseUtilInterface with method OnError and onSuccess implemented
     */
    public void setQRCodeArchivedStatus(Link link, boolean isArchived, FirebaseUtilInterface fui){
        //Create a map with all the keys that are going to be updated
        Map<String, Object> data = new HashMap<>();
        data.put("Archived", isArchived);

        //Tell the document with path Links/<LinkID> to update the values stored in the map
        db.collection(ROOT_LINKS_KEY).document(link.getLinkId()).update(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                System.out.println("Link Update Success");
                //Make sure local copy is updated
                link.setArchived(isArchived);
                fui.onSuccess();
            }
            else{
                //Handle Errors
                fui.onError(task.getException(),context);
            }
        });
    }

    /**
     * Retrieve the system preferences from Firestore and callback onGetSystemPreferencesSuccess
     * @param fui   FirebaseUtilInterface which implements onGetSystemPreferencesSuccess and OnError
     */
    public void getSystemPreferences(FirebaseUtilInterface fui) {
        DocumentReference preference = db.collection(ROOT_SYSTEM_PREFERENCES_KEY).document(ROOT_SYSTEM_PREFERENCES_PREFERENCE_DOCUMENT_KEY);

        preference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
               SystemPreferences sysPrefs = new SystemPreferences(task.getResult().getData());
               fui.onGetSystemPreferencesSuccess(sysPrefs);
            }
            else {
                fui.onError(task.getException(), context);
            }

        });


    }

    /**
     * Retrieve all the house points for a given house and sort them by ResidentReportTime descending
     * @param house Firestore Key for the house that you wish to get all the house points for.
     * @param floorId   String Floor Id for the user who is looking them up. This ensures that the viewer can only see the points for the floor they live on
     * @param fui
     */
    public void getAllHousePoints(String house, String floorId, final FirebaseUtilInterface fui) {
        CollectionReference housePointRef = db.collection(ROOT_HOUSE_KEY).document(house).collection(ROOT_HOUSE_POINTS_KEY);
        housePointRef.orderBy(PointLog.DATE_SUBMITTED_KEY, Query.Direction.DESCENDING).get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {
                        ArrayList<PointLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String logFloorId = (String) document.get("FloorID");
                            //TODO Think of a more dynamic way to handle this for People WHo do not live in HCRH
                            if (!floorId.equals("6N") && !floorId.equals("6S")) {
                                if (floorId.equals(logFloorId)) {
                                    String logId = document.getId();
                                    PointLog log = new PointLog(logId, document.getData(), context);
                                    logs.add(log);
                                }
                            } else {
                                if (floorId.equals(logFloorId) || "Shreve".equals(logFloorId)) {
                                    String logId = document.getId();
                                    PointLog log = new PointLog(logId, document.getData(), context);
                                    logs.add(log);
                                }
                            }
                        }
                        fui.onGetAllHousePointsSuccess(logs);
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    /**
     * This poorly named method adds a listener to Firestore to update the local copy of PointLogMessages.
     * TODO: Move this to the Listener UTIL
     * @param log   Point Log for which to start listening for messages
     * @param house House that the point log belongs to
     * @param fui
     */
    public void handlePointLogUpdates(PointLog log, String house, final FirebaseUtilInterface fui){
        CollectionReference housePointRef = db.collection(ROOT_HOUSE_KEY).document(house)
                .collection(ROOT_HOUSE_POINTS_KEY).document(log.getLogID())
                .collection(ROOT_HOUSE_POINTS_MESSAGES_KEY);
        housePointRef.orderBy(PointLogMessage.MESSAGE_CREATION_DATE_KEY, Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if( e != null){
                    fui.onError(e,context);
                }
                else{
                    List<PointLogMessage> messages = new ArrayList<>();
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        messages.add(new PointLogMessage(doc.getData()));
                    }
                    fui.onGetPointLogMessageUpdates(messages);
                }
            }
        });
    }

    /**
     * This will save a message to a Point Log in Firestore
     * @param log   Point Log on which to save the message
     * @param house House that the point log is for
     * @param message   String message to be saved
     * @param fui FUI which implements OnSuccess and optionally OnError
     */
    public void postMessageToPointLog(PointLog log, String house, PointLogMessage message, FirebaseUtilInterface fui){
        postMessageToPointLog(log, house, message, true, fui);
    }

    /**
     * This will save a message to a Point Log in Firestore
     * @param log   Point Log on which to save the message
     * @param house House that the point log is for
     * @param message   String message to be saved
     * @param shouldNotify  Boolean for if notifications should be posted
     * @param fui FUI which implements OnSuccess and optionally OnError
     */
    public void postMessageToPointLog(PointLog log, String house, PointLogMessage message, Boolean shouldNotify, FirebaseUtilInterface fui){
        CollectionReference pointMessageRef = db.collection(ROOT_HOUSE_KEY).document(house)
                .collection(ROOT_HOUSE_POINTS_KEY).document(log.getLogID())
                .collection(ROOT_HOUSE_POINTS_MESSAGES_KEY);
        //Update the point message collection
        pointMessageRef.add(message.generateFirebaseMap()).addOnSuccessListener(documentReference -> {
            //Once the message is saved, update the notification count on the Point Log.
            if(shouldNotify)
                updatePointLogNotificationCount(log,house,(message.getSenderPermissionLevel() == UserPermissionLevel.RHP),false, fui);
            else{
                fui.onSuccess();
            }
        });
    }

    /**
     * Get the collection of Point Logs for the given user.
     * //TODO Change the logic to not need this and instead wait for the Listeners to finish Initialization
     * @param userId
     * @param house
     * @param fui
     */
    public void getPersonalPointLogs(String userId, String house, final FirebaseUtilInterface fui){
        CollectionReference personalPointReference = db.collection(ROOT_HOUSE_KEY).document(house)
                .collection(ROOT_HOUSE_POINTS_KEY);
        personalPointReference.whereEqualTo(PointLog.RESIDENT_ID_KEY,userId).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<PointLog> logs = new ArrayList<>();
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            logs.add(new PointLog(document.getId(),document.getData(),context));
                        }
                        fui.onGetPersonalPointLogs(logs);
                    }
                    else{
                        fui.onError(task.getException(),context);
                    }
                });
    }

    /**
     * Use this method to update the count of notifications on a point log record
     * @param log   Point Log Object for which to update the notification count.
     * @param house House that the point log belongs to.
     * @param notificationForResident    Boolean false if this number needs to be updated for the rhp count or true to update the resident count.
     * @param shouldReset Reset the number to 0.
     * @param fui   FirebaseUtilInterface that implements OnSuccess and OnError.
     */
    public void updatePointLogNotificationCount(PointLog log, String house, boolean notificationForResident, boolean shouldReset, final FirebaseUtilInterface fui){
        DocumentReference pointLogRef = db.collection(ROOT_HOUSE_KEY).document(house)
                .collection(ROOT_HOUSE_POINTS_KEY).document(log.getLogID());
        //Use PointLog static method to ensure key is correctly used
        Map<String,Object> data = log.createNotificationsMap(notificationForResident,shouldReset);

        pointLogRef.update(data).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                fui.onSuccess();
            }
            else{
                fui.onError(task1.getException(), context);
            }
        });
    }

    /**
     * Get the house codes
     * @param fui
     */
    public void retrieveHouseCodes(final FirebaseUtilInterface fui){
        CollectionReference houseCodesRef = db.collection(ROOT_CODES_KEY);
        houseCodesRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        ArrayList<HouseCode> codes = new ArrayList<>();
                        for(QueryDocumentSnapshot doc: task.getResult()){
                            codes.add(new HouseCode(doc.getData()));
                        }
                        fui.onGetHouseCodes(codes);
                    }
                    else{
                        fui.onError(task.getException(),context);
                    }
                });
    }

}