package com.hcrpurdue.jason.hcrhousepoints.ListAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.hcrpurdue.jason.hcrhousepoints.Event;

import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EventsPageAdapter extends RecyclerView.Adapter<EventsPageAdapter.MyHolder>{

    List<Event> events;


    public EventsPageAdapter(List<Event> events) {
        this.events = events;

    }
    @Override
    public EventsPageAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(/*xml card view*/,parent,false);

        EventsPageAdapter.MyHolder myHolder = new EventsPageAdapter.MyHolder(view);
        return myHolder;
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(final EventsPageAdapter.MyHolder holder, final int position) {

        //manages setting
    }
    @Override
    public int getItemCount() {
        return events.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        //instances of things in holder
        public MyHolder(View itemView) {
            super(itemView);
            //assignment of things in holder
        }
    }




}
