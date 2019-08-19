package com.branch.www.screencapture.TargetImageTool;

import java.util.ArrayList;

public class TargetImagePoints {

    private ImagePoint imagePoint;

    private ArrayList<ImagePoint> imagePoints;

    public TargetImagePoints() {
        imagePoints = new ArrayList<ImagePoint>();
    }

    public ImagePoint getImagePoint() {
        return imagePoint;
    }

    public void setImagePoint(ImagePoint imagePoint) {
        this.imagePoint = imagePoint;
        this.imagePoints.add(imagePoint);
    }

    public ArrayList<ImagePoint> getImagePoints() {
        return imagePoints;
    }

    public void setImagePoints(ArrayList<ImagePoint> imagePoints) {
        this.imagePoints = imagePoints;
    }

    public String getImagePointsString(){
        String result = "";
        for(int i = 0; i < this.imagePoints.size(); i++)
        {
            result = result + this.imagePoints.get(i).getPointString();
        }
        result = result + "}";
        return result;
    }

}
