package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcrpurdue.jason.hcrhousepoints.Models.LaundryItem;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.RecyclerAdapters.LaundryRv;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class WasherFragment extends Fragment {

    ArrayList<LaundryItem> laundryItems = new ArrayList<>();
    RecyclerView rv;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_washers, container, false);
        rv = root.findViewById(R.id.rvDryers);
        retrieveData();
        return root;
    }
    public void retrieveData() {
        SharedPreferences mySharedPreferences = getContext().getSharedPreferences("building", Context.MODE_PRIVATE);

        final LaundryRv recycler = new LaundryRv(null);
        String wr = mySharedPreferences.getString("building", "");
        if (wr != null) {
            if (wr.equals("hn")) {
                laundryItems.clear();
                //@TODO fetch laundry data
            } else {
                laundryItems.clear();
                //@TODO fetch laundry data

            }

            recycler.notifyDataSetChanged();
        }


        RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(getContext(),3);
        rv.setLayoutManager(layoutmanager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(recycler);

    }
}