package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.HouseCode;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.MessageType;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseCodeMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.User;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.FirebaseUtilInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Because non-global variables are for people who care about technical debt
public class CacheManager {
    private static CacheManager instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();
    private CacheUtil cacheUtil = new CacheUtil();
    private List<PointType> pointTypeList = null;
    private User user = null;
    private int notificationCount = 0;
    private List<House> houseList = null;
    private List<Reward> rewards = null;
    private ArrayList<Link> userCreatedQRCodes = null;
    private List<PointLog> personalPointLogs = null;
    private List<PointLog> rHPNotificationLogs = null;
    private SystemPreferences sysPrefs = null;
    private AuthRank userRank = null;
    private Context context;
    private String authToken;

    private CacheManager() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    public String getAuthToken(){
        return this.authToken;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    private void setApplicationContext(Context c) {
        this.context = c;
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
        return this.user.getUserId();
    }


    public PointType getPointTypeWithID(int pointID) {

        for (int i = 0; i < pointTypeList.size(); i++) {
            if(pointTypeList.get(i).getId() == pointID) {
                return pointTypeList.get(i);
            }
        }
        return null;
    }

    /**
     * Notification count is set by the RHPNotificationListener. It will be updated whenever
     *  a user posts a message to a point log
     * @return
     */
    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    /**
     * Get a name for the permission level.  Normally "House" - FloorId Rank
     *
     * @return String
     */
    public String getHouseAndPermissionName(){
        switch (user.getPermissionLevel()){
            case RESIDENT:
                return getHouseName()+ " - "+getFloorName();
            case RHP:
                return getHouseName()+ " - "+getFloorName()+" RHP";
            case PROFESSIONAL_STAFF:
                return "REC";
            case FHP:
                return getHouseName()+ " - FHP";
            case PRIVILEGED_RESIDENT:
                return getHouseName()+ " - "+getFloorName()+ " Privileged";
            default :
                return getHouseName();
        }
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
        fbutil.getUnconfirmedPoints(user.getHouseName(), user.getFloorId(), new FirebaseUtilInterface() {
            @Override
            public void onGetUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                si.onUnconfirmedPointsSuccess(logs);
            }
        });
    }

    public List<PointType> getPointTypeList() {
        if(pointTypeList == null){
            pointTypeList = new ArrayList<>();
        }
        return pointTypeList;
    }

    public void setPointTypeList(List<PointType> pointTypes){
        this.pointTypeList = pointTypes;
    }

    public void setUserAndCache(User user, String id) {
        this.user = user;
        cacheUtil.writeUserToCache(id, this.user);
    }

    private void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    public boolean cacheFileExists() {
        return cacheUtil.cacheFileExists();
    }

    public void getUserDataNoCache(CacheManagementInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (user == null) {
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(User user) {
                    setUserAndCache(user, id);
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

    /**
     * Retrieve the User data from the server. This method only sets the cache manager's User object
     * @param si
     */
    public void getUserData(CacheManagementInterface si) {
        String id = FirebaseAuth.getInstance().getUid();
        if (getHouseName() == null) {
            user = cacheUtil.getCachedUserData();
            fbutil.getUserData(id, new FirebaseUtilInterface() {
                @Override
                public void onUserGetSuccess(User user) {
                    setUser(user);
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

    /**
     * Check the cache if data exists and set the User object in the CacheManager
     */
    public void getCachedData() {
        if (user == null) {
            user = cacheUtil.getCachedUserData();
        }
    }

    public String getName() {
        if(user == null){
            return null;
        }
        else {
            return user.getFirstName() + " " + user.getLastName();
        }
    }

    public String getHouseName() {
        if(user == null){
            return null;
        }
        else {
            return user.getHouseName();
        }
    }

    public House getUserHouse(){
        for(House house: getHouses()){
            if(house.getName().equals(user.getHouseName()))
                return house;
        }
        return null;
    }

    public UserPermissionLevel getPermissionLevel() {
        if(user == null){
            return null;
        }
        else {
            return user.getPermissionLevel();
        }
    }

    public void submitPoints(String description, Date dateOccurred, PointType type, CacheManagementInterface cacheManagementInterface) {

        APIHelper.getInstance(context).submitPoint(description,type.getId(), dateOccurred).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if(response.isSuccessful()){
                    System.out.println("Success submitted point");
                    cacheManagementInterface.onSuccess();
                }
                else{
                    try{
                        System.out.println("GOT Error: "+response.errorBody().string());
                        cacheManagementInterface.onHttpError(new ResponseCodeMessage(response.code(), response.errorBody().string()));
                    }
                    catch (IOException err){
                        cacheManagementInterface.onError(err, context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                cacheManagementInterface.onError(new Exception(t.getMessage()), context);
            }
        });

    }

    public void submitPointWithLink(Link link, CacheManagementInterface cacheManagementInterface) {
        if (link.isEnabled()) {
            APIHelper.getInstance(context).submitLink(link.getLinkId()).enqueue(new Callback<ResponseMessage>() {
                @Override
                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                    if(response.isSuccessful()){
                        System.out.println("Success submitted point");
                        cacheManagementInterface.onSuccess();
                    }
                    else{
                        try{
                            System.out.println("GOT Error: "+response.errorBody().string());
                            cacheManagementInterface.onError(new Exception("Sorry. There was an error submitting the points. Please Try again."),context);
                        }
                        catch (IOException err){
                            cacheManagementInterface.onError(err, context);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                    cacheManagementInterface.onError(new Exception(t.getMessage()), context);
                }
            });
        } else {
            cacheManagementInterface.onError(new Exception("This QR code is not enabled."), fbutil.getContext());
        }
    }

    public String getFloorName() {
        if(user == null){
            return null;
        }
        else {
            return user.getFloorId();
        }
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

    public void clearUserData() {
        user = null;
        cacheUtil.deleteCache();
    }

    /**
     * Refresh the point types from the server
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
     * Get the list of QRCodes that were created by the User with userId.
     *
     * @param shouldRefresh  Boolean that represents if the app should request updated information from the server
     * @param si       CacheManagementInterface that has the methods onError and onGetQRCodesForUserSuccess implemented.
     */
    public void getUserCreatedQRCodes(boolean shouldRefresh, CacheManagementInterface si){

        if(this.userCreatedQRCodes == null || shouldRefresh) {
            //No data is currently cached or the cache needs to be refreshed
            fbutil.getQRCodesForUser(user.getUserId(), new FirebaseUtilInterface() {
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

    public void createUser(String firstName, String lastName, String houseCode, CacheManagementInterface si){
        APIHelper.getInstance(context).createUser(firstName,lastName, houseCode).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("RESPONSE BODY: "+response.raw().toString());
                if(response.isSuccessful()){
                    System.out.println("Success created user");
                    si.onHttpCreateUserSuccess(response.body());
                }
                else{
                    try{
                        System.out.println("GOT Error: "+response.errorBody().string());
                        si.onHttpError(new ResponseCodeMessage(response.code(), response.errorBody().string()));
                    }
                    catch (IOException err){
                        si.onError(err, context);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                si.onError(new Exception(t.getMessage()), context);
            }
        });
    }


    /**
     * Create a new QRCode in the database. If the call is succesful, the new LinkId will be saved into the Link object
     *
     * @param link  Link object to be created in the database and the object that will be updated on success
     * @param si   CacheManagementInterface with methods onError and onSuccess
     */
    public void createQRCode(Link link, CacheManagementInterface si){
        APIHelper.getInstance(context).createLink(link.getDescription(), link.getPointTypeId(), link.isSingleUse()).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                System.out.println("GOT CODE: "+response.code());
                if(response.isSuccessful()) {
                    System.out.println("GOT RESPONSE: "+response.body().getMessage());
                    si.onHttpSuccess(new ResponseCodeMessage(response.code(), response.body().getMessage()));
                }
                else{
                    try{
                        System.out.println("GOT Error: "+response.errorBody().string());
                        si.onHttpError(new ResponseCodeMessage(response.code(), response.errorBody().string()));
                    }
                    catch (IOException err){
                        si.onError(err, context);
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                si.onError(new Exception(t.getMessage()), context);
            }
        });
    }

    /**
     * Update a QRCode in the database. If the call is succesful, the new LinkId will be saved into the Link object
     *
     * @param si   CacheManagementInterface with methods onError and onSuccess
     */
    public void updateQRCode(String linkId ,Map<String, Object> data, CacheManagementInterface si){
        APIHelper.getInstance(context).updateLink(linkId, data).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if(response.isSuccessful()) {
                    System.out.println("GOT RESPONSE: "+response.body().getMessage());
                    si.onHttpSuccess(new ResponseCodeMessage(response.code(), response.body().getMessage()));
                }
                else{
                    try {
                        System.out.println("ERROR CODE: " + response.code());
                        System.out.println("GOT Error: " + response.errorBody().string());
                        si.onHttpError(new ResponseCodeMessage(response.code(), "Failure"));
                    }
                    catch (IOException err){
                        si.onError(err, context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                si.onError(new Exception(t.getMessage()), context);
            }
        });
    }

    public void getAllHousePoints(CacheManagementInterface si) {
        fbutil.getAllHousePoints(user.getHouseName(), user.getFloorId(), new FirebaseUtilInterface() {

            @Override
            public void onGetAllHousePointsSuccess(List<PointLog> houseLogs) {
                si.onGetAllHousePointsSuccess(houseLogs);
            }
        });
    }

    public void approvePointLog(PointLog log, CacheManagementInterface cacheManagementInterface){
        APIHelper.getInstance(context).approvePointLog(log.getLogID()).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if(response.isSuccessful()) {
                    cacheManagementInterface.onSuccess();
                }
                else{
                    try {
                        System.out.println("ERROR CODE: " + response.code());
                        System.out.println("GOT Error: " + response.errorBody().string());
                        cacheManagementInterface.onHttpError(new ResponseCodeMessage(response.code(), "Failure"));
                    }
                    catch (IOException err){
                        cacheManagementInterface.onError(err, context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                cacheManagementInterface.onError(new Exception(t.getMessage()), context);
            }
        });
    }

    public void rejectPointLog(PointLog log, String reason, CacheManagementInterface cacheManagementInterface){
        APIHelper.getInstance(context).rejectPointLog(log.getLogID(), reason).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if(response.isSuccessful()) {
                    cacheManagementInterface.onSuccess();
                }
                else{
                    try {
                        System.out.println("ERROR CODE: " + response.code());
                        System.out.println("GOT Error: " + response.errorBody().string());
                        cacheManagementInterface.onHttpError(new ResponseCodeMessage(response.code(), "Failure"));
                    }
                    catch (IOException err){
                        cacheManagementInterface.onError(err, context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                cacheManagementInterface.onError(new Exception(t.getMessage()), context);
            }
        });
    }

    /**
     * Retrieve the updates to a point log object as they occur. Used to update pointlog details page
     * @param log
     * @param sui
     */
    public void handlePointLogUpdates(PointLog log, final CacheManagementInterface sui){
        fbutil.handlePointLogUpdates(log, getHouseName(), new FirebaseUtilInterface() {
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
     * @param message   message to post
     * @param cacheManagementInterface   CacheManagementInterface with onSuccess and onError
     */
    public void postMessageToPointLog(PointLog log, String message, CacheManagementInterface cacheManagementInterface){
        APIHelper.getInstance(context).postPointLogMessage(log.getLogID(), message).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if(response.isSuccessful()) {
                    System.out.println("GOT RESPONSE: "+response.body().getMessage());
                    cacheManagementInterface.onSuccess();
                }
                else{
                    try {
                        System.out.println("ERROR CODE: " + response.code());
                        System.out.println("GOT Error: " + response.errorBody().string());
                        cacheManagementInterface.onError(new Exception(response.errorBody().string()), context);
                    }
                    catch (IOException err){
                        cacheManagementInterface.onError(err, context);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                cacheManagementInterface.onError(new Exception(t.getMessage()), context);
            }
        });
    }

    /**
     * init the logs for this user
     * @param sui
     */
    public void initPersonalPointLogs(CacheManagementInterface sui){
        fbutil.getPersonalPointLogs(user.getUserId(), getHouseName(), new FirebaseUtilInterface() {
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
     */
    public void resetPointLogNotificationCount(PointLog log){
        APIHelper.getInstance(context).viewMessages(log.getLogID()).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if(response.isSuccessful()){
                    System.out.println("Did delete messages?");
                }
                else{
                    System.out.println(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                System.err.println("Did not read messages");
            }
        });
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


    /**
     * Get the rank of the user
     *
     * @param context
     * @param cmi
     */
    public void getUserRank(Context context, CacheManagementInterface cmi){
        if(userRank == null){
            refreshUserRank(context,cmi);
        }
        else{
            cmi.onGetRank(userRank);
        }
    }


    /**
     * Refresh the rank from the server
     * @param context
     * @param cmi
     */
    public void refreshUserRank(Context context, CacheManagementInterface cmi){
        APIHelper.getInstance(context).getRank().enqueue(new retrofit2.Callback<AuthRank>() {
            @Override
            public void onResponse(Call<AuthRank> call, Response<AuthRank> response) {
                System.out.println("GOT A RESPONSE FROM RANK");
                if(response.isSuccessful()) {
                    userRank = response.body();
                    cmi.onGetRank(userRank);
                }
                else {
                    System.out.println(response.code() + ": " + response.message());
                    cmi.onError(new Exception(response.code() + ": " + response.message()), context);
                }
            }

            @Override
            public void onFailure(Call<AuthRank> call, Throwable t) {
                System.out.println("ERROR ON GET RANK: "+t.getMessage());
                cmi.onError(new Exception(t.getMessage()), context);
            }
        });
    }

    /**
     * Set RHPNotificationLog List (Usually from Listener)
     * @param logs
     */
    public void setRHPNotificationLogs(List<PointLog> logs){
        rHPNotificationLogs = logs;
    }

    /**
     * Get RHPNotificationLog List
     * @return
     */
    public List<PointLog> getRHPNotificationLogs(){
        if(rHPNotificationLogs == null)
            rHPNotificationLogs = new ArrayList<>();
        return rHPNotificationLogs;
    }

    public void setHouseList(List<House> houses){
        this.houseList = houses;
    }

    public List<House> getHouses(){
        if(houseList == null)
            houseList = new ArrayList<>();
        return houseList;
    }

    public List<Reward> getRewards(){
        if(rewards == null)
            rewards = new ArrayList<>();
        return rewards;
    }

    public void setRewards(List<Reward> rewards){
        this.rewards = rewards;
    }

}