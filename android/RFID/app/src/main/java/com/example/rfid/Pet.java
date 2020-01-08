package com.example.rfid;

import java.util.ArrayList;
import java.util.HashMap;

public class Pet {
    private HashMap<String,ArrayList<History>> historys;
    private String tagID;
    private String name;
    private String type;
    private String eatType;
    private int eatWeight;
    private int currentEatWeight;
    private String primaryKey;

    public  Pet(){
        this.historys = new HashMap<String, ArrayList<History>>();
        this.currentEatWeight = 0;
        this.primaryKey = "";
    }

    public Pet(String tagID, String name, String type, String eatType, int eatWeight) {
        this.tagID = tagID;
        this.name = name;
        this.type = type;
        this.eatType = eatType;
        this.eatWeight = eatWeight;

        this.historys = new HashMap<String, ArrayList<History>>();
        this.currentEatWeight = 0;
        this.primaryKey = "";
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEatWeight() {
        return eatWeight;
    }

    public void setEatWeight(int eatWeight) {
        this.eatWeight = eatWeight;
    }

    public String getEatType() {
        return eatType;
    }

    public void setEatType(String eatType) {
        this.eatType = eatType;
    }

    public HashMap<String, ArrayList<History>> getHistorys() {
        return historys;
    }

    public int getCurrentEatWeight() {
        return currentEatWeight;
    }

    public void setCurrentEatWeight(int currentEatWeight) {
        this.currentEatWeight = currentEatWeight;
    }

    public void setPrimaryKey(String id) {
        this.primaryKey = id;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }
}
