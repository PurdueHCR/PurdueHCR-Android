package Utils;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.House;
import Models.Link;
import Models.PointLog;
import Models.PointType;
import Models.Reward;

// Because non-global variables are for people who care about technical debt
public class Singleton {
    private static Singleton instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private CacheUtil cacheUtil = new CacheUtil();
    private List<PointType> pointTypeList = null;
    private ArrayList<PointLog> unconfirmedPointList = null;
    private String userID = null;
    private String floorName = null;
    private String houseName = null;
    private String name = null;
    private int permissionLevel = 0;
    private int totalPoints = 0;
    private List<House> houseList = null;
    private List<Reward> rewardList = null;
    private List<Link> userCreatedQRCodes = null;

    private Singleton() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    private void setApplicationContext(Context c) {
        fbutil.setApplicationContext(c);
        cacheUtil.setApplicationContext(c);
    }

    public static Singleton getInstance(Context context) {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                    instance.setApplicationContext(context);
                    return instance;
                }
            }
        }
        return instance;
    }

    public String getUserId(){
        return this.userID;
    }


    public void getPointTypes(SingletonInterface si) {
        if (pointTypeList == null)
            fbutil.getPointTypes(new FirebaseUtilInterface() {
                @Override
                public void onPointTypeComplete(List<PointType> data) {
                    if (data != null && !data.isEmpty()) {
                        pointTypeList = data;
                        si.onPointTypeComplete(data);
                    } else {
                        si.onError(new IllegalStateException("Point Type list is empty"), fbutil.getContext());
                    }
                }
            });
        else {
            si.onPointTypeComplete(pointTypeList);
        }
    }

    public void getUnconfirmedPoints(SingletonInterface si) {
        getPointTypes(new SingletonInterface() {
            @Override
            public void onPointTypeComplete(List<PointType> data) {
                fbutil.getUnconfirmedPoints(houseName, floorName, pointTypeList, new FirebaseUtilInterface() {
                    @Override
                    public void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                        unconfirmedPointList = logs;
                        si.onUnconfirmedPointsSuccess(logs);
                    }
                });
            }
        });
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }

    public void setUserData(String floor, String house, String n, int permission, String id) {
        floorName = floor;
        houseName = house;
        name = n;
        permissionLevel = permission;
        userID = id;
        cacheUtil.writeToCache(id, floor, house, name, permission);
    }

    public boolean cacheFileExists() {
        return cacheUtil.cacheFileExists();
    }

    public void getUserDataNoCache(SingletonInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (houseName == null) {
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String name, int permission) {
                    setUserData(floor, house, name, permission, id);
                    si.onSuccess();
                }
            });
        } else
            si.onSuccess();
    }

    public void getUserData(SingletonInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (houseName == null) {
            cacheUtil.getCacheData(this);
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String name, int permission) {
                    setUserData(floor, house, name, permission, id);
                    si.onSuccess();
                }
            });
        } else
            si.onSuccess();
    }

    public void getCachedData() {
        if (houseName == null) {
            cacheUtil.getCacheData(this);
        }
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
            getPointTypes(new SingletonInterface() {
                              @Override
                              public void onPointTypeComplete(List<PointType> data) {
                                  PointType type = null;
                                  for (PointType pointType : pointTypeList) {
                                      if (pointType.getPointID() == link.getPointTypeId()) {
                                          type = pointType;
                                      }
                                  }
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
                              }
                          }
            );
        } else {
            si.onError(new Exception("Link is not enabled."), fbutil.getContext());
        }
    }

    public String getFloorName() {
        return floorName;
    }

    public void getLinkWithLinkId(String linkId, SingletonInterface si) {
        fbutil.getLinkWithId(linkId, new FirebaseUtilInterface() {
            @Override
            public void onGetLinkWithIdSuccess(Link link) {
                si.onGetLinkWithIdSuccess(link);
            }
        });
    }

    public void getPointStatistics(SingletonInterface si) {
        boolean getRewards = rewardList == null;
        fbutil.getPointStatistics(userID, getRewards, new FirebaseUtilInterface() {
            @Override
            public void onGetPointStatisticsSuccess(List<House> houses, int userPoints, List<Reward> rewards) {
                houseList = houses;
                totalPoints = userPoints;
                if (getRewards)
                    rewardList = rewards;
                si.onGetPointStatisticsSuccess(houseList, totalPoints, rewardList);
            }
        });
    }

    public void clearUserData() {
        floorName = null;
        houseName = null;
        name = null;
        permissionLevel = 0;
        userID = null;

        cacheUtil.deleteCache();
    }

    public void getFloorCodes(SingletonInterface si) {
        fbutil.getFloorCodes(new FirebaseUtilInterface() {
            @Override
            public void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {
                si.onGetFloorCodesSuccess(data);
            }
        });
    }

    public boolean showDialog(){
        return cacheUtil.showDialog();
    }


    /**
     * Get the list of QRCodes that were created by the User with userId.
     *
     * @param userId    String that represents the Firebase ID of the User who is getting QR codes.
     * @param si       SingletonInterface that has the methods onError and onGetQRCodesForUserSuccess implemented.
     */
    public void getUserCreatedQRCodes(String userId, boolean shouldRefresh, SingletonInterface si){

        if(this.userCreatedQRCodes == null || shouldRefresh) {
            //No data is currently cached or the cache needs to be refreshed
            fbutil.getQRCodesForUser(userId, new FirebaseUtilInterface() {
                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
                }

                @Override
                public void onGetQRCodesForUserSuccess(List<Link> qrCodes) {
                    setUserCreatedQRCodes(qrCodes); // Save to local Cache
                    si.onGetQRCodesForUserSuccess(qrCodes);
                }
            });
        }
        else{
            si.onGetQRCodesForUserSuccess(this.userCreatedQRCodes);
        }
    }

    private void setUserCreatedQRCodes(List<Link> codes){
        this.userCreatedQRCodes = codes;
    }



    /**
     * Create a new QRCode in the database. If the call is succesful, the new LinkId will be saved into the Link object
     *
     * @param link  Link object to be created in the database and the object that will be updated on success
     * @param si   SingletonInterface with methods onError and onSuccess
     */
    public void createQRCode(Link link, SingletonInterface si){
        fbutil.createQRCode(link, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }

    /**
     * Update a Link object in the database with a new Enabled Status
     *
     * @param link  Link object to be updated
     * @param si   SingletonInterface with method OnError and onSuccess implemented
     *
     * @Note    If it is easier to just give the link id, then this method can be changed to handle that instead.
     */
    public void setQRCodeEnabledStatus(Link link, boolean isEnabled, SingletonInterface si){
        fbutil.setQRCodeEnabledStatus(link, isEnabled, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }

    /**
     * Update a Link object in the database with a new Archived Status
     *
     * @param link  Link object to be updated
     * @param si   SingletonInterface with method OnError and onSuccess implemented
     *
     * @Note    If it is easier to just give the link id, then this method can be changed to handle that instead.
     */
    public void setQRCodeActivatedStatus(Link link, boolean isArchived, SingletonInterface si){
        fbutil.setQRCodeActivatedStatus(link, isArchived, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                si.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }


}