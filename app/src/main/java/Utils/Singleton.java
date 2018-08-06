package Utils;

import android.content.Context;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import Models.PointLog;
import Models.PointType;

// Because non-global variables are for people who care about technical debt
public class Singleton {
    private static Singleton instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private List<PointType> pointTypeList = null;
    private String userID = null;
    private String floorName = null;
    private String houseName = null;
    private String name = null;
    private int permissionLevel = 0;
    private int totalPoints = 0;

    private Singleton() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    public void setApplicationContext(Context c){
        fbutil.setApplicationContext(c);
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }


    public void getPointTypes(final SingletonInterface si, Context context) {
        fbutil.getPointTypes(new FirebaseUtilInterface() {
            @Override
            public void onPointTypeComplete(List<PointType> data) {
                if(data != null && !data.isEmpty())
                {
                    pointTypeList = data;
                    si.onPointTypeComplete(data);
                }
                else
                {
                    si.onError(new IllegalStateException("Point Type list is empty"), context);
                }
            }
        });
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }

    public void setUserData(String floor, String house, String n, int permission, int points, String id){
        floorName = floor;
        houseName = house;
        name = n;
        permissionLevel = permission;
        totalPoints = points;
        userID = id;
    }

    public void getUserData(String id, SingletonInterface si){
        if(houseName == null)
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String name, int permission, int points) {
                    setUserData(floor, house, name, permission, points, id);
                    si.onSuccess();
                }
            });
        else
            si.onSuccess();
    }

    public String getHouse(){
        return houseName;
    }

    public void submitPoints(String description, PointType type, SingletonInterface si){
        PointLog log = new PointLog(description, name, type, floorName);
        boolean preApproved = permissionLevel > 0;
        fbutil.submitPointLog(log, null, houseName, userID, preApproved, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }
        });
    }
}