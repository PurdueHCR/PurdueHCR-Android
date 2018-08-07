package Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import Models.PointType;

public interface SingletonInterface {
    default void onPointTypeComplete(List<PointType> data){}
    default void onSuccess(){}
    default void onError(Exception e, Context context){
        Toast.makeText(context, "An error occured. Please screenshot the log page and send it to your RHP", Toast.LENGTH_LONG).show();
        Log.e("Singleton", e.getMessage(), e);
    }

}
