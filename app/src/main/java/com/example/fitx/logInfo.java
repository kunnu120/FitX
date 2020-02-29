package com.example.fitx;

import android.app.Application;

public class logInfo extends Application {
    private double totalLogWeight;
    private double totalProgWeight;
    private double activityProgress;
    private double oldProgress;
    private double newProgress;
    private int logWeight;


    public logInfo() {

    }

    public double getTotalLogWeight() {
        return totalLogWeight;
    }

    public void setTotalLogWeight(double totalLogWeight) {
        this.totalLogWeight = totalLogWeight;
    }

    public double getTotalProgWeight() {
        return totalProgWeight;
    }

    public void setTotalProgWeight(double totalProgWeight) {
        this.totalProgWeight = totalProgWeight;
    }

    public double getActivityProgress() {
        return activityProgress;
    }

    public void setActivityProgress(double activityProgress) {
        this.activityProgress = activityProgress;
    }

    public double getOldProgress() {
        return oldProgress;
    }

    public void setOldProgress(double oldProgress) {
        this.oldProgress = oldProgress;
    }

    public double getNewProgress() {
        return newProgress;
    }

    public void setNewProgress(double newProgress) {
        this.newProgress = newProgress;
    }

    public int getLogWeight() {
        return logWeight;
    }

    public void setLogWeight(int logWeight) {
        this.logWeight = logWeight;
    }
}
