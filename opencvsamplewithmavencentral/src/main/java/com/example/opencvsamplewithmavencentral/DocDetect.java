package com.example.opencvsamplewithmavencentral;
import android.os.Build;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.features2d.MSER;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.*;





import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;





public class DocDetect {






    //-------------------------------------------------------------------------------------------------------------------------------//

    public static Mat detectEdges(Mat im, int blurRadius, int thr1, int thr2, boolean removeText) {
        Mat enhancedIm = preprocess(im, blurRadius);
        Mat edges = new Mat();
        Imgproc.Canny(enhancedIm, edges, thr1, thr2);

        if (removeText) {
            int height = im.rows();
            int width = im.cols();
            int maxArea = (int) ((width * height) / 1e2);
            List<MatOfPoint> characters = findCharacters(im, maxArea);
            removeCharacters(characters, edges);
        }

        return edges;
    }


    private static Mat preprocess(Mat im, int blurRadius) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(im, hsv, Imgproc.COLOR_BGR2HSV);

        List<Mat> hsvChannels = new ArrayList<>();

        Core.split(hsv, hsvChannels);

        Mat saturation = hsvChannels.get(2); // Saturation channel
        if (blurRadius != 0) {
            Imgproc.medianBlur(saturation, saturation, blurRadius);
        }

        return saturation;
    }

    private static List<MatOfPoint> findCharacters(Mat image, int maxArea) {
        MSER mser = MSER.create();
        mser.setMaxArea(maxArea);

        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        List<MatOfPoint> regions = new ArrayList<>();
        MatOfRect boundingBoxes = new MatOfRect();
        mser.detectRegions(gray, regions, boundingBoxes);

        return regions;
    }

//    private static void removeCharacters(List<MatOfPoint> characters, Mat mask) {
//        for (MatOfPoint character : characters) {
//            for (Point point : character.toArray()) {
//                int x = (int) point.y; // Row index
//                int y = (int) point.x; // Column index
//                mask.put(x, y, 0); // Set pixel value to 0
//            }
//        }
//    }

    private static void removeCharacters(List<MatOfPoint> characters, Mat edges) {
        for (MatOfPoint character : characters) {
            Rect boundingBox = Imgproc.boundingRect(character);
            Imgproc.rectangle(edges, boundingBox.tl(), boundingBox.br(), new Scalar(0), -1);
        }}


    //-------------------------------------------------------------------------------------------------------------------------------//









  //-------------------------------------------------------------------------------------------------------------------------------//

    public static List<Line> detectLines(Mat image, int houghThr, int groupSimilarThr) {
        // Detect lines using Hough Line Transform
        Mat lines = new Mat();
        Imgproc.HoughLines(image, lines, 1, Math.PI / 90, houghThr);

        // Convert to list of Line objects
        List<Line> lineList = cvHoughLinesToList(lines);

        // Group similar lines
        if (groupSimilarThr > 0) {
            lineList = groupSimilar(lineList, groupSimilarThr);
        }

        return lineList;
    }

    private static List<Line> cvHoughLinesToList(Mat lines) {
        List<Line> lineList = new ArrayList<>();
        for (int i = 0; i < lines.rows(); i++) {
            double[] lineData = lines.get(i, 0);
            double rho = lineData[0];
            double theta = lineData[1];
            lineList.add(new Line(rho, theta));
        }
        return lineList;
    }

    private static List<Line> groupSimilar(List<Line> lines, int thr) {
        lines.sort((line1, line2) -> Double.compare(line1.rho, line2.rho));
        List<Line> uniqueLines = new ArrayList<>();

        for (Line toAdd : lines) {
            if (!isDuplicated(toAdd, uniqueLines, thr)) {
                uniqueLines.add(toAdd);
            }
        }

        return uniqueLines;
    }

    private static boolean isDuplicated(Line line, List<Line> lines, int thr) {
        for (Line existingLine : lines) {
            if (Math.abs(Math.abs(line.rho) - Math.abs(existingLine.rho)) < thr) {
                return true;
            }
        }
        return false;
    }

    public static class Line {
        public double rho;
        public double theta;

        public Line(double rho, double theta) {
            this.rho = rho;
            this.theta = theta;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------------//

    //-------------------------------------------------------------------------------------------------------------------------------//







}
