package com.hcrpurdue.jason.hcrhousepoints.Models;

public class LaundryItem {
    String name;
    String status;

    public LaundryItem(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
