package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.hcrpurdue.jason.hcrhousepoints.Activities.LaundryTabs;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.Objects;

public class Laundry extends Fragment {
    //Honors North cv
    CardView hn;
    //Honors South cv
    CardView hs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.fragment_laundry, container, false);
        hn = baseView.findViewById(R.id.cvlaundry1);
        hs = baseView.findViewById(R.id.cvlaundry2);
       // setUtilities();
        //System.out.println(APIHelper.getInstance(getContext()).getEvents());
        //Honors North Cardview click listener
hn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        Context context = v.getContext();
        SharedPreferences mySharedPreferences = context.getSharedPreferences("building", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("building","hn").apply();
        Intent intent = new Intent(getContext(), LaundryTabs.class);
        startActivity(intent);

    }
});
hs.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        SharedPreferences mySharedPreferences = context.getSharedPreferences("building", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("building","hs").apply();
        Intent intent = new Intent(getContext(), LaundryTabs.class);


        startActivity(intent);

    }
});

        return baseView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Events");
    }
}
