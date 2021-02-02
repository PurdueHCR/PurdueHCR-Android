package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.util.ArrayList;

public class PointTypeList {
    private ArrayList<PointType> pointTypes;

    public PointTypeList(ArrayList<PointType> pointTypes) {
        this.pointTypes = pointTypes;
    }

    public ArrayList<PointType> getPointTypes() {
        return pointTypes;
    }

    public void setPointTypes(ArrayList<PointType> pointTypes) {
        this.pointTypes = pointTypes;
    }
}
