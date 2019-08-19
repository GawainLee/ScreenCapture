package com.branch.www.screencapture;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.branch.www.screencapture.TargetImageTool.ImagePoint;
import com.branch.www.screencapture.TargetImageTool.ListTargetImagePoints;
import com.branch.www.screencapture.TargetImageTool.TargetImagePoints;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.sqrt;

public class FindTargetImageByPoint {

    public boolean findTarget(Bitmap sourceBitmap, ArrayList<ImagePoint> targetImagePoints)
    {
        boolean reuslt = false;
        int h = sourceBitmap.getHeight();
        int w = sourceBitmap.getWidth();
        int targetSizt = targetImagePoints.size();
        for(int y = 0; y < h; y++){

            ImagePoint firstImagePoint = targetImagePoints.get(0);
            int firstImagePointY = firstImagePoint.getPointY();
            int firstColor = sourceBitmap.getPixel(firstImagePoint.getPointX(), y);
            //check first point
            if (checkColor(firstColor, firstImagePoint))
            {
                //check other points
                for(int i = 1; i < targetSizt; i++)
                {
                    ImagePoint imagePoint = targetImagePoints.get(i);
                    int tempImagePointY = y + (imagePoint.getPointY() - firstImagePointY);
                    int color = sourceBitmap.getPixel(imagePoint.getPointX(), tempImagePointY);
                    if (checkColor(color,imagePoint))
                    {
                        reuslt = true;
                    }
                    else
                    {
                        reuslt = false;
                        break;
                    }
                }
                if(reuslt)
                {
                    return true;
                }
            }
        }
        return reuslt;
    }

    private double differentRate = 0.01;
    public boolean checkColor(int sourceColor, ImagePoint imagePoint){
        int sourceColorRed = Color.red(sourceColor);
        int sourceColorGreen = Color.green(sourceColor);
        int sourceColorBlue = Color.blue(sourceColor);
        double r3 =( sourceColorRed - imagePoint.getPointR()) / 256.0000;
        double g3 = (sourceColorGreen - imagePoint.getPointG()) / 256.0000;
        double b3 = (sourceColorBlue - imagePoint.getPointB()) / 256.0000;

        double diff = sqrt(r3 * r3 + g3 * g3 + b3 * b3);
//                System.out.println(diff);
        //diff more small, more like the target color, more good
        if(diff < differentRate)
        {
//            System.out.println("SourceColor: Red:" + sourceColorRed + " Green:" + sourceColorGreen + " Blue:" + sourceColorBlue);
//            System.out.println("TargetColor: Red:" + targetPoint.getPointRed() + " Green:" + targetPoint.getPointGreen() + " Blue:" + targetPoint.getPointBlue());
//            System.out.println("Different Rate:" + diff);
            return true;
        }
        else {
            return false;
        }
    }
}
