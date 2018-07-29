package Utils;

import java.util.List;

import Models.PointType;

// Because non-global variables are for people who care about technical debt
public class Singleton {
    private static Singleton instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private List<PointType> pointTypeList = null;
    private String floorName = null;
    private String houseName = null;
    private String userName = null;
    private int permissionLevel = 0;
    private int totalPoints = 0;

    private Singleton() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }


    public void getPointTypes(final SingletonInterface si) {
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
                    si.onError(new IllegalStateException("Point Type list is empty"));
                }
            }
            @Override
            public void onError(Exception e)
            {

            }
        });
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }

    public void setUserData(String floor, String house, String name, int permission, int points){
        floorName = floor;
        houseName = house;
        userName = name;
        permissionLevel = permission;
        totalPoints = points;
    }

    public void getUserData(SingletonInterface si){

    }
}