package Utils;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.House;
import Models.Link;
import Models.PointLog;
import Models.PointLogMessage;
import Models.PointType;
import Models.Reward;
import Models.SystemPreferences;

public interface SingletonInterface {
    default void onPointTypeComplete(List<PointType> data) {
    }

    default void onSuccess() {
    }

    default void onError(Exception e, Context context) {
        Toast.makeText(context, "A Singleton error occurred", Toast.LENGTH_LONG).show();
        Log.e("Singleton", e.getMessage(), e);
    }

    default void onGetLinkWithIdSuccess(Link link) {
    }

    default void onUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
    }

    default void onConfirmedPointsSuccess(ArrayList<PointLog> logs) {

    }

    default void onGetPointStatisticsSuccess(List<House> houses, int userPoints, List<Reward> rewards) {
    }

    default void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {

    }

    default void onGetQRCodesForUserSuccess(ArrayList<Link> qrCodes){

    }

    default void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {

    }
    default void onGetAllHousePointsSuccess(List<PointLog> houseLogs){

    }

    default void onGetPointLogMessageUpdates(List<PointLogMessage> messages){}
}
