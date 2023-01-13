package org.example;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple pipeline that crops the input image around the cone,
 * and outputs the parking zone to park in (0, 1, or 2) if the input image is Red, Blue, or Green.
 */
public class RGBPipeline extends OpenCvPipeline {
    List<Mat> bgrChannels = new ArrayList<>(3);
    private Mat bgrInput = new Mat();
    private Mat output = new Mat();
    private Mat submat;
    private int parkingZone = -1;

    //TODO Test this extensively. Originally written for Desktop OpenCV so there may be bugs!

    @Override
    public Mat processFrame(Mat input) {

        //INCLUDE THIS LINE ON ROBOT VERSION. EASYOPENCV DOES RGBA, OPENCV DOES BGR!!!!!!!!!!!!
//        Imgproc.cvtColor(input, bgrInput, Imgproc.COLOR_RGBA2BGR);

        //DELETE THIS LINE ON ROBOT VERSION.
        bgrInput = input;


        //This line needs configuration. Crops the image around the cone.
        submat = cropCenter(bgrInput, 1.0, 0.0, 0.0);

        //Splits our cropped image into its three color channels, B, G, and R.
        Core.split(submat, bgrChannels);

        //Calculates the average of each of the three color channels.
        double meanB = Core.mean(bgrChannels.get(0)).val[0];
        double meanG = Core.mean(bgrChannels.get(1)).val[0];
        double meanR = Core.mean(bgrChannels.get(2)).val[0];

        double max = Math.max(meanB, Math.max(meanG, meanR));

        //If the average of G is biggest, output 2. If it's B, output 1, if it's R, output 0
        if(max == meanB) parkingZone = 2;
        else if(max == meanG) parkingZone = 1;
        else parkingZone = 0;

        System.out.println("Park in zone " + parkingZone);

        //INCLUDE THIS LINE ON ROBOT VERSION. CONVERTS THE IMAGE BACK TO RGB TO DISPLAY ON PHONE.
//        Imgproc.cvtColor(submat, output, Imgproc.COLOR_BGR2RGBA);

        //DELETE THIS LINE ON ROBOT VERSION
        output = submat;

        return output;
    }

    /**
     * Returns a value from -1 to 2 depending on the parking zone. 0 - 2 corresponds to R, B, and G zones (yes). -1 means val hasn't been calculated yet.
     * @return  zone to park in. -1 means hasn't been calculated yet
     */
    public int getParkingZone() {
        return parkingZone;
    }

    /**
     * Crops the given mat based off the center.
     * @param input the input frame to crop
     * @param percent the percent to crop. 0 to 1.
     * @param offsetX the x offset of the image. -1 to 1
     * @param offsetY the y offset of the image. -1 to 1
     * @return the modified frame
     */
    private Mat cropCenter(Mat input, double percent, double offsetX, double offsetY) {
        int rows = (int)(input.rows() * percent);
        int cols = (int)(input.cols() * percent);
        int x = (int)(input.rows() * offsetX);
        int y = (int)(input.cols() * offsetY);

        return input.submat(new Rect(x, y, Math.min(rows, cols), Math.min(rows, cols)));

//        return input.submat(new Rect(x, y, rows, cols));

//        double x = rangeClip(offsetX, -1.0, 1.0) * (double)(centerCol - (cols/2));
//        double y = rangeClip(offsetY, -1.0, 1.0) * (double)(centerRow - (rows/2));

//        return input.submat(new Rect(
//                new Point((double)(centerCol - (cols/2)) + x, (double)(centerRow - (rows/2))+ y),
//                new Point((double)(centerCol - (cols/2)) + cols + x, (double)(centerRow - (rows/2)) + rows + y)
//        ));
    }

    private Mat cropenter(Mat input, double percent, double offsetX, double offsetY) {

        double p = rangeClip(percent, 0.0, 1.0);
        int centerRow = input.rows()/2;
        int centerCol = input.cols()/2;
        int rows = (int)(input.rows() * p);
        int cols = (int)(input.cols() * p);

        double x = rangeClip(offsetX, -1.0, 1.0) * (double)(centerCol - (cols/2));
        double y = rangeClip(offsetY, -1.0, 1.0) * (double)(centerRow - (rows/2));

        return input.submat(new Rect(
                new Point((double)(centerCol - (cols/2)) + x, (double)(centerRow - (rows/2))+ y),
                new Point((double)(centerCol - (cols/2)) + cols + x, (double)(centerRow - (rows/2)) + rows + y)
        ));
    }

    private static double rangeClip(double val, double min, double max) {
        if (val < min) return min;
        if (val > max) return max;
        return val;
    }
}
