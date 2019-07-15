package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.FirebaseUtilInterface;

public class FirebaseUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

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

        //TODO: Step 2

        if(sysPrefs.isHouseEnabled() && log.getPointType() != null && log.getPointType().isEnabled()) {
            //Create the data to be put into the object in the database
            Map<String, Object> data = log.convertToDict();


            // If the pointLog does not care about its id, add value to the database with random ID
            if (TextUtils.isEmpty(documentID)) {
                //Add a value to the Points collection in a house
                db.collection("House").document(house).collection("Points")
                        .add(data)
                        // add an action listener to handle the event when it goes Async
                        .addOnSuccessListener(documentReference -> {
                            //Now that it is written to the house, we must write it to the user
                            Map<String, Object> userPointData = new HashMap<>();
                            userPointData.put("Point", documentReference);

                            log.getResidentRef().collection("Points").document(documentReference.getId())
                                    .set(userPointData)
                                    .addOnSuccessListener(aVoid -> {
                                        //if the point is preapproved, update the house and user points
                                        if (preapproved) {
                                            updateHouseAndUserPoints(log, house,false,false, fui);
                                        } else {
                                            fui.onSuccess();
                                        }
                                    })
                                    .addOnFailureListener(e -> fui.onError(e, context));
                        })
                        .addOnFailureListener(e -> fui.onError(e, context));
            } else {
                //Add a value ot the Points collection in the house with the id: documentID
                DocumentReference ref = db.collection("House").document(house).collection("Points").document(log.getResidentRef().getId() + documentID);
                ref.get()
                        .addOnSuccessListener(task -> {
                            if (!task.exists()) {
                                ref.set(data)
                                        // add an action listener to handle the event when it goes Async
                                        .addOnSuccessListener(aVoid -> {
                                            //Now that it is written to the house, we must write it to the user
                                            Map<String, Object> userPointData = new HashMap<>();
                                            userPointData.put("Point", ref);

                                            // add the value to the Points table in a user
                                            log.getResidentRef().collection("Points").document(documentID)
                                                    .set(userPointData)
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        //if the point is preapproved, update the house and user points
                                                        if (preapproved) {
                                                            updateHouseAndUserPoints(log, house,false,false, fui);
                                                        } else {
                                                            fui.onSuccess();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> fui.onError(e, context));
                                        })
                                        .addOnFailureListener(e -> fui.onError(e, context));
                            } else {
                                fui.onError(new IllegalStateException("Code was already submitted"), context);
                            }
                        })
                        .addOnFailureListener(e -> fui.onError(e, context));
            }
        }
        else if(sysPrefs.isHouseEnabled()) {
            Toast.makeText(context, "Point not enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, sysPrefs.getHouseIsEnabledMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    public DocumentReference getUserReference(String userID) {

        return db.collection("Users").document(userID);
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
        DocumentReference housePointRef = db.collection("House").document(house).collection("Points").document(log.getLogID());
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
                                            //TODO Update the House and User points based on approval and updating
                                            if((approved || updating)){
                                                updateHouseAndUserPoints(log,house,updating,isRECGrantingAward,fui);
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
        final DocumentReference houseRef = db.collection("House").document(house);

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
        final DocumentReference residentRef = log.getResidentRef();

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

    public void getPointTypes(final FirebaseUtilInterface fui) {
        db.collection("PointTypes").get().addOnCompleteListener(task -> {
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

    public void getUserData(String id, final FirebaseUtilInterface fui) {
        db.collection("Users").document(id)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Map<String, Object> data = task.getResult().getData();
                                if (data != null) {
                                    String firstName = (String) data.get("FirstName");
                                    String lastName = (String) data.get("LastName");
                                    fui.onUserGetSuccess((String) data.get("FloorID"), (String) data.get("House"),firstName,lastName, ((Long) data.get("Permission Level")).intValue());
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

    public void getUnconfirmedPoints(String house, String floorId, final FirebaseUtilInterface fui) {
        CollectionReference housePointRef = db.collection("House").document(house).collection("Points");
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
                        if (logs.isEmpty())
                            Toast.makeText(context, "No unapproved points", Toast.LENGTH_SHORT).show();
                        fui.onGetUnconfirmedPointsSuccess(logs);
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    public void getConfirmedPoints(String house, String floorId, List<PointType> pointTypes, final FirebaseUtilInterface fui) {
        CollectionReference housePointRef = db.collection("House").document(house).collection("Points");
        housePointRef.whereGreaterThan("PointTypeID", 0).get()
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
                        if (logs.isEmpty())
                            Toast.makeText(context, "No unapproved points", Toast.LENGTH_SHORT).show();
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
        if(!Singleton.getInstance(context).getCachedSystemPreferences().isHouseEnabled()){
            fui.onError(new Exception(),context);
            return;
        }
        DocumentReference linkRef = this.db.collection("Links").document(id);
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

    public void getPointStatistics(String userID, boolean getRewards, FirebaseUtilInterface fui) {
        List<House> houseList = new ArrayList<>();
        DocumentReference userRef = db.collection("Users").document(userID);
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = task.getResult().getData();
                        if (data != null) {
                            int userPoints = ((Long) data.get("TotalPoints")).intValue();
                            db.collection("House").get()
                                    .addOnCompleteListener(houseTask -> {
                                        if (houseTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot doc : houseTask.getResult()) {
                                                Map<String, Object> houseData = doc.getData();
                                                Integer housePoints = ((Long) houseData.get("TotalPoints")).intValue();
                                                Integer numRes = ((Long) houseData.get("NumberOfResidents")).intValue();
                                                House house = new House(doc.getId(), numRes, housePoints);
                                                houseList.add(house);
                                            }
                                            if (getRewards) // Rewards don't update often, don't make an extra query if not needed
                                            {
                                                db.collection("Rewards").get()
                                                        .addOnCompleteListener(rewardTask -> {
                                                            if (rewardTask.isSuccessful()) {
                                                                List<Reward> rewardList = new ArrayList<>();
                                                                Resources resources = context.getResources();
                                                                String packageName = context.getPackageName();
                                                                for (QueryDocumentSnapshot doc : rewardTask.getResult()) {
                                                                    Map<String, Object> rewardData = doc.getData();
                                                                    String fileName = (String) rewardData.get("FileName");
                                                                    String rewardIcon = fileName.replace(".png", "").toLowerCase();
                                                                    int rewardIconResource = resources.getIdentifier(rewardIcon, "drawable", packageName);
                                                                    int rewardPoints = ((Long) rewardData.get("RequiredValue")).intValue();
                                                                    Reward reward = new Reward(doc.getId(), rewardPoints, rewardIconResource);
                                                                    rewardList.add(reward);
                                                                }
                                                                fui.onGetPointStatisticsSuccess(houseList, userPoints, rewardList);
                                                            } else
                                                                fui.onError(rewardTask.getException(), context);
                                                        })
                                                        .addOnFailureListener(e -> fui.onError(e, context));
                                            }
                                            else{
                                                fui.onGetPointStatisticsSuccess(houseList, userPoints, null);
                                            }
                                        } else {
                                            fui.onError(houseTask.getException(), context);
                                        }
                                    })
                                    .addOnFailureListener(e -> fui.onError(e, context));
                        } else {
                            fui.onError(new IllegalStateException("houseData is null"), context);
                        }
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    public void getFloorCodes(FirebaseUtilInterface fui) {
        db.collection("House").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Pair<String, String>> floorCodes = new HashMap<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        if (entry.getKey().contains(":Code") && entry.getValue().getClass() == String.class) {
                            floorCodes.put((String) entry.getValue(), new Pair<>(entry.getKey().replace(":Code", ""), document.getId()));
                        }
                    }
                }
                fui.onGetFloorCodesSuccess(floorCodes);
            } else
                fui.onError(task.getException(), context);
        });
    }


    /**
     * Get the list of QRCodes that were created by the User with userId.
     *
     * @param userId    String that represents the Firebase ID of the User who is getting QR codes.
     * @param fui       Firebase Util Interface that has the methods onError and onGetQRCodesForUserSuccess implemented.
     */
    public void getQRCodesForUser(String userId, FirebaseUtilInterface fui){
        //Run Firebase call to retrieve all Links where the CreatorID is equal to the userID
        db.collection("Links").whereEqualTo("CreatorID",    userId).get().addOnCompleteListener(task -> {
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
        data.put("CreatorID", Singleton.getInstance(context).getUserId());
        data.put("Enabled", link.isEnabled());
        data.put("Archived", link.isArchived());

        //Call Firebase to add the data
        db.collection("Links").add(data).addOnCompleteListener(task -> {
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
        db.collection("Links").document(link.getLinkId()).update(data).addOnCompleteListener(task -> {
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
        db.collection("Links").document(link.getLinkId()).update(data).addOnCompleteListener(task -> {
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

    public void getSystemPreferences(FirebaseUtilInterface fui) {
        DocumentReference preference = db.collection("SystemPreferences").document("Preferences");

        preference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {



               boolean isHouseEnabled = ((boolean) task.getResult().get("isHouseEnabled"));
               String houseEnabledMessage = ((String) task.getResult().get("houseEnabledMessage"));

               SystemPreferences sysPrefs = new SystemPreferences(isHouseEnabled, houseEnabledMessage);
               fui.onGetSystemPreferencesSuccess(sysPrefs);
            }
            else {
                fui.onError(task.getException(), context);
            }

        });


    }

    public void getAllHousePoints(String house, String floorId, final FirebaseUtilInterface fui) {
        CollectionReference housePointRef = db.collection("House").document(house).collection("Points");
        housePointRef.orderBy("ResidentReportTime", Query.Direction.DESCENDING).get()
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
                        if (logs.isEmpty())
                            Toast.makeText(context, "No unapproved points", Toast.LENGTH_SHORT).show();
                        fui.onGetAllHousePointsSuccess(logs);
                    } else {
                        fui.onError(task.getException(), context);
                    }
                })
                .addOnFailureListener(e -> fui.onError(e, context));
    }

    public void handlePointLogUpdates(PointLog log, String house, final FirebaseUtilInterface fui){
        CollectionReference housePointRef = db.collection("House").document(house)
                .collection("Points").document(log.getLogID())
                .collection("Messages");
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

    public void postMessageToPointLog(PointLog log, String house, PointLogMessage message, FirebaseUtilInterface fui){
        CollectionReference pointMessageRef = db.collection("House").document(house)
                .collection("Points").document(log.getLogID())
                .collection("Messages");
        pointMessageRef.add(message.generateFirebaseMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("Finishjed");
                fui.onSuccess();
            }
        });
    }

}