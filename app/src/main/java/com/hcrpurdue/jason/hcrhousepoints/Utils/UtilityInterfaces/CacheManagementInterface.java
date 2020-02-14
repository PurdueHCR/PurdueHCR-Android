package com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.HouseCode;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;

public interface CacheManagementInterface {
    default void onPointTypeComplete(List<PointType> data) {
    }

    default void onSuccess() {
    }

    default void onError(Exception e, Context context) {
        Toast.makeText(context, "A CacheManager error occurred", Toast.LENGTH_LONG).show();
        Log.e("CacheManager", e.getMessage(), e);
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

    default void onGetPersonalPointLogs(List<PointLog> personalLogs){}

    default void onGetHouseCodes(List<HouseCode> codes){}


    default void onGetRank(AuthRank rank){}
}
