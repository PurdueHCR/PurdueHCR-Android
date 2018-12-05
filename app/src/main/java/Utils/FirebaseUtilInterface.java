/**
 * This creates an interface that callers can use to define callback/handler methods
 * To respond when Firebase responds Asynchronously
 *
 */

package Utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.House;
import Models.Link;
import Models.PointLog;
import Models.PointType;
import Models.Reward;
import Models.SystemPreferences;

public interface FirebaseUtilInterface {
    default void onPointTypeComplete(List<PointType> data) {
    }

    default void onUserGetSuccess(String floor, String house, String name, int permission) {
    }

    default void onSuccess() {
    }

    default void onError(Exception e, Context context) {
        Toast.makeText(context, "House system is disabled", Toast.LENGTH_LONG).show(); //TODO: Text changed to House system is disabled from Firebase Util error
        Log.e("FirebaseUtil", e.getMessage(), e);
    }

    default void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
    }

    default void onGetLinkWithIdSuccess(Link link) {
    }

    default void onGetPointStatisticsSuccess(List<House> houses, int userPoints, List<Reward> rewards) {
    }

    default void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {

    }

    default void onGetQRCodesForUserSuccess(List<Link> qrCodes){

    }

    default void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {


    }


}