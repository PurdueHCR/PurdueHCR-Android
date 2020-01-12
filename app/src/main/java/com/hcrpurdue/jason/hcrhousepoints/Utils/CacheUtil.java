package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class CacheUtil {
    private Context context;
    private final String FILE_NAME = "user_data";
    private final String DIALOG_FILE_NAME = "dialog_shown";

    public boolean showDialog() {
        try {
            File file = new File(context.getCacheDir(), DIALOG_FILE_NAME);
            if (file.exists())
                return false;
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write("shown");
            fileWriter.close();
            return true;
        } catch (IOException e) {
            Toast.makeText(context, "Error with cache", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Error getting dialog cache", e);
            return false;
        }
    }

    public void setApplicationContext(Context c) {
        context = c;
    }

    public void writeUserToCache(String userID, User user) {
        try {
            File file = new File(context.getCacheDir(), FILE_NAME);
            FileWriter fileWriter = new FileWriter(file, false);
            String fileContent = "userID:" + userID +
                    "\nfloorName:" + user.getFloorId() +
                    "\nhouseName:" + user.getHouseName() +
                    "\nfirstName:" + user.getFirstName() +
                    "\nlastName:"  + user.getLastName() +
                    "\ntotalPoints" + user.getTotalPoints() +
                    "\npermissionLevel:" + user.getPermissionLevel().getServerValue();
            fileWriter.write(fileContent);
            fileWriter.close();
        } catch (IOException e) {
            Toast.makeText(context, "Error writing cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Error writing cache file", e);
        }
    }

    public boolean cacheFileExists() {
        return new File(context.getCacheDir(), FILE_NAME).exists();
    }

    /**
     * Get any cached user data that is available
     * @return the user that was found
     */
    public User getCachedUserData() {
        try {
            String userID = null;
            String floorName = null;
            String houseName = null;
            String first = null;
            String last = null;
            int totalPoints = 0;
            UserPermissionLevel permissionLevel = UserPermissionLevel.RESIDENT;
            String line;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(context.getCacheDir(), FILE_NAME)));

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    switch (key) {
                        case "userID":
                            userID = value;
                            break;
                        case "floorName":
                            floorName = value;
                            break;
                        case "houseName":
                            houseName = value;
                            break;
                        case "firstName":
                            first = value;
                            break;
                        case "lastName":
                            last = value;
                            break;
                        case "totalPoints":
                            totalPoints = Integer.parseInt(value);
                        case "permissionLevel":
                            permissionLevel = UserPermissionLevel.fromServerValue(Integer.parseInt(value));
                            break;
                    }
                }
            }
            bufferedReader.close();
            User user = new User(userID, first, last, floorName, houseName, permissionLevel, totalPoints);

            return user;
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Could not find cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Could not find cache file", e);
            return null;
        } catch (IOException e) {
            Toast.makeText(context, "Error reading cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Error reading cache file", e);
            return null;
        }
    }

    public void deleteCache() {
        if (!new File(context.getCacheDir(), FILE_NAME).delete()) {
            Toast.makeText(context, "Could not delete cache file", Toast.LENGTH_SHORT).show();
            Log.e("CacheUtil", "Could not delete cache file");
        }
    }
}
