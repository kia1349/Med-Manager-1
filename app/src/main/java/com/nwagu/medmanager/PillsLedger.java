package com.nwagu.medmanager;

import java.util.ArrayList;

class PillsLedger {

    ArrayList<PillDetails> pillsArrayList = new ArrayList<>();

    PillsLedger() {

    }

    //Method to populate the pillsArrayList
    void add(int index, String pillName, String description, String interval, String startDate, String endDate, String lastDate) {
        PillDetails pillDetails = new PillDetails(index, pillName, description, interval, startDate, endDate, lastDate);
        pillsArrayList.add(pillDetails);
    }

    //the below is called a bean class
    class PillDetails{
        private int index;
        private String pillName;
        private String description;
        private String interval;
        private String startDate;
        private String endDate;
        private String lastDate;
        private boolean isDue;

        PillDetails(int index, String pillName, String description, String interval, String startDate, String endDate, String lastDate) {
            this.index = index;
            this.pillName = pillName;
            this.description = description;
            this.interval = interval;
            this.startDate = startDate;
            this.endDate = endDate;
            this.lastDate = lastDate;
        }

        int getIndex() {
            return index;
        }

        String getPillName() {
            return pillName;
        }

        String getDescription() {
            return description;
        }

        String getInterval() {
            return interval;
        }

        String getStartDate() {
            return startDate;
        }

        String getEndDate() {
            return endDate;
        }

        String getLastDate() { return lastDate; }

        boolean getDue() {return isDue;}

        void setPillName(String pillName) {
            this.pillName = pillName;
        }

        void setDescription(String description) {
            this.description = description;
        }

        void setInterval(String interval) {this.interval = interval;}

        void setLastDate(String lastDate) {this.lastDate = lastDate;}

        void setDue(boolean isDue) {this.isDue = isDue;}
    }

}