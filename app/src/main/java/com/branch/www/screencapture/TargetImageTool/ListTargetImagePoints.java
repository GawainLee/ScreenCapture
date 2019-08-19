package com.branch.www.screencapture.TargetImageTool;

import java.util.ArrayList;

public class ListTargetImagePoints {

    private TargetImagePoints targetImagePoints;

    private ArrayList<TargetImagePoints> listTargetImagePoints;

    public ListTargetImagePoints() {
        listTargetImagePoints = new ArrayList<TargetImagePoints>();
    }

    public String getListTargetImagePointsString(){
        String result = "";
        for(int i = 0; i < this.listTargetImagePoints.size(); i++){
            result = result + this.listTargetImagePoints.get(i).getImagePointsString();
        }
        return result;
    }

    public TargetImagePoints getTargetImagePoints() {
        return targetImagePoints;
    }

    public void setTargetImagePoints(TargetImagePoints targetImagePoints) {
        this.targetImagePoints = targetImagePoints;
        this.listTargetImagePoints.add(targetImagePoints);
    }

    public ArrayList<TargetImagePoints> getListTargetImagePoints() {
        return listTargetImagePoints;
    }

    public void setListTargetImagePoints(
            ArrayList<TargetImagePoints> listTargetImagePoints) {
        this.listTargetImagePoints = listTargetImagePoints;
    }

}
