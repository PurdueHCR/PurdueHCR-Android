package com.hcrpurdue.jason.hcrhousepoints.Views;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HouseCompetitionCard {
    private BarChart houseChart;
    private CacheManager cacheManager;
    private Resources resources;
    private String packageName;
    private Context context;

    private List<House> allHouses = new ArrayList<>();


    public HouseCompetitionCard(Context context, View parentView) {
        houseChart = parentView.findViewById(R.id.statistics_point_chart);

        this.cacheManager = CacheManager.getInstance(context);
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
        this.context = context;
        handleHouseUpdate();
        setupGraph();
        populateGraphCard();
    }

    /**
     * Initialize the graph of the house points
     */
    private void setupGraph(){
        houseChart.getAxisLeft().setAxisMinimum(0);
        houseChart.getAxisLeft().setEnabled(false);
        houseChart.getAxisRight().setEnabled(false);
        houseChart.getXAxis().setEnabled(false);
        houseChart.getDescription().setEnabled(false);
        houseChart.setScaleEnabled(false);
        houseChart.setTouchEnabled(false);
        houseChart.setDrawBorders(false);
        houseChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        houseChart.setAutoScaleMinMaxEnabled(true);

        if(cacheManager.getSystemPreferences().isCompetitionVisible()){
            houseChart.setNoDataText("Loading House Points...");
        }
        else{
            houseChart.setNoDataText("The Competition Standings Have Been Hidden");
        }

        houseChart.invalidate();
    }


    private void populateGraphCard(){
        List<IBarDataSet> dataSetList = new ArrayList<>();
        // So that things are in podium order (4, 2, 1, 3, 5)


        float i = 0f;
        for (House house : allHouses) {
            String houseName = house.getName();
            BarEntry barEntry = new BarEntry(i++, house.getPointsPerResident());
            ArrayList<BarEntry> barEntryList = new ArrayList<>();
            barEntryList.add(barEntry);
            BarDataSet dataSet = new BarDataSet(barEntryList, houseName);
            dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
                DecimalFormat formater = new DecimalFormat("###.##");
                return formater.format(value);
            });
            int colorID = ContextCompat.getColor(context, resources.getIdentifier(houseName.toLowerCase(), "color", packageName));
            dataSet.setColor(colorID);
            dataSet.setValueTextSize(12);
            dataSetList.add(dataSet);
        }
        BarData barData;

        if(cacheManager.getSystemPreferences().isCompetitionVisible()){
            barData = new BarData(dataSetList);
        }
        else{
            barData = null;
        }

        houseChart.setData(barData);
        houseChart.notifyDataSetChanged();
        houseChart.invalidate();
    }

    /**
     * Call this when the House Listener is notified
     */
    public void handleHouseUpdate(){
        allHouses = cacheManager.getHouses();
        if(allHouses == null || allHouses.size() < 5){
            populateGraphCard();
        }
        else{
            Collections.sort(allHouses);
            Collections.swap(allHouses, 0, 2);
            Collections.swap(allHouses, 0, 3);
            populateGraphCard();
        }

    }

    /**
     * Call this when the system preference update is called
     */
    public void handleSysPrefUpdate(){
        setupGraph();
        populateGraphCard();
    }

}
