/**
 * Model Class for the House Code object on Firestore
 */

package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.util.HashMap;
import java.util.Map;

public class HouseCode {

    public final String CODE_KEY = "Code";
    public final String CODE_NAME_KEY = "CodeName";
    public final String PERMISSION_LEVEL = "PermissionLevel";
    public final String FLOOR_ID_KEY = "FloorId";
    public final String HOUSE_NAME_KEY = "House";

    private String code;
    private String codeName;
    private PermissionLevel permissionLevel;


    private String floorId;
    private String houseName;

    public HouseCode(String code, String codeName, PermissionLevel permissionLevel, String floorId, String houseName){
        this.code = code;
        this.codeName = codeName;
        this.permissionLevel = permissionLevel;
        this.floorId = floorId;
        this.houseName = houseName;
    }


    /**
     * init a House Code from the database
     * @param dataMap
     */
    public HouseCode(Map<String,Object> dataMap){
        this.code = (String) dataMap.get(CODE_KEY);
        this.codeName = (String) dataMap.get(CODE_NAME_KEY);
        this.permissionLevel = PermissionLevel.getPermissionLevelFromFirestore(((Long) dataMap.get(PERMISSION_LEVEL)).intValue());
        this.floorId = (String) dataMap.get(FLOOR_ID_KEY);
        this.houseName = (String) dataMap.get(HOUSE_NAME_KEY);
    }

    /**
     * Save this model into a map to upload to Firestore
     * @return
     */
    public Map<String,Object> convertToFirestoreMap(){
        Map<String,Object> map = new HashMap<>();
        map.put(CODE_KEY,this.code);
        map.put(CODE_NAME_KEY,this.codeName);
        map.put(PERMISSION_LEVEL,this.permissionLevel.getFirestoreValue());
        map.put(FLOOR_ID_KEY,this.floorId);
        map.put(HOUSE_NAME_KEY,this.houseName);
        return map;
    }

    public String getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public String getFloorId() {
        return floorId;
    }

    public String getHouseName() {
        return houseName;
    }

}
