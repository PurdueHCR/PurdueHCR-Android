package Utils;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.House;
import Models.Link;
import Models.PointLog;
import Models.PointType;

// Because non-global variables are for people who care about technical debt
public class Singleton {
    private static Singleton instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private List<PointType> pointTypeList = null;
    private ArrayList<PointLog> unconfirmedPointList = null;
    private String userID = null;
    private String floorName = null;
    private String houseName = null;
    private String name = null;
    private int permissionLevel = 0;
    private int totalPoints = 0;
    private List<House> houseList = null;

    private Singleton() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    public void setApplicationContext(Context c) {
        fbutil.setApplicationContext(c);
    }

    public static Singleton getInstance() {
        if (instance == null) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            instance = new Singleton();
            if (auth.getCurrentUser() != null)
                instance.getUserData(auth.getUid(), new SingletonInterface() {
                });
        }
        return instance;
    }


    public void getPointTypes(final SingletonInterface si, Context context) {
        if (pointTypeList == null)
            fbutil.getPointTypes(new FirebaseUtilInterface() {
                @Override
                public void onPointTypeComplete(List<PointType> data) {
                    if (data != null && !data.isEmpty()) {
                        pointTypeList = data;
                        si.onPointTypeComplete(data);
                    } else {
                        si.onError(new IllegalStateException("Point Type list is empty"), context);
                    }
                }
            });
        else {
            si.onPointTypeComplete(pointTypeList);
        }
    }

    public void getUnconfirmedPoints(final SingletonInterface si) {
        fbutil.getUnconfirmedPoints(houseName, floorName, new FirebaseUtilInterface() {
            @Override
            public void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                unconfirmedPointList = logs;
                si.onUnconfirmedPointsSuccess(logs);
            }
        });
    }

    public PointType getTypeWithPointId(int pointId) {
        for (int i = 0; i < this.pointTypeList.size(); i++) {
            if (this.pointTypeList.get(i).getPointID() == pointId) {
                return this.pointTypeList.get(i);
            }
        }
        return null;
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }

    public void setUserData(String floor, String house, String n, int permission, int points, String id) {
        floorName = floor;
        houseName = house;
        name = n;
        permissionLevel = permission;
        totalPoints = points;
        userID = id;
    }

    public void getUserData(String id, SingletonInterface si) {
        if (houseName == null)
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

    public String getName() {
        return name;
    }

    public String getHouse() {
        return houseName;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void submitPoints(String description, PointType type, SingletonInterface si) {
        PointLog log = new PointLog(description, name, type, floorName);
        boolean preApproved = permissionLevel > 0;
        fbutil.submitPointLog(log, null, houseName, userID, preApproved, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }
        });
    }

    public void submitPointWithLink(Link link, SingletonInterface si) {
        if (link.isEnabled()) {
            PointType type = getTypeWithPointId(link.getPointTypeId());
            PointLog log = new PointLog(link.getDescription(), name, type, floorName);
            fbutil.submitPointLog(log, (link.isSingleUse()) ? link.getLinkId() : null, houseName, userID, true, new FirebaseUtilInterface() {
                @Override
                public void onSuccess() {
                    si.onSuccess();
                }

                @Override
                public void onError(Exception e, Context c) {
                    if (e.getLocalizedMessage().equals("Code was already submitted")) {
                        Toast.makeText(c, "You have already submitted this code.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        si.onError(e, c);
                    }
                }
            });
        } else {
            si.onError(new Exception("Link is not enabled."), fbutil.getContext());
        }

    }

    public String getFloorName() {
        return floorName;
    }

    public void getLinkWithLinkId(String linkId, SingletonInterface si) {
        System.out.println("YO: getlink: " + linkId);
        fbutil.getLinkWithId(linkId, new FirebaseUtilInterface() {
            @Override
            public void onGetLinkWithIdSuccess(Link link) {
                System.out.println("on get link success");
                si.onGetLinkWithIdSuccess(link);
            }
        });
    }

    public void getPointStatistics(SingletonInterface si) {
        fbutil.getPointStatistics(userID, new FirebaseUtilInterface() {
            @Override
            public void onGetUserPointSuccess(int data){
                totalPoints = data;
            }

            @Override
            public void onGetPointStatisticsSuccess(List<House> data) {
                houseList = data;
                si.onGetPointStatisticsSuccess(data);
            }
        });
    }

    public void clearUserData() {
        floorName = null;
        houseName = null;
        name = null;
        permissionLevel = 0;
        totalPoints = 0;
        userID = null;
    }

    public void getFloorCodes(SingletonInterface si){
        fbutil.getFloorCodes(new FirebaseUtilInterface() {
            @Override
            public void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {
                si.onGetFloorCodesSuccess(data);
            }
        });
    }

    public List<House> getHouseList(){
        return houseList;
    }

    public int getTotalPoints() {
        return totalPoints;
    }
}