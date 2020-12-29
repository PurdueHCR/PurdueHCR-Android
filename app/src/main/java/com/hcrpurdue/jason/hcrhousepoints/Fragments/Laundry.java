package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.hcrpurdue.jason.hcrhousepoints.Activities.LaundryActivity;
import com.hcrpurdue.jason.hcrhousepoints.Activities.LaundryTabs;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;

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
        Intent intent = new Intent(getContext(), LaundryTabs.class);
        //passes intent varaible with name building and value hn "
        intent.putExtra("building","hn");
        startActivity(intent);

    }
});
hs.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(),LaundryActivity.class);
        //passes intent varaible with name building and value hn "
        intent.putExtra("building","hs");
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
