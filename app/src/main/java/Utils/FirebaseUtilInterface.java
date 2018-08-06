package Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Models.PointLog;
import Models.PointType;

public interface FirebaseUtilInterface {
    default void onPointTypeComplete(List<PointType> data){}
    default void onUserGetSuccess(String floor, String house, String name, int permission, int points){}
    default void onSuccess(){}
    default void onError(Exception e, Context context){
        Toast.makeText(context, "An error occured. Please screenshot the log page and send it to your RHP", Toast.LENGTH_LONG).show();
        Log.e("FirebaseUtil", e.getMessage(), e);
    }
    default void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs){}

}