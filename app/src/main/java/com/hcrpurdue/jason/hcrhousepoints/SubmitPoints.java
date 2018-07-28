package com.hcrpurdue.jason.hcrhousepoints;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.PointLog;
import Models.PointType;
import Utils.FirebaseUtil;
import Utils.FirebaseUtilInterface;
import Utils.Singleton;
import Utils.SingletonInterface;

public class SubmitPoints extends AppCompatActivity {
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_points);
        singleton = Singleton.getInstance();
    }


    private void getPointTypes() {
        singleton.getPointTypes(new SingletonInterface() {
            @Override
            public void onComplete(boolean success) {
                if (success)
                {
                    List<PointType> list = singleton.getPointTypeList();
                    List<Map<String, String>> pointTypes = new ArrayList<>();
                    list.forEach()
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error retrieving point type from database, please try again later before contacting your RHP", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
