package com.branch.www.screencapture.TargetImageTool;

public class AnalystListTargetImagePoints {

    private String source;

    private ImagePoint imagePoint;

    private TargetImagePoints targetImagePoints;

    private ListTargetImagePoints listTargetImagePoints;

    private String listTargetString = "\\}";

    private String targetImagePointString = "\\]";

    private String imagePointString = ",";

    public AnalystListTargetImagePoints(String source) {
        this.source = source;
        this.imagePoint = new ImagePoint();
        this.targetImagePoints = new TargetImagePoints();
        this.listTargetImagePoints = new ListTargetImagePoints();
        analyst(source);
    }

    public void analyst(String source){
        String[] temp1 = source.split(listTargetString);
        //list
        for(int i = 0; i < temp1.length; i++){
            String[] temp2 = temp1[i].split(targetImagePointString);
            this.targetImagePoints = new TargetImagePoints();
            //image
            for(int ii = 0; ii < temp2.length; ii++){
                String[] temp3 = temp2[ii].split(imagePointString);
                //point
                imagePoint = new ImagePoint(Integer.parseInt(temp3[0]),Integer.parseInt(temp3[1]),Integer.parseInt(temp3[2]),Integer.parseInt(temp3[3]),Integer.parseInt(temp3[4]),Integer.parseInt(temp3[5]));
                this.targetImagePoints.setImagePoint(imagePoint);
            }
            this.listTargetImagePoints.setTargetImagePoints(this.targetImagePoints);
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ImagePoint getImagePoint() {
        return imagePoint;
    }

    public void setImagePoint(ImagePoint imagePoint) {
        this.imagePoint = imagePoint;
    }

    public TargetImagePoints getTargetImagePoints() {
        return targetImagePoints;
    }

    public void setTargetImagePoints(TargetImagePoints targetImagePoints) {
        this.targetImagePoints = targetImagePoints;
    }

    public ListTargetImagePoints getListTargetImagePoints() {
        return listTargetImagePoints;
    }

    public void setListTargetImagePoints(ListTargetImagePoints listTargetImagePoints) {
        this.listTargetImagePoints = listTargetImagePoints;
    }
}
