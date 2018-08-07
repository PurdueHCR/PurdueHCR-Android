package Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.PointLog;
import Models.PointType;

public class FirebaseUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

    protected void setApplicationContext(Context c){
        context = c;
    }

    /**
     * Submits the log to Firebase. It will save the log in House/'House'/Points/ and then will save a reference to the point in Users/'user'/Points/
     *
     * @param log         PointLog:   The PointLog that you want to save to the database
     * @param documentID  String:     If you want the pointlog to have a specific ID save it here, otherwise leave empty or null
     * @param preapproved boolean:    true if it does not require RHP approval, false otherwise
     * @param fui         FirebaseUtilInterface: Implement the CompleteWithErrors(Exception e). Exception will be null if no Exception is recieved
     */
    public void submitPointLog(PointLog log, String documentID, String house, String userID, boolean preapproved, final FirebaseUtilInterface fui) {
        log.setResidentRef(db.collection("Users").document(userID));
        int multiplier = (preapproved) ? 1 : -1;

        //Create the data to be put into the object in the database
        Map<String, Object> data = new HashMap<>();
        data.put("Description", log.getPointDescription());
        data.put("PointTypeID", (log.getType().getPointID() * multiplier));
        data.put("Resident", log.getResident());
        data.put("ResidentRef", log.getResidentRef());

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
                                        updateHouseAndUserPointsWithApprovedLog(log, house, fui);
                                    } else {
                                        fui.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> fui.onError(e, context));
                    })
                    .addOnFailureListener(e -> fui.onError(e, context));
        } else {
            //Add a value ot the Points collection in the house with the id: documentID
            DocumentReference ref = db.collection("Houses").document(house).collection("Points").document(documentID);
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
                                        updateHouseAndUserPointsWithApprovedLog(log, house, fui);
                                    } else {
                                        fui.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> fui.onError(e, context));
                    })
                    .addOnFailureListener(e -> fui.onError(e, context));
        }
    }

    /**
     * Handles the updating the database for approving or denying points. It will update the point in the house and the TotalPoints for both user and house
     *
     * @param log      PointLog:   The PointLog that is to be either approved or denied
     * @param approved boolean:    Was the log approved?
     * @param approvingOrDenyingUser    String:     Username of the account who is approving or denying point log
     * @param house    String:     The house that the pointlog belongs to
     * @param fui      FirebaseUtilInterface: Implement the OnError and onSuccess methods
     */
    public void hanldePointLog(PointLog log, boolean approved, String house, String approvingOrDenyingUser, FirebaseUtilInterface fui) {
        DocumentReference housePointRef = db.collection("House").document(house).collection("Points").document(log.getLogID());


        String descript = log.getPointDescription();
        if(!approved){
            descript = "DENIED: "+descript;
        }

        //Create the map that will update the point log
        Map<String, Object> data = new HashMap<>();
        data.put("PointTypeID", log.getType().getPointID());
        data.put("ApprovedBy", approvingOrDenyingUser);
        data.put("ApprovedOn", Timestamp.now());
        data.put("Description", descript);


        //update the point log
        housePointRef.update(data)
                .addOnSuccessListener((Void aVoid) -> {
                    //If the point log was sucessfully updated, update the points in house and user
                    if (approved) {
                        updateHouseAndUserPointsWithApprovedLog(log, house, fui);
                    }
                })
                //If failed, call the onError
                .addOnFailureListener(e -> fui.onError(e, context));

    }

    /**
     * This encapsulates the update House points and user points methods to increase readability
     *
     * @param log   PointLog:   log that was approved and needs to see points added to user and house
     * @param house String:     The house that contains the PointLog
     * @param fui   FirebaseUtilInterface:  Implement the onError and the onSuccess
     */
    private void updateHouseAndUserPointsWithApprovedLog(PointLog log, String house, FirebaseUtilInterface fui) {
        //Update the house points first. We want to update one at a time, so there is no concurrent calling of fui methods
        updateHousePoints(log, house, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                //If house is updated sucessfully, update the user's points.
                //The reason we update house points first is if one of the writes
                // fails, we want either no one gets the points, or the house gets the points.
                updateUserPoints(log, fui);
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
    private void updateHousePoints(PointLog log, String house, FirebaseUtilInterface fui) {
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
            long newCount = oldCount + log.getType().getPointValue();
            transaction.update(houseRef, "TotalPoints", newCount);

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
    private void updateUserPoints(PointLog log, FirebaseUtilInterface fui) {
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
            long newCount = oldCount + log.getType().getPointValue();
            transaction.update(residentRef, "TotalPoints", newCount);

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
                    pointTypeList.add(new PointType(((Long) data.get("Value")).intValue(), (String) data.get("Description"), (boolean) data.get("ResidentsCanSubmit"), Integer.parseInt(document.getId())));
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
                                if (data != null)
                                    fui.onUserGetSuccess((String) data.get("FloorID"), (String) data.get("House"), (String) data.get("Name"), ((Long) data.get("Permission Level")).intValue(), ((Long) data.get("TotalPoints")).intValue());
                                else
                                    fui.onError(new IllegalStateException("Data is null"), context);
                            } else {
                                fui.onError(task.getException(), context);
                            }
                        }
                );
    }

    public void getUnconfirmedPoints(String house, String floorId, final FirebaseUtilInterface fui){
        CollectionReference housePointRef = db.collection("House").document(house).collection("Points");
        housePointRef.whereLessThan("PointTypeID",0).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<PointLog> logs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String logFloorId = (String) document.get("FloorID");
                                if(floorId.equals(logFloorId)){
                                    String logId = document.getId();
                                    String description = (String) document.get("Description");
                                    int pointTypeId = document.getLong("PointTypeID").intValue();
                                    String resident = (String) document.get("Resident");
                                    Object ref = document.get("ResidentRef");
                                    DocumentReference residentRef;
                                    if(ref == null){
                                        residentRef = (DocumentReference) ref;
                                    }
                                    PointType pointType = Singleton.getInstance().getTypeWithPointId(pointTypeId);
                                    PointLog log = new PointLog(description, resident, pointType, floorId);
                                    log.setLogID(logId);
                                    logs.add(log);
                                }
                                fui.onGetUnconfirmedPointsSuccess(logs);
                            }
                        } else {
                            System.out.println("Error getting documents: "+ task.getException());
                            fui.onError(task.getException(),context);
                        }
                    }
                });
    }
}