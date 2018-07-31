package Utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.PointLog;
import Models.PointType;

public class FirebaseUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Submits the log to Firebase. It will save the log in House/'House'/Points/ and then will save a reference to the point in Users/'user'/Points/
     *
     * @param log         PointLog:   The PointLog that you want to save to the database
     * @param documentID  String:     If you want the pointlog to have a specific ID save it here, otherwise leave empty or null
     * @param preapproved boolean:    true if it does not require RHP approval, false otherwise
     * @param fui         FirebaseUtilInterface: Implement the CompleteWithErrors(Exception e)
     */
    public void submitPointLog(PointLog log, String documentID, Boolean preapproved, final FirebaseUtilInterface fui) {
        String house = "HOUSE"; //TODO set House
        DocumentReference userRef; //TODO set userref
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
            db.collection("Houses").document(house).collection("Points")
                    .add(data)
                    // add an action listener to handle the event when it goes Async
                    .addOnSuccessListener(documentReference -> {
                        //Now that it is written to the house, we must write it to the user
                        Map<String, Object> userPointData = new HashMap<>();
                        userPointData.put("Point", documentReference);

                        log.getResidentRef().collection("Points").document(documentReference.getId())
                                .set(userPointData)
                                .addOnSuccessListener(aVoid -> {
                                    //Congrats it is now added to both the house and the user. Tell your caller you succeeded
                                    fui.onSuccess();
                                })
                                .addOnFailureListener(fui::onError);
                    })
                    .addOnFailureListener(fui::onError);
        } else {
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
                                .addOnSuccessListener(aVoid1 -> fui.onError(null))
                                .addOnFailureListener(fui::onError);
                    })
                    .addOnFailureListener(fui::onError);
        }
    }

    public void getPointTypes(final FirebaseUtilInterface fui) {
        db.collection("PointTypes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PointType> pointTypeList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> data = document.getData();
                    pointTypeList.add(new PointType(((Long) data.get("Value")).intValue(), (String) data.get("Description"), (boolean) data.get("ResidentsCanSubmit"), Integer.parseInt(document.getId())));
                }
                pointTypeList.sort(Comparator.comparing(PointType::getPointID));
                fui.onPointTypeComplete(pointTypeList);
            } else {
                fui.onError(task.getException());
            }
        });
    }

    public void getUserData(String id, final FirebaseUtilInterface fui) {
        db.collection("Users").document(id)
                .get()
                .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                Map<String, Object> data = task.getResult().getData();
                                if(data != null)
                                    fui.onUserGetSuccess((String)data.get("Name"), (String)data.get("FloorID"), (String)data.get("House"), ((Long)data.get("Permission Level")).intValue(), ((Long)data.get("TotalPoints")).intValue());
                                else
                                    fui.onError(new IllegalStateException("Data is null"));
                            }
                            else
                            {
                                fui.onError(task.getException());
                            }
                        }
                );
    }
}