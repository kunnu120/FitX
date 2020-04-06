package com.example.fitx;

import android.app.Application;

public class logInfo extends Application {
    private double totalLogWeight;
    private double totalProgWeight;
    private double activityProgress;
    private double oldProgWeight;
    private double newProgWeight;
    private double progLogWeight;
    private double oldLogWeight;


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

    public double getOldProgWeight() {
        return oldProgWeight;
    }

    public void setOldProgWeight(double oldProgWeight) {
        this.oldProgWeight = oldProgWeight;
    }


    public void setNewProgWeight(double newProgWeight) {
        this.newProgWeight = newProgWeight;
    }

    public double getProgLogWeight() {
        return progLogWeight;
    }

    public void setProgLogWeight(double progLogWeight) {
        this.progLogWeight = progLogWeight;
    }

    public double getOldLogWeight() {return oldLogWeight;}

    public void setOldLogWeight(double oldLogWeight) {this.oldLogWeight = oldLogWeight;}

}
