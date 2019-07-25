/**
 * This creates an interface that callers can use to define callback/handler methods
 * To respond when Firebase responds Asynchronously
 *
 */

package com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;

public interface FirebaseUtilInterface {
    default void onPointTypeComplete(List<PointType> data) {
    }

    default void onUserGetSuccess(String floor, String house, String firstName, String lastName, int permission) {
    }

    default void onSuccess() {
    }

    default void onError(Exception e, Context context) {
        Toast.makeText(context, "House system is disabled", Toast.LENGTH_LONG).show(); //TODO: Text changed to House system is disabled from Firebase Util error
        Log.e("FirebaseUtil", e.getMessage(), e);
    }

    default void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
    }

    default void onGetConfirmedPointsSuccess(ArrayList<PointLog> logs) {
    }

    default void onGetLinkWithIdSuccess(Link link) {
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

    default void onGetPersonalPointLogs(List<PointLog> personalLogs){}
}