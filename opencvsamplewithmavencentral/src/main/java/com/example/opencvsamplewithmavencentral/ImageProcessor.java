//package com.example.opencvsamplewithmavencentral;
//
//import org.opencv.core.*;
//import org.opencv.imgproc.Imgproc;
//
//import java.util.ArrayList;
//import java.util.List;
//import org.opencv.core.Core.MinMaxLocResult;
//
//public class ImageProcessor {
//
//    // Method to find the biggest contour (assumes quadrilaterals are required)
//    public static MatOfPoint biggestContour(List<MatOfPoint> contours) {
//        MatOfPoint biggest = new MatOfPoint();
//        double maxArea = 0;
//
//        for (MatOfPoint contour : contours) {
//            if (contour.size().height < 4) continue;
//            double area = Imgproc.contourArea(contour);
//
//            if (area > 5000) {
//                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
//                double peri = Imgproc.arcLength(contour2f, true);
//                MatOfPoint2f approx2f = new MatOfPoint2f();
//                Imgproc.approxPolyDP(contour2f, approx2f, 0.02 * peri, true);
//
//                MatOfPoint approx = new MatOfPoint(approx2f.toArray());
//                if (area > maxArea && approx.total() == 4) {
//                    biggest = approx;
//                    maxArea = area;
//                }
//            }
//        }
//        return biggest;
//    }
//
//    // Method to reorder points for a rectangle
//    public static MatOfPoint reorder(MatOfPoint points) {
//        if (points.total() != 4) return points;
//
//        Mat pointsMat = new Mat();
//        points.convertTo(pointsMat, CvType.CV_32S);
//        Mat reshapedPoints = pointsMat.reshape(1, 4);
//
//        Mat myPointsNew = Mat.zeros(4, 2, CvType.CV_32S);
//        Mat rowSums = new Mat();
//        Core.reduce(reshapedPoints, rowSums, 1, Core.REDUCE_SUM, CvType.CV_32S);
//
//        MinMaxLocResult minMaxSum = Core.minMaxLoc(rowSums);
//        myPointsNew.row(0).setTo(reshapedPoints.row((int) minMaxSum.minLoc.y));
//        myPointsNew.row(3).setTo(reshapedPoints.row((int) minMaxSum.maxLoc.y));
//
//        Mat diffs = new Mat(reshapedPoints.rows(), 1, CvType.CV_32S);
//        for (int i = 0; i < reshapedPoints.rows(); i++) {
//            double[] point = reshapedPoints.get(i, 0);
//            diffs.put(i, 0, point[0] - point[1]);
//        }
//
//        MinMaxLocResult minMaxDiff = Core.minMaxLoc(diffs);
//        myPointsNew.row(1).setTo(reshapedPoints.row((int) minMaxDiff.minLoc.y));
//        myPointsNew.row(2).setTo(reshapedPoints.row((int) minMaxDiff.maxLoc.y));
//
//        MatOfPoint reorderedPoints = new MatOfPoint();
//        myPointsNew.convertTo(reorderedPoints, CvType.CV_32S);
//        return reorderedPoints;
//    }
//
//    // Method to draw a rectangle or dots on an image
//    public static Mat drawRectangle(Mat img, MatOfPoint points, int thickness) {
//        if (points.rows() != 4) return img;
//        Point[] pts = points.toArray();
//        Scalar red = new Scalar(255, 0, 0);
//
//        for (Point pt : pts) {
//            Imgproc.circle(img, pt, 35, red, -1);
//        }
//        return img;
//    }
//}


//import android.graphics.Bitmap;
//
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.MatOfPoint2f;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;//    TODO BEST TILL
//@Override
//public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//    try {
//        // Get original frame
//        Mat rgba = inputFrame.rgba();
//
//        // Preprocess: Grayscale, Gaussian blur, and edge detection
//        Mat gray = new Mat();
//        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.GaussianBlur(gray, gray, new Size(11, 11), 0);
//
//        Mat edges = new Mat();
//        Imgproc.Canny(gray, edges, 300, 300);
//
////            // Dilate edges to enhance contour detection
////            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
////            Imgproc.dilate(edges, edges, kernel);
//        // Preparing the kernel matrix object for dilation
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//
//        // Apply dilation to strengthen edges
//        Imgproc.dilate(edges, edges, kernel);
//
//        // Find contours
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // Find the largest rectangle-like contour
//        MatOfPoint largestRectangle = biggestContour(contours);
//
//        if (largestRectangle != null && !largestRectangle.empty()) {
//            // Draw the largest rectangle on the frame
//            Imgproc.drawContours(rgba, Collections.singletonList(largestRectangle), -1, new Scalar(0, 255, 0), 20);
//
//            rgba = drawRectangle(rgba, largestRectangle,10);
//        } else {
//            System.out.println("No valid contours found to draw.");
//        }
//
//        return rgba;
//    } catch (Exception e) {
//        System.err.println("Error processing camera frame: " + e.getMessage());
//        return inputFrame.rgba(); // Return the original frame in case of error
//    }
//}
////TODO BEST TILL
//
//
//
//
//
//
//
//static MatOfPoint biggestContour(List<MatOfPoint> contours) {
//    MatOfPoint biggest = new MatOfPoint();
//    double maxArea = 0;
//
//    for (MatOfPoint contour : contours) {
//        // Skip contours with less than 4 points
//        if (contour.size().height < 4) continue;
//        double area = Imgproc.contourArea(contour);
//
//        if (area > 5000) {
//            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
//            double peri = Imgproc.arcLength(contour2f, true);
//
//            MatOfPoint2f approx2f = new MatOfPoint2f();
//
//            Imgproc.approxPolyDP(contour2f, approx2f, 0.02 * peri, true);
//
//            MatOfPoint approx = new MatOfPoint(approx2f.toArray());
//
//            if (area > maxArea && approx.total() == 4) {
//                biggest = approx;
//                maxArea = area;
//            }
//        }
//    }
//
//    return biggest;
//}
//
//static MatOfPoint reorder(MatOfPoint myPoints) {
//    // Check if the input points do not have exactly 4 points
//    if (myPoints.total() != 4) {
//        System.out.println("Contour size mismatch: Expected 4 points.");
//        return myPoints;  // Return the original points if size mismatch
//    }
//
//    // Convert MatOfPoint to Mat
//    Mat pointsMat = new Mat();
//    myPoints.convertTo(pointsMat, CvType.CV_32S);
//
//    // Reshape to 4x2
//    Mat reshapedPoints = pointsMat.reshape(1, 4);
//
//    // Prepare Mat to store reordered points
//    Mat myPointsNew = Mat.zeros(4, 2, CvType.CV_32S);
//
//    // Sum along rows to find corners
//    Mat rowSums = new Mat();
//    Core.reduce(reshapedPoints, rowSums, 1, Core.REDUCE_SUM, CvType.CV_32S);
//
//    // Find minimum and maximum sum indices
//    Core.MinMaxLocResult minMaxSum = Core.minMaxLoc(rowSums);
//    int minSumIdx = (int) minMaxSum.minLoc.y; // Minimum sum index
//    int maxSumIdx = (int) minMaxSum.maxLoc.y; // Maximum sum index
//
//    // Assign points based on the sum logic
//    myPointsNew.row(0).setTo(reshapedPoints.row(minSumIdx)); // Top-left
//    myPointsNew.row(3).setTo(reshapedPoints.row(maxSumIdx)); // Bottom-right
//
//    // Compute differences (x - y for each point)
//    Mat diffs = new Mat(reshapedPoints.rows(), 1, CvType.CV_32S);
//    for (int i = 0; i < reshapedPoints.rows(); i++) {
//        double[] point = reshapedPoints.get(i, 0);
//        diffs.put(i, 0, point[0] - point[1]);
//    }
//
//    // Find minimum and maximum difference indices
//    Core.MinMaxLocResult minMaxDiff = Core.minMaxLoc(diffs);
//    int minDiffIdx = (int) minMaxDiff.minLoc.y; // Minimum difference index
//    int maxDiffIdx = (int) minMaxDiff.maxLoc.y; // Maximum difference index
//
//    // Assign points based on the difference logic
//    myPointsNew.row(1).setTo(reshapedPoints.row(minDiffIdx)); // Top-right
//    myPointsNew.row(2).setTo(reshapedPoints.row(maxDiffIdx)); // Bottom-left
//
//    // Convert back to MatOfPoint
//    MatOfPoint reorderedPoints = new MatOfPoint();
//    myPointsNew.convertTo(reorderedPoints, CvType.CV_32S);
//
//    return reorderedPoints;
//}
//
//
//public Mat drawRectangle(Mat img, MatOfPoint biggest, int thickness) {
//    // Ensure the biggest contour has the correct shape
//    if (biggest.rows() != 4 || biggest.cols() != 1) {
//        System.out.println("Invalid biggest contour. Expected 4 points.");
//        return img; // Return the original image if validation fails
//    }
//
//    // Convert MatOfPoint to a list of Points
//    Point[] points = biggest.toArray();
//
//    // Define colors
////        Scalar blue = new Scalar(255, 0, 0); // Blue for lines
//    Scalar red = new Scalar(255, 0, 0); // Red for dots
//
////        // Draw lines between the points
////        Imgproc.line(img, points[0], points[1], blue, thickness); // Line from point 0 to point 1
////        Imgproc.line(img, points[1], points[3], blue, thickness); // Line from point 1 to point 3
////        Imgproc.line(img, points[3], points[2], blue, thickness); // Line from point 3 to point 2
////        Imgproc.line(img, points[2], points[0], blue, thickness); // Line from point 2 to point 0
//
//    // Draw circular dots at each point
//    for (Point point : points) {
//        Imgproc.circle(img, point, 35, red, -1); // -1 fills the circle, radius is 10
//    }
//
//    // Return the updated image
//    return img;
//}
//
//
//
////    @Override
////    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
////        // Get the current frame in RGBA and grayscale
////        Mat rgbaFrame = inputFrame.rgba();
////        Mat grayFrame = inputFrame.gray();
////
////
////
////        // Preprocessing: Blur to reduce noise and improve edge detection
////        Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(5, 5), 0);
////
////        // Edge Detection using Canny
////        Mat edges = new Mat();
////        double threshold1 = 50;
////        double threshold2 = 150;
////        Imgproc.Canny(grayFrame, edges, threshold1, threshold2, 3, false);
////
////        // Find contours
////        List<MatOfPoint> contours = new ArrayList<>();
////        Mat hierarchy = new Mat();
////        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
////
////        // Filter contours to find quadrilaterals
////        double maxArea = 0;
////        MatOfPoint2f paperContour = null;
////
////        for (MatOfPoint contour : contours) {
////            // Approximate contour to polygon
////            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
////            double peri = Imgproc.arcLength(contour2f, true);
////            MatOfPoint2f approxCurve = new MatOfPoint2f();
////            Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * peri, true);
////
////            // Check for quadrilateral
////            if (approxCurve.total() == 4) {
////                // Check if this contour has the largest area so far
////                double area = Imgproc.contourArea(approxCurve);
////                if (area > maxArea) {
////                    maxArea = area;
////                    paperContour = approxCurve;
////                }
////            }
////        }
////
////        if (paperContour != null) {
////            // Document detected
////            consecutiveFramesWithDocument++;
////
////            // Convert contour back to MatOfPoint for drawing
////            MatOfPoint points = new MatOfPoint(paperContour.toArray());
////            List<MatOfPoint> drawPoints = new ArrayList<>();
////            drawPoints.add(points);
////
////            // Draw the detected quadrilateral
////            Imgproc.drawContours(rgbaFrame, drawPoints, -1, new Scalar(0, 255, 0, 255), 2);
////
////            // Draw corner circles
////            Point[] cornerPoints = paperContour.toArray();
////            for (int i = 0; i < cornerPoints.length; i++) {
////                Imgproc.circle(rgbaFrame, cornerPoints[i], 10, new Scalar(255, 0, 0, 255), -1);
////            }
////
////            // Check if we should auto-capture
////            if (shouldAutoCapture()) {
////
////                // Perform perspective correction
////                Mat warped = fourPointTransform(rgbaFrame, cornerPoints);
////
////                // Enhance the image
////                Mat enhanced = enhanceScannedImage(warped);
////
////                // Save or process the enhanced image
////                processCapturedImage(enhanced);
////
////                // Release Mats
////                warped.release();
////                enhanced.release();
////            }
////        } else {
////            // No document detected
////            consecutiveFramesWithDocument = 0;
////        }
////
////        // Release resources
////        grayFrame.release();
////        edges.release();
////        hierarchy.release();
////
////        // Return the frame to display on screen
////        return rgbaFrame;
////    }
//
//
//private MatOfPoint2f findLargestContour(Mat src) {
//    List<MatOfPoint> contours = new ArrayList<>();
//    Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//
//    // Get the 5 largest contours
//    Collections.sort(contours, new Comparator<MatOfPoint>() {
//        public int compare(MatOfPoint o1, MatOfPoint o2) {
//            double area1 = Imgproc.contourArea(o1);
//            double area2 = Imgproc.contourArea(o2);
//            return (int) (area2 - area1);
//        }
//    });
//    if (contours.size() > 5) contours.subList(4, contours.size() - 1).clear();
//
//    MatOfPoint2f largest = null;
//    for (MatOfPoint contour : contours) {
//        MatOfPoint2f approx = new MatOfPoint2f();
//        MatOfPoint2f c = new MatOfPoint2f();
//        contour.convertTo(c, CvType.CV_32FC2);
//        Imgproc.approxPolyDP(c, approx, Imgproc.arcLength(c, true) * 0.02, true);
//
//        if (approx.total() == 4 && Imgproc.contourArea(contour) > 150) {
//            // the contour has 4 points, it's valid
//            largest = approx;
//            break;
//        }
//    }
//
//    return largest;
//}
////@Override
////public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
////    // Get the current frame in RGBA and grayscale
////    Mat rgbaFrame = inputFrame.rgba();
////        Mat grayFrame = inputFrame.gray();
////
////
////
////
////    // Preprocessing: Blur to reduce noise and improve edge detection
////    Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(5, 5), 0);
////
////    // Edge Detection using Canny
////    Mat edges = new Mat();
////    double threshold1 = 50;
////    double threshold2 = 150;
////    Imgproc.Canny(grayFrame, edges, threshold1, threshold2, 3, false);
////
////    // Find contours
////    List<MatOfPoint> contours = new ArrayList<>();
////    Mat hierarchy = new Mat();
////    Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
////
////    // Filter contours to find quadrilaterals
////    double maxArea = 0;
////    MatOfPoint2f paperContour = null;
////
////    for (MatOfPoint contour : contours) {
////        // Approximate contour to polygon
////        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
////        double peri = Imgproc.arcLength(contour2f, true);
////        MatOfPoint2f approxCurve = new MatOfPoint2f();
////        Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * peri, true);
////
////        // Check for quadrilateral
////        if (approxCurve.total() == 4) {
////            // Check if this contour has the largest area so far
////            double area = Imgproc.contourArea(approxCurve);
////            if (area > maxArea) {
////                maxArea = area;
////                paperContour = approxCurve;
////            }
////        }
////    }
////
////    if (paperContour != null) {
////        // Document detected
////        consecutiveFramesWithDocument++;
////
////        // Convert contour back to MatOfPoint for drawing
////        MatOfPoint points = new MatOfPoint(paperContour.toArray());
////        List<MatOfPoint> drawPoints = new ArrayList<>();
////        drawPoints.add(points);
////
////        // Draw the detected quadrilateral
////        Imgproc.drawContours(rgbaFrame, drawPoints, -1, new Scalar(0, 255, 0, 255), 2);
////
////        // Draw corner circles
////        Point[] cornerPoints = paperContour.toArray();
////        for (int i = 0; i < cornerPoints.length; i++) {
////            Imgproc.circle(rgbaFrame, cornerPoints[i], 10, new Scalar(255, 0, 0, 255), -1);
////        }
////
////        // Check if we should auto-capture
////        if (shouldAutoCapture()) {
////
////            // Perform perspective correction
////            Mat warped = fourPointTransform(rgbaFrame, cornerPoints);
////
////            // Enhance the image
////            Mat enhanced = enhanceScannedImage(warped);
////
////            // Save or process the enhanced image
////            processCapturedImage(enhanced);
////
////            // Release Mats
////            warped.release();
////            enhanced.release();
////        }
////    } else {
////        // No document detected
////        consecutiveFramesWithDocument = 0;
////    }
////
////    // Release resources
////    grayFrame.release();
////    edges.release();
////    hierarchy.release();
////
////    // Return the frame to display on screen
////    return rgbaFrame;
////}
//
//
//
//
//
//private Mat fourPointTransform(Mat src, Point[] pts) {
//    // Order points: top-left, top-right, bottom-right, bottom-left
//    Point[] orderedPts = sortPoints(pts);
//
//    // Compute the width and height of the new image
//    double widthA = distance(orderedPts[2], orderedPts[3]);
//    double widthB = distance(orderedPts[1], orderedPts[0]);
//    double maxWidth = Math.max(widthA, widthB);
//
//    double heightA = distance(orderedPts[1], orderedPts[2]);
//    double heightB = distance(orderedPts[0], orderedPts[3]);
//    double maxHeight = Math.max(heightA, heightB);
//
//    // Destination points
//    MatOfPoint2f dst = new MatOfPoint2f(
//            new Point(0, 0),
//            new Point(maxWidth - 1, 0),
//            new Point(maxWidth - 1, maxHeight - 1),
//            new Point(0, maxHeight - 1)
//    );
//
//    // Source points
//    MatOfPoint2f srcPts = new MatOfPoint2f(orderedPts);
//
//    // Get perspective transform matrix
//    Mat M = Imgproc.getPerspectiveTransform(srcPts, dst);
//
//    // Warp the image
//    Mat warped = new Mat();
//    Imgproc.warpPerspective(src, warped, M, new Size(maxWidth, maxHeight));
//
//    return warped;
//}
//
//private Point[] sortPoints(Point[] pts) {
//    Point[] ordered = new Point[4];
//
//    // Calculate sums and differences
//    double[] sums = new double[4];
//    double[] diffs = new double[4];
//    for (int i = 0; i < 4; i++) {
//        sums[i] = pts[i].x + pts[i].y;
//        diffs[i] = pts[i].y - pts[i].x;
//    }
//
//    // Top-left has the smallest sum
//    ordered[0] = pts[indexOfMin(sums)];
//    // Bottom-right has the largest sum
//    ordered[2] = pts[indexOfMax(sums)];
//    // Top-right has the smallest difference
//    ordered[1] = pts[indexOfMin(diffs)];
//    // Bottom-left has the largest difference
//    ordered[3] = pts[indexOfMax(diffs)];
//
//    return ordered;
//}
//private int indexOfMin(double[] arr) {
//    int index = 0;
//    double min = arr[0];
//    for (int i = 1; i < arr.length; i++) {
//        if (arr[i] < min) {
//            min = arr[i];
//            index = i;
//        }
//    }
//    return index;
//}
//private int indexOfMax(double[] arr) {
//    int index = 0;
//    double max = arr[0];
//    for (int i = 1; i < arr.length; i++) {
//        if (arr[i] > max) {
//            max = arr[i];
//            index = i;
//        }
//    }
//    return index;
//}
//private double distance(Point p1, Point p2) {
//    return Math.hypot(p1.x - p2.x, p1.y - p2.y);
//}
//
//private Mat enhanceScannedImage(Mat src) {
//    Mat gray = new Mat();
//    Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
//
//    // Apply adaptive thresholding to get binary image
//    Mat binary = new Mat();
//    Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//            Imgproc.THRESH_BINARY, 15, 15);
//
//    // Optionally perform other enhancements like contrast adjustment or noise reduction
//
//    // Release resources
//    gray.release();
//
//    return binary;
//}
//
//private void processCapturedImage(Mat image) {
//    // Convert Mat to Bitmap
//    Bitmap bitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
//    Utils.matToBitmap(image, bitmap);
//
//    // Save the image to storage
//    saveImageToGallery(bitmap);
//
//    // Reset capturing flag
//    isCapturing = false;
//}
//private void saveImageToGallery(Bitmap bitmap) {
//
////        Toast.makeText(this, "Welcome to the app!", Toast.LENGTH_SHORT).show();
//    System.out.println();
////        String savedImagePath = null;
////
////        String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
////        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
////                Environment.DIRECTORY_PICTURES) + "/YourAppName");
////        boolean success = true;
////        if (!storageDir.exists()) {
////            success = storageDir.mkdirs();
////        }
////        if (success) {
////            File imageFile = new File(storageDir, imageFileName);
////            savedImagePath = imageFile.getAbsolutePath();
////            try {
////                OutputStream fOut = new FileOutputStream(imageFile);
////                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
////                fOut.close();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////            // Add the image to the system gallery
////            galleryAddPic(savedImagePath);
////
////            // Optionally, notify the user
////            runOnUiThread(() -> Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show());
//}
////    private void galleryAddPic(String imagePath) {
//////        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//////        File f = new File(imagePath);
//////        Uri contentUri = Uri.fromFile(f);
//////        mediaScanIntent.setData(contentUri);
//////        this.sendBroadcast(mediaScanIntent);
////    }