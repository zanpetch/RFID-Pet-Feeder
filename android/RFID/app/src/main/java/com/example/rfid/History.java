package com.example.rfid;

class History {
    private String type;
    private String date;
    private int eatWeight;
    private String time;

    public History(){

    }

    public History(String date, int eatWeight, String type, String time) {
        this.date = date;
        this.eatWeight = eatWeight;
        this.type = type;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public int getEatWeight() {
        return eatWeight;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEatWeight(int eatWeight) {
        this.eatWeight = eatWeight;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
