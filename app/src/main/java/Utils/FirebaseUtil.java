package Utils;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.PointLog;
import Models.PointType;

public class FirebaseUtil {
    private  FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void submitPointLog(PointLog log, String documentID, Boolean preapproved, final FirebaseUtilInterface fui){

    }

    public void getPointTypes(final FirebaseUtilInterface fui){
        db.collection("PointTypes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<PointType> pointTypeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        pointTypeList.add(new PointType((int)data.get("Value"), (String)data.get("Description"), (boolean)data.get("ResidentsCanSubmit"), Integer.parseInt(document.getId())));
                    }
                    fui.onComplete(pointTypeList);
                } else {
                    fui.onComplete(null);
                }
            }
        });
    }
}
