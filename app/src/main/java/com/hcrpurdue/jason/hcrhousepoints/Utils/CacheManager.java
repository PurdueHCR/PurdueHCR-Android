package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.HouseCode;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.MessageType;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.FirebaseUtilInterface;

// Because non-global variables are for people who care about technical debt
public class CacheManager {
    private static CacheManager instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private CacheUtil cacheUtil = new CacheUtil();
    private List<PointType> pointTypeList = null;
    private ArrayList<PointLog> unconfirmedPointList = null;
    private ArrayList<PointLog> confirmedPointList = null;
    private String userID = null;
    private String floorName = null;
    private String houseName = null;
    private String name = null;
    private String firstName = null;
    private String lastName = null;
    private int permissionLevel = 0;
    private int totalPoints = 0;
    private int notificationCount = 0;
    private List<House> houseList = null;
    private List<Reward> rewardList = null;
    private ArrayList<Link> userCreatedQRCodes = null;
    private List<PointLog> personalPointLogs = null;
    private SystemPreferences sysPrefs = null;

    private CacheManager() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    private void setApplicationContext(Context c) {
        fbutil.setApplicationContext(c);
        cacheUtil.setApplicationContext(c);
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
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


    public PointType getPointTypeWithID(int pointID) {

        for (int i = 0; i < pointTypeList.size(); i++) {
            if(pointTypeList.get(i).getId() == pointID) {
                return pointTypeList.get(i);
            }
        }
        return null;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    /**
     * Get the point types which are currently cached
     * @return
     */
    public List<PointType> getCachedPointTypes(){
        return this.pointTypeList;
    }

    /**
     * REfresh the point types from the server
     * @param si
     */
    public void getUpdatedPointTypes(CacheManagementInterface si) {
        fbutil.getPointTypes(new FirebaseUtilInterface() {
            @Override
            public void onPointTypeComplete(List<PointType> data) {
                if (data != null && !data.isEmpty()) {
                    pointTypeList = data;
                    Collections.sort(pointTypeList);
                    si.onPointTypeComplete(data);
                } else {
                    si.onError(new IllegalStateException("Point Type list is empty"), fbutil.getContext());
                }
            }
        });
    }

    /**
     *
     * @param si
     *
     * Gets system preferences for House
     */
    public void getSystemPreferences(CacheManagementInterface si) {
        fbutil.getSystemPreferences(new FirebaseUtilInterface() {
            @Override
            public void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {
                if(systemPreferences != null) {
                    sysPrefs = systemPreferences;
                    si.onGetSystemPreferencesSuccess(sysPrefs);
                }
                else {
                    si.onError(new IllegalStateException("System preferences is null"), fbutil.getContext());
                }
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e, context);
            }
        });
    }

    /**
     * Gets system preferences for House
     * @return Return this current sysPRefs
     */
    public SystemPreferences getSystemPreferences() {
        return sysPrefs;
    }

    /**
     *
     * Returns cached system preferences
     * @return
     */
    public SystemPreferences getCachedSystemPreferences() {
        return sysPrefs;
    }

    public void getUnconfirmedPoints(CacheManagementInterface si) {
        fbutil.getUnconfirmedPoints(houseName, floorName, new FirebaseUtilInterface() {
            @Override
            public void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                unconfirmedPointList = logs;
                si.onUnconfirmedPointsSuccess(logs);
            }
        });
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }

    public void setUserData(String floor, String house, String first, String last, int permission, String id) {
        floorName = floor;
        houseName = house;
        firstName = first;
        lastName = last;
        permissionLevel = permission;
        userID = id;
        cacheUtil.writeToCache(id, floor, house, first, last, permission);
    }

    public boolean cacheFileExists() {
        return cacheUtil.cacheFileExists();
    }

    public void getUserDataNoCache(CacheManagementInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (houseName == null) {
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String firstName, String lastName, int permission) {
                    setUserData(floor, house, firstName,lastName, permission, id);
                    si.onSuccess();
                }
                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
                }
            });
        } else
            si.onSuccess();
    }

    public void getUserData(CacheManagementInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (houseName == null) {
            cacheUtil.getCacheData(this);
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(String floor, String house, String firstName, String lastName, int permission) {
                    setUserData(floor, house, firstName,lastName, permission, id);
                    si.onSuccess();
                }

                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
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
        return firstName+" "+lastName;
    }

    public String getHouse() {
        return houseName;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void submitPoints(String description, Date dateOccurred, PointType type, CacheManagementInterface sui) {
        PointLog log = new PointLog(description, firstName, lastName, type, floorName,userID, dateOccurred);
        boolean preApproved = permissionLevel == 1; // preaproved only RHP
        fbutil.submitPointLog(log, null, houseName, userID, preApproved, sysPrefs, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                if(preApproved){
                    PointLogMessage plm = new PointLogMessage("Preapproved", "PurdueHCR", "",getPermissionLevel(), MessageType.APPROVE);
                    fbutil.postMessageToPointLog(log, getHouse(), plm, new FirebaseUtilInterface() {
                        @Override
                        public void onSuccess() {

                            sui.onSuccess();
                        }

                        @Override
                        public void onError(Exception e, Context context) {
                            sui.onError(e,context);
                        }
                    });
                }
                else{
                    sui.onSuccess();
                }

            }
        });
    }

    public void submitPointWithLink(Link link, CacheManagementInterface sui) {
        if (link.isEnabled()) {
              PointType type = null;
              for (PointType pointType : pointTypeList) {
                  if (pointType.getId() == link.getPointTypeId()) {
                      type = pointType;
                  }
              }
              PointLog log = new PointLog(link.getDescription(), firstName, lastName, type, floorName, userID);
              fbutil.submitPointLog(log, (link.isSingleUse()) ? link.getLinkId() : null, houseName, userID, link.isSingleUse() || permissionLevel == 1, sysPrefs, new FirebaseUtilInterface() {
                  @Override
                  public void onSuccess() {
                      if(link.isSingleUse()){
                          PointLogMessage plm = new PointLogMessage("Preapproved", "PurdueHCR", "",getPermissionLevel(), MessageType.APPROVE);
                          fbutil.postMessageToPointLog(log, getHouse(), plm, new FirebaseUtilInterface() {
                              @Override
                              public void onSuccess() {

                                  sui.onSuccess();
                              }

                              @Override
                              public void onError(Exception e, Context context) {
                                  sui.onError(e,context);
                              }
                          });
                      }
                      else{
                          sui.onSuccess();
                      }
                  }

                  @Override
                  public void onError(Exception e, Context c) {
                      if (e.getLocalizedMessage().equals("Code was already submitted")) {
                          Toast.makeText(c, "You have already submitted this code.",
                                  Toast.LENGTH_SHORT).show();
                      }
                      sui.onError(e, c);

                  }
              });
        } else {
            sui.onError(new Exception("QR is not enabled."), fbutil.getContext());
        }
    }

    public String getFloorName() {
        return floorName;
    }

    public void getLinkWithLinkId(String linkId, CacheManagementInterface si) {
        fbutil.getLinkWithId(linkId, new FirebaseUtilInterface() {
            @Override
            public void onGetLinkWithIdSuccess(Link link) {
                si.onGetLinkWithIdSuccess(link);
            }

            @Override
            public void onError(Exception e, Context context) {
                si.onError(e,context);
            }
        });
    }

    public void getPointStatistics(CacheManagementInterface si) {
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

    public void getFloorCodes(CacheManagementInterface si) {
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
     * @param shouldRefresh  Boolean that represents if the app should request updated information from the server
     * @param si       CacheManagementInterface that has the methods onError and onGetQRCodesForUserSuccess implemented.
     */
    public void getUserCreatedQRCodes(boolean shouldRefresh, CacheManagementInterface si){

        if(this.userCreatedQRCodes == null || shouldRefresh) {
            //No data is currently cached or the cache needs to be refreshed
            fbutil.getQRCodesForUser(userID, new FirebaseUtilInterface() {
                @Override
                public void onError(Exception e, Context context) {
                    si.onError(e,context);
                }

                @Override
                public void onGetQRCodesForUserSuccess(ArrayList<Link> qrCodes) {
                    setUserCreatedQRCodes(qrCodes); // Save to local Cache
                    si.onGetQRCodesForUserSuccess(qrCodes);
                }
            });
        }
        else{
            si.onGetQRCodesForUserSuccess(this.userCreatedQRCodes);
        }
    }

    private void setUserCreatedQRCodes(ArrayList<Link> codes){
        this.userCreatedQRCodes = codes;
    }



    /**
     * Create a new QRCode in the database. If the call is succesful, the new LinkId will be saved into the Link object
     *
     * @param link  Link object to be created in the database and the object that will be updated on success
     * @param si   CacheManagementInterface with methods onError and onSuccess
     */
    public void createQRCode(Link link, CacheManagementInterface si){
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
     * @param si   CacheManagementInterface with method OnError and onSuccess implemented
     *
     * @Note    If it is easier to just give the link id, then this method can be changed to handle that instead.
     */
    public void setQRCodeEnabledStatus(Link link, boolean isEnabled, CacheManagementInterface si){
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
     * @param si   CacheManagementInterface with method OnError and onSuccess implemented
     *
     * @Note    If it is easier to just give the link id, then this method can be changed to handle that instead.
     */
    public void setQRCodeArchivedStatus(Link link, boolean isArchived, CacheManagementInterface si){
        fbutil.setQRCodeArchivedStatus(link, isArchived, new FirebaseUtilInterface() {
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

    public void getAllHousePoints(CacheManagementInterface si) {
        fbutil.getAllHousePoints(houseName, floorName, new FirebaseUtilInterface() {

            @Override
            public void onGetAllHousePointsSuccess(List<PointLog> houseLogs) {
                si.onGetAllHousePointsSuccess(houseLogs);
            }
        });
    }

    /**
     * Handles the updating the database for approving or denying points. It will update the point in the house and the TotalPoints for both user and house
     *
     * @param log                    PointLog:   The PointLog that is to be either approved or denied
     * @paramad approved               boolean:    Was the log approved?
     * @param sui                    FirebaseUtilInterface: Implement the OnError and onSuccess methods
     */
    public void handlePointLog(PointLog log, boolean approved, boolean updating, CacheManagementInterface sui){
        fbutil.updatePointLogStatus(log, approved, getHouse(),updating,false, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                MessageType mt = MessageType.REJECT;
                String msg = getName()+" rejected the point request.";
                if(approved){
                    msg = getName()+" approved the point request.";
                    mt = MessageType.APPROVE;
                }
                PointLogMessage plm = new PointLogMessage(msg, firstName, lastName,getPermissionLevel(), mt);
                fbutil.postMessageToPointLog(log, getHouse(), plm, new FirebaseUtilInterface() {
                    @Override
                    public void onSuccess() {

                        sui.onSuccess();
                    }

                    @Override
                    public void onError(Exception e, Context context) {
                        sui.onError(e,context);
                    }
                });
            }

            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }
        });
    }

    /**
     * Retrieve the updates to a point log object as they occur. Used to update pointlog details page
     * @param log
     * @param sui
     */
    public void handlePointLogUpdates(PointLog log, final CacheManagementInterface sui){
        fbutil.handlePointLogUpdates(log, houseName, new FirebaseUtilInterface() {
            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }

            @Override
            public void onGetPointLogMessageUpdates(List<PointLogMessage> messages) {
                sui.onGetPointLogMessageUpdates(messages);
            }
        });
    }

    /**
     * Add a message to the point log
     * @param log   Log to which the message should be posted
     * @param plm   PointLogMessage to post
     * @param sui   CacheManagementInterface with onSuccess and onError
     */
    public void postMessageToPointLog(PointLog log, PointLogMessage plm, CacheManagementInterface sui){
        fbutil.postMessageToPointLog(log, getHouse(), plm, new FirebaseUtilInterface() {
            @Override
            public void onSuccess() {
                sui.onSuccess();
            }

            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }
        });
    }

    /**
     * Add a message to the point log
     * @param log   Log to which the message should be posted
     * @param message   message to post
     * @param sui   CacheManagementInterface with onSuccess and onError
     */
    public void postMessageToPointLog(PointLog log, String message, CacheManagementInterface sui){
        PointLogMessage plm = new PointLogMessage(message, firstName, lastName, permissionLevel, MessageType.COMMENT);
        postMessageToPointLog(log,plm,sui);
    }

    /**
     * init the logs for this user
     * @param sui
     */
    public void initPersonalPointLogs(CacheManagementInterface sui){
        fbutil.getPersonalPointLogs(userID, houseName, new FirebaseUtilInterface() {
            @Override
            public void onError(Exception e, Context context) {
                sui.onError(e,context);
            }

            @Override
            public void onGetPersonalPointLogs(List<PointLog> personalLogs) {
                setPersonalPointLogs(personalLogs);
                sui.onGetPersonalPointLogs(personalLogs);
            }
        });
    }

    public void setPersonalPointLogs(List<PointLog> logs){
        personalPointLogs = logs;
    }

    public List<PointLog> getPersonalPointLogs(){
        return personalPointLogs;
    }

    /**
     * Reset the notifications for the point log and user.
     * @param log   Point log for which to reset Pointlog
     * @param resetResident FALSE: reset RHP notification count. TRUE: Reset resident count
     */
    public void resetPointLogNotificationCount(PointLog log, boolean resetResident){
        fbutil.updatePointLogNotificationCount(log, houseName, resetResident, true, new FirebaseUtilInterface() {});
    }

    /**
     * Get house codes
     * @param cmi
     */
    public void getHouseCodes(CacheManagementInterface cmi){
        fbutil.retrieveHouseCodes(new FirebaseUtilInterface() {
            @Override
            public void onError(Exception e, Context context) {
                cmi.onError(e,context);
            }

            @Override
            public void onGetHouseCodes(List<HouseCode> codes) {
                cmi.onGetHouseCodes(codes);
            }
        });
    }

}