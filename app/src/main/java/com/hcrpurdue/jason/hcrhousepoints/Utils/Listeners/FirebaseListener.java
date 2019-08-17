package com.hcrpurdue.jason.hcrhousepoints.Utils.Listeners;

import android.content.Context;

import com.google.firebase.firestore.ListenerRegistration;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SnapshotInterface;

import java.util.HashMap;
import java.util.Map;

public abstract class FirebaseListener {

    protected Map<String, ListenerCallbackInterface> callbackByStringMap = new HashMap<>();
    protected CacheManager cacheManager;
    protected Context context;
    protected ListenerRegistration listener;
    protected SnapshotInterface si;

    public FirebaseListener(Context context, SnapshotInterface si){
        this.context = context;
        cacheManager = CacheManager.getInstance(context);
        this.si = si;
    }

    public void handleCallbacks(){
        for(String key: callbackByStringMap.keySet()){
            callbackByStringMap.get(key).onUpdate();
        }
    }


    public void addCallback(String key,ListenerCallbackInterface lci){
        if(!callbackByStringMap.containsKey(key)) {
            callbackByStringMap.put(key, lci);
        }
    }

    public void removeCallback(String key){
        if(callbackByStringMap.containsKey(key)){
            callbackByStringMap.remove(key);
        }
    }

    /**
     * Call this only when you are completely finished with the Listener
     */
    public void killListener(){
        if(listener != null){
            listener.remove();
            listener = null;
            callbackByStringMap.clear();
        }
    }
}
