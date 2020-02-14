package com.hcrpurdue.jason.hcrhousepoints.Models;

public class AuthRank {
    private int houseRank;
    private int semesterRank;

    public AuthRank(int houseRank, int semesterRank) {
        this.houseRank = houseRank;
        this.semesterRank = semesterRank;
    }

    public int getHouseRank() {
        return houseRank;
    }

    public int getSemesterRank() {
        return semesterRank;
    }
}
