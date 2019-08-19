package com.branch.www.screencapture.TargetImageTool;

public class ImagePoint {

    private int pointNum, pointX, pointY, pointR, pointG, pointB;

    public ImagePoint() {
        super();
    }

    public ImagePoint(int pointNum, int pointX, int pointY, int pointR,
                      int pointG, int pointB) {
        super();
        this.pointNum = pointNum;
        this.pointX = pointX;
        this.pointY = pointY;
        this.pointR = pointR;
        this.pointG = pointG;
        this.pointB = pointB;
    }

    public int getPointNum() {
        return pointNum;
    }

    public void setPointNum(int pointNum) {
        this.pointNum = pointNum;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public int getPointR() {
        return pointR;
    }

    public void setPointR(int pointR) {
        this.pointR = pointR;
    }

    public int getPointG() {
        return pointG;
    }

    public void setPointG(int pointG) {
        this.pointG = pointG;
    }

    public int getPointB() {
        return pointB;
    }

    public void setPointB(int pointB) {
        this.pointB = pointB;
    }

    /**
     * get point in String
     * (pointNum, pointX, pointY, pointR, pointG, pointB)
     * @return
     */
    public String getPointString(){
        return this.pointNum + "," + this.pointX + "," + this.pointY + "," + this.pointR + "," + this.pointG + "," + this.pointB + "]";
    }

}
