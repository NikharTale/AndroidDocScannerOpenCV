package com.example.opencvsamplewithmavencentral;

import static org.opencv.imgproc.Imgproc.contourArea;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.features2d.MSER;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import android.widget.Button;
import android.widget.Toast;

import android.content.Intent; // For Intent to navigate between activities
import android.os.Bundle; // For activity lifecycle methods like onCreate
import android.view.View; // For handling views and click events
import android.widget.ImageView; // For the ImageView component
//import androidx.appcompat.app.AppCompatActivity; // If extending AppCompatActivity
import com.example.opencvsamplewithmavencentral.DocumentScanner;



public class MainActivity extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    // Declare global variables for stability tracking

    private int consecutiveFramesWithDocument = 0;
    private final int FRAMES_THRESHOLD = 25; // Number of frames to confirm stability
    private boolean isCapturing = false; // Prevent multiple captures during one stable period

    private List<String> capturedImages = new ArrayList<>(); // Declare the captured images list
    private boolean isToastVisible = false; // To prevent overlapping toasts
    private Toast currentToast;
    private boolean docIsInFrame = false;



    DocumentScanner scanner = new DocumentScanner();
    DocDetect docDetect = new DocDetect();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "called onCreate");

        if (!OpenCVLoader.initDebug()) {
            showToast("OpenCV initialization failed!");

            Log.e(TAG, "OpenCV initialization failed!");
            return;
        }

        Log.i(TAG, "OpenCV loaded successfully");

        // Set up the layout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Camera view setup
        mOpenCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Capture button setup
        Button captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(v -> {
            isCapturing = true;

            showToast("Capture button clicked!");
        });


        ImageView previewArea = findViewById(R.id.preview_area);
        previewArea.setOnClickListener(v -> {
            pushNewScreen();
        });

    }

    void pushNewScreen(){
        Intent intent = new Intent(MainActivity.this, CapturedImagesActivity.class);
        intent.putStringArrayListExtra("captured_images", new ArrayList<>(capturedImages));
        startActivity(intent);
    }



    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOpenCvCameraView != null) mOpenCvCameraView.enableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // You can initialize any matrices or variables here
    }

    @Override
    public void onCameraViewStopped() {
        // Release allocated resources
    }

//    @Override  public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat rgba = inputFrame.rgba();
//      Mat output=  scanner.detectDocument(rgba);
//
//return  output;
//
//
//    }


//    private ArrayList<MatOfPoint> findContours(Mat src) {
//
//        Mat grayImage = null;
//        Mat cannedImage = null;
//        Mat resizedImage = null;
//
//        double ratio = src.size().height / 500;
//        int height = Double.valueOf(src.size().height / ratio).intValue();
//        int width = Double.valueOf(src.size().width / ratio).intValue();
//        Size size = new Size(width,height);
//
//        resizedImage = new Mat(size, CvType.CV_8UC4);
//        grayImage = new Mat(size, CvType.CV_8UC4);
//        cannedImage = new Mat(size, CvType.CV_8UC1);
//
//        Imgproc.resize(src,resizedImage,size);
//        Imgproc.cvtColor(resizedImage, grayImage, Imgproc.COLOR_RGBA2GRAY, 4);
//        Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5), 0);
//        Imgproc.Canny(grayImage, cannedImage, 75, 200);
//
//        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarchy = new Mat();
//
//        Imgproc.findContours(cannedImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        hierarchy.release();
//
//        Collections.sort(contours, (lhs, rhs) -> Double.valueOf(Imgproc.contourArea(rhs)).compareTo(Imgproc.contourArea(lhs)));
//
//        resizedImage.release();
//        grayImage.release();
//        cannedImage.release();
//
//        return contours;
//    }




//    ///new one 04/12/2024 video version
//        @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        try {
//            // Get the original frame (RGBA)
//            Mat rgba = inputFrame.rgba();
//
//            // Create a copy of the original image
//            Mat rgbaCopy = rgba.clone();
//
//            // Preprocess: Grayscale, Gaussian blur, and edge detection
//            Mat gray = new Mat();
//            Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);

//            Imgproc.GaussianBlur(gray, gray, new Size(11, 11), 0);
//
//            Mat edges = new Mat();
//            Imgproc.Canny(gray, edges, 50, 50);
//
//
//
//            // Strengthen edges using dilation
//            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//            Imgproc.dilate(edges, edges, kernel);
////            return  edges;
////
//            // Find contours and the largest rectangle-like contour
//            List<MatOfPoint> contours = new ArrayList<>();
//            Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//            MatOfPoint largestRectangle = findLargestRectangle(contours);
//
//            // Draw the largest rectangle
//            if (largestRectangle != null) {
//                Imgproc.drawContours(rgba, Collections.singletonList(largestRectangle), -1, new Scalar(0, 255, 0), 20);
//                drawRectangle(rgba, largestRectangle, 10);
//
//                // Crop the image based on the rectangle
//                Mat croppedImage = cropImage(rgbaCopy, largestRectangle);
//
//                if (isCapturing) {
//                    // Save the cropped image
//                    saveFrame(croppedImage);
//                    isCapturing = false;
//                }
//
//            }
//
//
//
//            return rgba;
//        } catch (Exception e) {
//            Log.e(TAG, "Error processing camera frame: " + e.getMessage());
//            return inputFrame.rgba();
//        }
//    }









//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
//        try {
//            // Resize the input frame (RGBA) before proceeding
//            double ratio = inputFrame.rgba().size().height / 500.0;
//            int height = (int) (inputFrame.rgba().size().height / ratio);
//            int width = (int) (inputFrame.rgba().size().width / ratio);
//            Size size = new Size(width, height);
//
//            // Create a resized image
//            Mat resizedImage = new Mat(size, CvType.CV_8UC4);
//            Imgproc.resize(inputFrame.rgba(), resizedImage, size);
//
//            // Preprocess: Grayscale, Gaussian blur, and edge detection
//            Mat gray = new Mat();
//            Imgproc.cvtColor(resizedImage, gray, Imgproc.COLOR_BGR2GRAY);
//            Imgproc.GaussianBlur(gray, gray, new Size(11, 11), 0);
//
//            Mat edges = new Mat();
//            Imgproc.Canny(gray, edges, 50, 50);
//
//            // Strengthen edges using dilation
//            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//            Imgproc.dilate(edges, edges, kernel);
//
//            // Find contours and the largest rectangle-like contour
//            List<MatOfPoint> contours = new ArrayList<>();
//            Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//
//
//                        MatOfPoint largestRectangle = findLargestRectangle(contours);
//
//            // Draw the largest rectangle
//            if (largestRectangle != null) {
//                Imgproc.drawContours(resizedImage, Collections.singletonList(largestRectangle), -1, new Scalar(0, 255, 0), 10);
//                drawRectangle(resizedImage, largestRectangle, 10);
//
//                // Crop the image based on the rectangle
//                Mat croppedImage = cropImage(resizedImage, largestRectangle);
//
//                if (isCapturing) {
//                    // Save the cropped image
//                    saveFrame(croppedImage);
//                    isCapturing = false;
//                }
//            }
//
//
//
//
////            // Initialize DocumentScanner and get the quadrilateral
////            DocumentScanner scanner = new DocumentScanner();
////            DocumentScanner.Quadrilateral quadrilateral = scanner.getQuadrilateral(contours, resizedImage.size());
//
//
//
//
////            System.out.println("Quadrilateral: " + quadrilateral);
////
////            // Draw the quadrilateral if found
////            if (quadrilateral != null) {
////                Point[] points = quadrilateral.points;
////                for (int i = 0; i < 4; i++) {
////                    Imgproc.line(resizedImage, points[i], points[(i + 1) % 4], new Scalar(0, 255, 0), 5);
////                }
////            }
//
//            // Resize edges image to match rgba image size
//            Mat edgesResized = new Mat();
//            Imgproc.resize(edges, edgesResized, resizedImage.size());
//
//            // Convert the edges image to a 3-channel (RGBA) image for concatenation
//            Mat edgesColor = new Mat();
//            Imgproc.cvtColor(edgesResized, edgesColor, Imgproc.COLOR_GRAY2RGBA);
//
//            // Concatenate the RGBA image and the edges image side by side
//            List<Mat> images = new ArrayList<>();
//            images.add(resizedImage);
//            images.add(edgesColor);
//            Mat combined = new Mat();
//            Core.hconcat(images, combined);
//
//            // Resize the concatenated image to match the expected Bitmap size (optional)
//            Mat resizedCombined = new Mat();
//            Size bitmapSize = new Size(720, 960); // Example size for Bitmap
//            Imgproc.resize(combined, resizedCombined, bitmapSize);
//
//            // Convert the Mat to RGB (if required)
//            Mat rgbCombined = new Mat();
//            Imgproc.cvtColor(resizedCombined, rgbCombined, Imgproc.COLOR_RGBA2RGB);
//
//            // Convert the Mat to Bitmap
//            Bitmap bitmap = Bitmap.createBitmap(rgbCombined.cols(), rgbCombined.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(rgbCombined, bitmap);
//
//            // Return the resized and converted Bitmap (if you need to return Bitmap)
//            // return bitmap;  // Uncomment this line if you want to return a Bitmap instead of a Mat
//
//            // Return the resized combined Mat if you want to continue using OpenCV Mat
//            return resizedCombined;
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error processing camera frame: " + e.getMessage());
//            return inputFrame.rgba();
//        }
//
//
//    }


//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        try {
//
//            // Get the original frame (RGBA)
//            Mat rgba = inputFrame.rgba();
//            Mat src = rgba;
//
//            double ratio = src.size().height / 500;
//            int height = Double.valueOf(src.size().height / ratio).intValue();
//            int width = Double.valueOf(src.size().width / ratio).intValue();
//            Size size = new Size(width,height);
//            Mat resizedImage = null;
//            resizedImage = new Mat(size, CvType.CV_8UC4);
//
//
//
//
//
//            // Create a copy of the original image
//            Mat rgbaCopy = rgba.clone();
//
//            // Preprocess: Grayscale, Gaussian blur, and edge detection
//
//            Mat gray = new Mat(size, CvType.CV_8UC4);
//            Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_BGR2GRAY);
//
//
//            Imgproc.GaussianBlur(gray, gray, new Size(11, 11), 0);
//
//            Mat edges = new Mat(size, CvType.CV_8UC4);
//            Imgproc.Canny(gray, edges,  50, 50  );
//
//            // Strengthen edges using dilation
//            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//            Imgproc.dilate(edges, edges, kernel);
//
//            // Find contours and the largest rectangle-like contour
//            List<MatOfPoint> contours = new ArrayList<>();
//            Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//
////            MatOfPoint largestRectangle = findLargestRectangle(contours);
////
////            // Draw the largest rectangle
////            if (largestRectangle != null) {
////                Imgproc.drawContours(rgba, Collections.singletonList(largestRectangle), -1, new Scalar(0, 255, 0), 20);
////                drawRectangle(rgba, largestRectangle, 10);
////
////                // Crop the image based on the rectangle
////                Mat croppedImage = cropImage(rgbaCopy, largestRectangle);
////
////                if (isCapturing) {
////                    // Save the cropped image
////                    saveFrame(croppedImage);
////                    isCapturing = false;
////                }
////            }
//
//
//            // Initialize DocumentScanner and get the quadrilateral
//            DocumentScanner scanner = new DocumentScanner();
//            DocumentScanner.Quadrilateral quadrilateral = scanner.getQuadrilateral(contours, rgba.size());
//
//
//
//
//            System.out.println("quadrilateralquadrilateralquadrilateralquadrilateralquadrilateralquadrilateral");
//            System.out.println(quadrilateral);
//
//            System.out.println("quadrilateralquadrilateralquadrilateralquadrilateralquadrilateralquadrilateral");
//
//
//            // Draw the quadrilateral if found
//            if (quadrilateral != null) {
//                // Draw the quadrilateral points (you can adjust the line thickness here)
//                Point[] points = quadrilateral.points;
//                for (int i = 0; i < 4; i++) {
//                    Imgproc.line(rgba, points[i], points[(i + 1) % 4], new Scalar(0, 255, 0), 5);
//                }}
//
//
//
//            // Resize edges image to match rgba image size
//            Mat edgesResized = new Mat();
//            Imgproc.resize(edges, edgesResized, rgba.size());
//
//            // Convert the edges image to a 3-channel (RGBA) image for concatenation
//            Mat edgesColor = new Mat();
//            Imgproc.cvtColor(edgesResized, edgesColor, Imgproc.COLOR_GRAY2RGBA);
//
//            // Concatenate the RGBA image and the edges image side by side
//            List<Mat> images = new ArrayList<>();
//            images.add(rgba);
//            images.add(edgesColor);
//            Mat combined = new Mat();
//            Core.hconcat(images, combined);
//
//            // Resize the concatenated image to match the expected Bitmap size (optional)
//            Mat resizedCombined = new Mat();
//            Size bitmapSize = new Size(720, 960); // Example size for Bitmap
//            Imgproc.resize(combined, resizedCombined, bitmapSize);
//
//            // Convert the Mat to RGB (if required)
//            Mat rgbCombined = new Mat();
//            Imgproc.cvtColor(resizedCombined, rgbCombined, Imgproc.COLOR_RGBA2RGB);
//
//            // Convert the Mat to Bitmap
//            Bitmap bitmap = Bitmap.createBitmap(rgbCombined.cols(), rgbCombined.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(rgbCombined, bitmap);
//
//            // Return the resized and converted Bitmap (if you need to return Bitmap)
//            // return bitmap;  // Uncomment this line if you want to return a Bitmap instead of a Mat
//
//            // Return the resized combined Mat if you want to continue using OpenCV Mat
//            return resizedCombined;
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error processing camera frame: " + e.getMessage());
//            return inputFrame.rgba();
//        }
//    }


//    private void saveFrame(Mat frame) {
//        try {
//            String filename = getExternalFilesDir(null) + "/captured_frame_" + System.currentTimeMillis() + ".png";
//            Mat bgrFrame = new Mat();
//            Imgproc.cvtColor(frame, bgrFrame, Imgproc.COLOR_RGBA2BGR);
//            boolean success = Imgcodecs.imwrite(filename, bgrFrame);
//
//            if (success) {
//                Log.i(TAG, "Frame saved successfully at: " + filename);
//                runOnUiThread(() -> {
//                    Toast.makeText(this, "Frame saved to " + filename, Toast.LENGTH_LONG).show();
//                    // Add the saved image path to the list
//                        capturedImages.add(filename);
//                });
//            } else {
//                Log.e(TAG, "Failed to save frame.");
//                runOnUiThread(() -> Toast.makeText(this, "Failed to save frame", Toast.LENGTH_SHORT).show());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error saving frame: " + e.getMessage());
//        }
//    }


//    // Find the largest rectangle-like contour
//    private MatOfPoint findLargestRectangle(List<MatOfPoint> contours) {
//        MatOfPoint largest = null;
//        double maxArea = 0;
//
//        for (MatOfPoint contour : contours) {
//            if (contour.size().height < 4) continue;
//
//            double area = Imgproc.contourArea(contour);
//            if (area > 5000) {
//                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
//                double peri = Imgproc.arcLength(contour2f, true);
//                MatOfPoint2f approx2f = new MatOfPoint2f();
//                Imgproc.approxPolyDP(contour2f, approx2f, 0.02 * peri, true);
//
//                MatOfPoint approx = new MatOfPoint(approx2f.toArray());
//                if (area > maxArea && approx.total() == 4) {
//                    largest = approx;
//                    maxArea = area;
//                }
//            }
//        }
//        return largest;
//    }


//    // Find the largest rectangle-like contour
//    private MatOfPoint findLargestRectangle(List<MatOfPoint> contours, Size imageSize) {
//        MatOfPoint largest = null;
//        double maxArea = 0;
//
//        // Iterate over the contours
//        for (MatOfPoint contour : contours) {
//            // Skip contours with less than 4 points
//            if (contour.size().height < 4) continue;
//
//            double area = Imgproc.contourArea(contour);
//
//            // Consider contours with significant area
//            if (area > 5000) {
//                // Convert contour to MatOfPoint2f for processing
//                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
//                double peri = Imgproc.arcLength(contour2f, true);
//                MatOfPoint2f approx2f = new MatOfPoint2f();
//
//                // Approximate the contour to a polygon
//                Imgproc.approxPolyDP(contour2f, approx2f, 0.02 * peri, true);
//                MatOfPoint approx = new MatOfPoint(approx2f.toArray());
//
//                // Check if the contour has 4 points (a quadrilateral)
//                if (approx.total() == 4) {
//                    // Check if this rectangle is the largest and inside the hot area
//                    if (area > maxArea && insideHotArea(approx.toArray(), imageSize)) {
//                        largest = approx;
//                        maxArea = area;
//                    }
//                }
//            }
//        }
//
//        // Return the largest rectangle or null if not found
//        return largest;
//    }

//    // Helper method to check if a quadrilateral is inside the "hot area"
//    private boolean insideHotArea(Point[] rp, Size size) {
//        // Define the hot area (adjust the margin as needed)
//        int[] hotArea = getHotArea((int) size.width, (int) size.height);
//
//        // Check if the points are inside the hot area
//        return (rp[0].x >= hotArea[0] && rp[0].y >= hotArea[1]  // Top-left corner
//                && rp[1].x <= hotArea[2] && rp[1].y >= hotArea[1]  // Top-right corner
//                && rp[2].x <= hotArea[2] && rp[2].y <= hotArea[3]  // Bottom-right corner
//                && rp[3].x >= hotArea[0] && rp[3].y <= hotArea[3]); // Bottom-left corner
//    }

//    // Define the hot area based on image size and margins (for camera preview)
//    private int[] getHotArea(int width, int height) {
//        int marginX = (int) (width *  0.1f);  // Horizontal margin (percentage of width)
//        int marginY = (int) (height *  0.1f); // Vertical margin (percentage of height)
//
//        // Return the hot area boundaries
//        return new int[]{
//                marginX, marginY, width - marginX, height - marginY
//        };
//    }








    private Point[] sortPoints(Point[] points) {
        List<Point> pointList = Arrays.asList(points);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pointList.sort(Comparator.comparingDouble(p -> p.x + p.y)); // Sort by sum of x and y
        }
        Point topLeft = pointList.get(0);
        Point bottomRight = pointList.get(3);
        Point topRight = pointList.get(1).x > pointList.get(2).x ? pointList.get(1) : pointList.get(2);
        Point bottomLeft = pointList.get(1).x > pointList.get(2).x ? pointList.get(2) : pointList.get(1);

        return new Point[]{topLeft, topRight, bottomRight, bottomLeft};
    }

    // Crop the image using the largest rectangle
    private Mat cropImage(Mat image, MatOfPoint rectangle) {
        // Convert the rectangle points to a MatOfPoint2f for the perspective transform
        Point[] sortedPoints = sortPoints(rectangle.toArray());
        MatOfPoint2f points2f = new MatOfPoint2f(sortedPoints);

        // Get the bounding rectangle of the detected points
        Rect boundingRect = Imgproc.boundingRect(rectangle);

        // Define destination points based on the bounding rectangle's size
        Point[] destPoints = new Point[]{
                new Point(0, 0), // Top-left
                new Point(boundingRect.width - 1, 0), // Top-right
                new Point(boundingRect.width - 1, boundingRect.height - 1), // Bottom-right
                new Point(0, boundingRect.height - 1) // Bottom-left
        };
        MatOfPoint2f dst = new MatOfPoint2f(destPoints);

        // Get the perspective transformation matrix
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(points2f, dst);

        // Apply the perspective warp
        Mat cropped = new Mat();
        Imgproc.warpPerspective(image, cropped, perspectiveTransform, new Size(boundingRect.width, boundingRect.height));

        return cropped; // Return the correctly cropped image
    }

//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//
//            Mat rgba = inputFrame.rgba();
////         Create a copy of the original image
//            Mat rgbaCopy = rgba.clone();
//
//
//        try {// Initialize DocumentScanner and get the quadrilateral
//
//
////            List<MatOfPoint> contours =  scanner.findContours(rgba);
//            Map<String, Object> result = scanner.findContours(rgba);
//            List<MatOfPoint> contours = (List<MatOfPoint>) result.get("contours");
////            rgba = (Mat) result.get("edges");
//
//
//
////
////            // Detect the largest rectangle
////
//            MatOfPoint largestRectangle = scanner.findLargestRectangle(contours,rgba.size());
//            //MatOfPoint largestRectangle = scanner.findLargestA4LikeRectangle(contours,rgba.size());
//
//
//
//
//            if (largestRectangle != null) {
//              Boolean value = isDocumentStable(largestRectangle);
//                Scalar color = new Scalar(0, 255, 0);
//                if(value){
//                    color = new Scalar(255, 0, 0);
//                }
//
//                scanner.drawDocument(rgba,largestRectangle,color );
//
//                // Calculate the bounding rectangle
//                Rect boundingRect = Imgproc.boundingRect(largestRectangle);
//                scanner.drawRectangle(rgba, largestRectangle, 10 ,color);
//                System.out.println("====================>>> isTooFar============>>"+ isTooFar(boundingRect,rgba));
//
//                // Check if document is stable
//                // Check if the document is too far (small area) or well-aligned
//                if (isTooFar(boundingRect,rgba)) {
//                    showToast("Move closer to the document!");
//
//                } else if (value) {
////                     Trigger capture if not already capturing
//                    if (!isCapturing) {
//                        isCapturing = true; // Lock capturing
//                        try {
//                            Thread.sleep(1000); // Delay for 1 second
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        showToast("Document stable! Capturing...");
//                        captureDocument(rgbaCopy, largestRectangle); // Save the frame
//                    }
//                } else if(!docIsInFrame) {
//
//                        docIsInFrame =true;
//                        showToast("Hold still! Align the document properly.");
//
//
//
//                    isCapturing = false; // Reset capturing when unstable
//                }
//            } else {
//                // Reset stability counter if no rectangle is found
//                consecutiveFramesWithDocument = 0;
//                isCapturing = false;
//                docIsInFrame =true;
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error in onCameraFrame: " + e.getMessage());
//        }
//
//        return rgba;
//    }

    /**
     * Checks if the detected document is stable for a given number of frames.
     */
    public boolean isDocumentStable(MatOfPoint rectangle) {
        // Get the bounding rectangle
        Rect boundingRect = Imgproc.boundingRect(rectangle);
        double aspectRatio = (double) boundingRect.width / boundingRect.height;

        // Ensure aspect ratio is within a reasonable range for a document
        if (aspectRatio > 0.7 && aspectRatio < 0.8) {
            consecutiveFramesWithDocument++;
        } else {
            consecutiveFramesWithDocument = 0;
        }
        System.out.println("================printing the " + consecutiveFramesWithDocument +"==================================");

        // Return true if stable for enough frames
        return consecutiveFramesWithDocument >= FRAMES_THRESHOLD;
    }



    private void showToast(String message ) {


            runOnUiThread(() -> {
                // Cancel the current toast if it exists
                if (currentToast != null ) {
                    currentToast.cancel();
                }
                // Create and show the new toast
                currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                currentToast.show();

            });




    }

    /**
     * Determines if the detected document is too far by checking its bounding area.
     */
    private boolean isTooFar(Rect boundingRect,Mat rgba) {
        double documentArea = boundingRect.width * boundingRect.height;

        // Adjust the threshold based on camera resolution
        double minAreaThreshold = 0.3 * rgba.size().area(); // Example: 30% of the frame area
        return documentArea < minAreaThreshold;
    }

    /**
     * Captures the detected document and saves it.
     */
    private void captureDocument(Mat rgba, MatOfPoint rectangle) {
        Mat cropped = cropImage(rgba, rectangle);

        // Save the captured document
//        saveFrame(enhanceImage(cropped));
//        saveFrame(cropped);
//        saveFrame(rgba);
        saveFrame(enhanceDocument(cropped));

        // Reset stability counter
        consecutiveFramesWithDocument = 0;

        // Provide visual feedback

        showToast("Document captured");
//        pushNewScreen();

    }




    /**
     * Optimized method to save the frame.
     */
    private void saveFrame(Mat frame) {
        String filename = getExternalFilesDir(null) + "/captured_frame_" + System.currentTimeMillis() + ".png";
        Mat bgrFrame = new Mat();
        Imgproc.cvtColor(frame, bgrFrame, Imgproc.COLOR_RGBA2BGR);
        boolean success = Imgcodecs.imwrite(filename, bgrFrame);

        if (success) {
            Log.i(TAG, "Frame saved at: " + filename);

             showToast("Captured");
//            capturedImages.add(filename); // Add to batch

            // Navigate to the fullscreen activity
            Intent intent = new Intent(this, ImageFullscreenActivity.class);
            intent.putExtra("image_path", filename);
            startActivity(intent);

        } else {
            Log.e(TAG, "Failed to save frame.");
        }


}



    public static Mat enhanceDocument(Mat img) {
        Mat resultImage = new Mat();

        // Step 1: Convert to Grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // Step 2: Apply Gaussian Blur
        Mat smooth = new Mat();
        Imgproc.GaussianBlur(gray, smooth, new Size(95, 95), 0);

        // Step 3: Divide Gray Image by the Blurred Image
        Mat division = new Mat();
        Core.divide(gray, smooth, division, 255);

        // Step 4: Apply Unsharp Masking (Sharpening)
        Mat sharpened = new Mat();
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        kernel.put(0, 0, 0, -1, 0);
        kernel.put(1, 0, -1, 5, -1);
        kernel.put(2, 0, 0, -1, 0);
        Imgproc.filter2D(division, sharpened, -1, kernel);

        // Step 5: Normalize result to 8-bit range (0 to 255)
        sharpened.convertTo(resultImage, CvType.CV_8UC1);

        return resultImage;

    }


//    Rect bounding_rect;
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat mRgba =new Mat();
//        Mat mGray =new Mat();
//        Mat mRgbaT =new Mat();
//
//        mRgba = inputFrame.rgba();
//        mGray = inputFrame.gray();
//        mRgbaT = mRgba.t();
//        Core.flip(mRgba.t(), mRgbaT, 1);
//        Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());
//
//        Imgproc.cvtColor( mRgba, mGray, Imgproc.COLOR_BGR2GRAY );
//        Imgproc.threshold( mGray, mGray, 155, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU );
//
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours( mGray, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE );
//
//        int index= 0 ;
//        double maxim = 0;
//        for (int contourIdx = 0; contourIdx<contours.size();contourIdx++){
//            double temp;
//            temp = contourArea( contours.get(contourIdx) );
//            if (maxim<temp){
//                maxim=temp;
//                index=contourIdx;
//            }
//        }
//
//        bounding_rect = Imgproc.boundingRect( contours.get( index ) );
//        Imgproc.rectangle( mRgbaT, new Point( bounding_rect.x, bounding_rect.y ), new Point( bounding_rect.x + bounding_rect.height, bounding_rect.y + bounding_rect.width ), new Scalar( 250, 250, 250 ), 5 );
//
//        return mRgbaT;
//    }




//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat mRgba = inputFrame.rgba();
//        Mat mGray = inputFrame.gray();
//        Mat mRgbaT = mRgba.clone(); // Use the original frame directly
//
//        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(mGray, mGray, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
//
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(mGray, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        if (!contours.isEmpty()) {
//            int index = 0;
//            double maxim = 0;
//            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//                double temp = Imgproc.contourArea(contours.get(contourIdx));
//                Rect rect = Imgproc.boundingRect(contours.get(contourIdx));
//                double aspectRatio = (double) rect.width / rect.height;
//
//                if (temp > maxim && aspectRatio > 0.5 && aspectRatio < 2.0) {
//                    maxim = temp;
//                    index = contourIdx;
//                }
//            }
//
//            Rect bounding_rect = Imgproc.boundingRect(contours.get(index));
//            Scalar rectColor = Core.mean(mRgba).val[0] > 127 ? new Scalar(255, 0, 0) : new Scalar(250, 250, 250);
//            Imgproc.rectangle(mRgbaT, new Point(bounding_rect.x, bounding_rect.y),
//                    new Point(bounding_rect.x + bounding_rect.width, bounding_rect.y + bounding_rect.height),
//                    rectColor, 5);
//        }
//
//
//        // Release resources
////        mGray.release();
//        hierarchy.release();
//
//        return mRgbaT;// mGray;
//    }


//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        // Step 1: Get the grayscale input frame
//        Mat rgba = inputFrame.rgba();
//
//        Mat grayFrame = new Mat();
//            Imgproc.cvtColor(rgba, grayFrame, Imgproc.COLOR_BGR2GRAY);
//
//        // Step 2: Apply GaussianBlur to reduce noise
//        Imgproc.GaussianBlur(grayFrame, grayFrame, new Size(3, 3), 0);
//
//        // Step 3: Apply Canny Edge Detection
//        Mat edges = new Mat();
//        double threshold1 = 50; // Lower threshold for edge detection
//        double threshold2 = 150; // Upper thresholdfor edge detection
//        Imgproc.Canny(grayFrame, edges, threshold1, threshold2);
//        // Perform automatic Canny edge detection
////         edges = autoCanny(grayFrame, 0.33);
//
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//        Imgproc.dilate(edges, edges, kernel);
//
//        // Step 4: Perform Hough Transform to detect lines
//        Mat lines = new Mat();
//        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 100, 50, 10);
//
//        // Step 5: Draw detected lines on a new Mat
//        Mat outputFrame = inputFrame.rgba();
//        for (int i = 0; i < lines.rows(); i++) {
//            double[] line = lines.get(i, 0);
//            if (line != null && line.length >= 4) {
//                Point pt1 = new Point(line[0], line[1]);
//                Point pt2 = new Point(line[2], line[3]);
//                Imgproc.line(outputFrame, pt1, pt2, new Scalar(0, 255, 0), 2); // Green lines
//            }
//        }
//
//        // Optional: Intersect lines to detect corners and score quadrilaterals (extend as needed)
//
//        // Return the frame with detected edges and lines
//        return edges;
//    }

    public static Mat autoCanny(Mat image, double sigma) {
        // Compute the median of the pixel intensities
        Mat sorted = new Mat();
        Core.sort(image.reshape(1, 1), sorted, Core.SORT_ASCENDING);
        double median = sorted.get(0, sorted.cols() / 2)[0];

        // Calculate lower and upper thresholds for Canny
        int lower = (int) Math.max(0, (1.0 - sigma) * median);
        int upper = (int) Math.min(255, (1.0 + sigma) * median);

        // Apply Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(image, edges, lower, upper);

        return edges;
    }


//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        // Get the current frame in grayscale
//        Mat grayFrame = inputFrame.gray();
//
//        // Apply Gaussian blur to reduce noise
//        Mat blurredFrame = new Mat();
//        Imgproc.GaussianBlur(grayFrame, blurredFrame, new Size(5, 5), 0);
//
//        // Perform Canny edge detection
//        Mat edges = new Mat();
//        Imgproc.Canny(blurredFrame, edges, 50, 150); // Adjust thresholds as needed
//
//        // Detect lines using Probabilistic Hough Line Transform
//        Mat lines = new Mat();
//        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 100, 50, 10);
//
//        // Create a blank image for drawing merged lines
//        Mat lineImage = Mat.zeros(edges.size(), CvType.CV_8UC3);
//        List<Line> mergedLines = new ArrayList<>();
//
//        // Resize if sizes are not equal
//        if (!lineImage.size().equals(inputFrame.rgba().size())) {
//            Imgproc.resize(lineImage, lineImage, inputFrame.rgba().size());
//        }
//
//        // Ensure both images have the same number of channels (RGBA)
//        if (lineImage.channels() == 3) {
//            Mat temp = new Mat();
//            Imgproc.cvtColor(lineImage, temp, Imgproc.COLOR_RGB2RGBA);
//            lineImage = temp;
//        }
//
//        // Process the detected lines to merge collinear and adjacent segments
//        if (lines.rows() > 0) {
//            for (int i = 0; i < lines.rows(); i++) {
//                double[] l = lines.get(i, 0);
//                Point p1 = new Point(l[0], l[1]);
//                Point p2 = new Point(l[2], l[3]);
//                Line newLine = new Line(p1, p2);
//
//                boolean merged = false;
//                for (Line existingLine : mergedLines) {
//                    if (existingLine.canMergeWith(newLine)) {
//                        existingLine.merge(newLine);
//                        merged = true;
//                        break;
//                    }
//                }
//
//                if (!merged) {
//                    mergedLines.add(newLine);
//                }
//            }
//        }
//
//        // Draw the merged lines
//        for (Line line : mergedLines) {
//            Imgproc.line(lineImage, line.start, line.end, new Scalar(255, 0, 0), 2);
//        }
//
//
//
//        // Combine the original frame with the line image
//        // Perform addWeighted to combine images
//        Mat combinedFrame = new Mat();
//        Core.addWeighted(inputFrame.rgba(), 0.8, lineImage, 1.0, 0, combinedFrame);
//
//        // Release resources
//        grayFrame.release();
//        blurredFrame.release();
//        edges.release();
//        lines.release();
//        lineImage.release();
//
//        return combinedFrame;
//    }



//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
//
//        Mat mat = inputFrame.rgba();
//
//
//        Mat edges = docDetect.detectEdges(mat, 7, 100, 200, true);
//
//
////        List<DocDetect.Pair<Double, Double>> lines = DocDetect.detectLines(edges, 65, 30);
//
//        // Step 2: Detect lines
//        int houghThreshold = 65;
//        int groupSimilarThreshold = 30;
//        List<DocDetect.Line> lines = docDetect.detectLines(edges, houghThreshold, groupSimilarThreshold);
//
//        // Step 3: Process the detected lines (print or visualize them)
////        for (DocDetect.Line line : lines) {
////            System.out.println("Line detected: rho=" + line.rho + ", theta=" + line.theta);
////        }
//
//        // Optionally, draw the detected lines on the original image
////        Mat outputImage = drawLinesOnImage(mat, lines);
//        // Add more lines as detected
//        // Call findIntersections
//        int imageWidth = 1024;  // Example width
//        int imageHeight = 768;  // Example height
//        double angleThreshold = 45;
//        List<IntersectionFinder.Intersection> intersections = IntersectionFinder.findIntersections(lines, imageWidth, imageHeight, angleThreshold);
//
//        for (IntersectionFinder.Intersection intersection : intersections) {
//            System.out.println("Intersection ID: " + intersection.id +
//                    ", Coordinates: (" + intersection.coords.x + ", " + intersection.coords.y + ")");
//        }
//
//        // Draw the intersections on the image
//        IntersectionFinder.drawIntersections(mat, intersections, new Scalar(0, 0, 255), 5, 2);  // Red color, 5px radius
//
//
//
//
//        return mat;
//
//
//
//    }


    private static Mat drawLinesOnImage(Mat image, List<DocDetect.Line> lines) {
//        Mat colorImage = new Mat();


        for (DocDetect.Line line : lines) {
            double rho = line.rho;
            double theta = line.theta;
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a * rho;
            double y0 = b * rho;

            // Define the line endpoints
            Point pt1 = new Point(Math.round(x0 + 1000 * (-b)), Math.round(y0 + 1000 * (a)));
            Point pt2 = new Point(Math.round(x0 - 1000 * (-b)), Math.round(y0 - 1000 * (a)));

            // Draw the line
            Imgproc.line(image, pt1, pt2, new Scalar(0, 0, 255), 2);
        }

        return image;
    }



//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
//        DocumentDetector dec = new DocumentDetector();
//       return dec.onCameraFrame(inputFrame.rgba());
//
//
//
//    }



// #### **Main Processing Function: `onCameraFrame`**


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // Get the current frame in RGBA format
        Mat rgba = inputFrame.rgba();

        // Invert the image
        Mat inverted = new Mat();
        Core.bitwise_not(rgba, inverted);

        // Detect edges
        Mat edges = detectEdges(inverted, 7, 50, 100, true);


//         Detect lines
        List<Line> uniqueLines = detectLines(edges, 65, 30);


//        for (Line line : uniqueLines) {
//            System.out.println("Rho: " + line.rho + ", Theta: " + line.rho);
//        }
//         rgba = detectLines(edges, 65, 30);


        Mat outputImage = newdrawLinesOnImage(rgba, uniqueLines);


////         Find intersections
//        List<Intersection> intersections = findIntersections(uniqueLines, inverted.size(), 45);
//
////         Find quadrilaterals
//        List<MatOfPoint> quadrilaterals = findQuadrilaterals(intersections);
//
//        // Draw quadrilaterals on the original image
//        drawQuadrilaterals(quadrilaterals, rgba);
//
////         Release intermediate Mats
//        inverted.release();
//        edges.release();

        return rgba;
    }

    private static Mat newdrawLinesOnImage(Mat image, List<Line> lines) {
//        Mat colorImage = new Mat();


        for (Line line : lines) {
            double rho = line.rho;
            double theta = line.theta;
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a * rho;
            double y0 = b * rho;

            // Define the line endpoints
            Point pt1 = new Point(Math.round(x0 + 1000 * (-b)), Math.round(y0 + 1000 * (a)));
            Point pt2 = new Point(Math.round(x0 - 1000 * (-b)), Math.round(y0 - 1000 * (a)));

            // Draw the line
            Imgproc.line(image, pt1, pt2, new Scalar(0, 0, 255), 2);
        }

        return image;
    }


//#### **Edge Detection Functions**



    // Preprocess the image by converting to HSV, extracting the Value channel, and applying median blur
    public Mat preprocess(Mat im, int blurRadius) {
        // Convert image to HSV color space
        Mat hsv = new Mat();
        Imgproc.cvtColor(im, hsv, Imgproc.COLOR_BGR2HSV);

        // Split the HSV image into separate channels
        List<Mat> hsvChannels = new ArrayList<>();
        Core.split(hsv, hsvChannels);

        // Extract the Value (V) channel
        Mat valueChannel = hsvChannels.get(2); // Index 2 corresponds to the V channel

        // Apply median blur if required
        if (blurRadius != 0) {
            Imgproc.medianBlur(valueChannel, valueChannel, blurRadius);
        }


        // Release unused Mats
        hsv.release();
        hsvChannels.get(0).release(); // H channel
        hsvChannels.get(1).release(); // S channel

        return valueChannel;
    }
    // Detect edges using Canny edge detection and remove text regions
    public Mat  detectEdges(Mat im, int blurRadius, double thr1, double thr2, boolean removeText) {
        Mat enhancedIm = preprocess(im, blurRadius);
        Mat edges = new Mat();
        Imgproc.Canny(enhancedIm, edges, thr1, thr2);

//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
//        Imgproc.dilate(edges, edges, kernel);

        if (removeText) {
            int width = im.cols();
            int height = im.rows();
            int maxArea = (int) ((width * height) / 1e2);
            List<MatOfPoint> characters = findCharacters(im, maxArea);
            removeCharacters(characters, edges);
        }

        enhancedIm.release();
        return edges;
    }

    // Find text regions using MSER
    public List<MatOfPoint> findCharacters(Mat im, int maxArea) {
        Mat gray = new Mat();
        Imgproc.cvtColor(im, gray, Imgproc.COLOR_BGR2GRAY);
        List<MatOfPoint> regions = new ArrayList<>();
        MSER mser = MSER.create();
        mser.setMaxArea(maxArea);
        MatOfRect bboxes = new MatOfRect();
        mser.detectRegions(gray, regions, bboxes);
        gray.release();
        return regions;
    }
//     Remove text regions from the edge image
    public void removeCharacters(List<MatOfPoint> characters, Mat mask) {
        for (MatOfPoint character : characters) {
            Imgproc.fillPoly(mask, Arrays.asList(character), new Scalar(0));
        }
    }

//    private void removeCharacters(List<MatOfPoint> characters, Mat mask) {
//        for (MatOfPoint character : characters) {
//            for (Point point : character.toArray()) {
//                int x = (int) point.y; // Swap y and x because OpenCV uses (row, col)
//                int y = (int) point.x;
//                mask.put(x, y, 0); // Set the mask value at (x, y) to 0
//            }
//        }
//    }



    // Detect lines using Hough Transform and group similar lines
    public  List<Line> detectLines(Mat edges, double houghThr, double groupSimilarThr) {
        Mat linesMat = new Mat();
        Imgproc.HoughLines(edges, linesMat, 1, Math.PI / 180, (int) houghThr);

        List<Line> lines = new ArrayList<>();
        if (linesMat.rows() > 0) {
            for (int i = 0; i < linesMat.rows(); i++) {
                double[] data = linesMat.get(i, 0);
                lines.add(new Line(data[0], data[1]));
            }
        }

//        if (groupSimilarThr != 0) {
//            lines = groupSimilar(lines, groupSimilarThr);
//        }

        linesMat.release();

        return lines;
    }

    // Group similar lines to reduce duplicates
    public List<Line> groupSimilar(List<Line> lines, double thr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lines.sort(Comparator.comparingDouble(line -> line.rho));
        }
        List<Line> uniqueLines = new ArrayList<>();
        for (Line toAdd : lines) {
            if (!isDuplicated(toAdd, uniqueLines, thr)) {
                uniqueLines.add(toAdd);
            }
        }
        return uniqueLines;
    }


    // Check if a line is a duplicate of any line in a list
    public boolean isDuplicated(Line line, List<Line> lines, double thr) {
        for (Line existingLine : lines) {
            if (Math.abs(Math.abs(line.rho) - Math.abs(existingLine.rho)) < thr) {
                return true;
            }
        }
        return false;
    }

    // Line class to store rho and theta
    public class Line {
        public double rho;
        public double theta;

        public Line(double rho, double theta) {
            this.rho = rho;
            this.theta = theta;
        }
    }

    // Find intersections between lines not similar in angle
    public List<Intersection> findIntersections(List<Line> lines, Size imageSize, double angleThr) {
        List<Intersection> intersections = new ArrayList<>();
        int vertexId = 0;
        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                Line line1 = lines.get(i);
                Line line2 = lines.get(j);

                if (anglesAreSimilar(line1, line2, angleThr)) {
                    continue;
                }

                Point coords = findIntersectionCoords(line1, line2);
                if (coordsAreValid(coords, imageSize) && !alreadyPresent(coords, intersections)) {
                    intersections.add(new Intersection(vertexId++, line1, line2, coords));
                }
            }
        }
        return intersections;
    }

    // Check if angles are similar
    public boolean anglesAreSimilar(Line line1, Line line2, double angleThr) {
        double angleDifference = Math.abs(line1.theta - line2.theta) * 180 / Math.PI;
        return angleDifference < angleThr;
    }

    // Find intersection coordinates of two lines
    public Point findIntersectionCoords(Line line1, Line line2) {
        double rho1 = line1.rho, theta1 = line1.theta;
        double rho2 = line2.rho, theta2 = line2.theta;
        double a1 = Math.cos(theta1), b1 = Math.sin(theta1);
        double a2 = Math.cos(theta2), b2 = Math.sin(theta2);
        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            return new Point(-1, -1); // Lines are parallel
        } else {
            double x = (b2 * rho1 - b1 * rho2) / determinant;
            double y = (a1 * rho2 - a2 * rho1) / determinant;
            return new Point(x, y);
        }
    }

    // Check if coordinates are within image boundaries
    public boolean coordsAreValid(Point coords, Size imageSize) {
        return coords.x > 0 && coords.x < imageSize.width && coords.y > 0 && coords.y < imageSize.height;
    }

    // Check if the intersection point already exists
    public boolean alreadyPresent(Point coords, List<Intersection> intersections) {
        for (Intersection intersection : intersections) {
            if (Math.abs(intersection.coords.x - coords.x) < 1 && Math.abs(intersection.coords.y - coords.y) < 1) {
                return true;
            }
        }
        return false;
    }
    // Intersection class to store intersection details
    public class Intersection {
        public int id;
        public Line line1;
        public Line line2;
        public Point coords;

        public Intersection(int id, Line line1, Line line2, Point coords) {
            this.id = id;
            this.line1 = line1;
            this.line2 = line2;
            this.coords = coords;
        }
    }


//   #### **Quadrilateral Detection Functions**


    // Find quadrilaterals from intersections
    public List<MatOfPoint> findQuadrilaterals(List<Intersection> intersections) {
        Map<Integer, List<Integer>> graph = buildGraph(intersections);
        Set<List<Integer>> loops = new HashSet<>();
        for (Integer node : graph.keySet()) {
            boundedDFS(graph, node, loops, new ArrayList<>());
        }
        return cyclesToCoords(new ArrayList<>(loops), intersections);
    }

    // Build a graph from intersections
    public Map<Integer, List<Integer>> buildGraph(List<Intersection> intersections) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (Intersection i1 : intersections) {
            List<Integer> neighbors = new ArrayList<>();
            for (Intersection i2 : intersections) {
                if (i1.id != i2.id && commonLineExists(i1, i2)) {
                    neighbors.add(i2.id);
                }
            }
            graph.put(i1.id, neighbors);
        }
        return graph;
    }

    // Check if two intersections share a common line
    public boolean commonLineExists(Intersection i1, Intersection i2) {
        return (i1.line1.equals(i2.line1) || i1.line1.equals(i2.line2) || i1.line2.equals(i2.line1) || i1.line2.equals(i2.line2));
    }

    // Perform bounded DFS to find cycles of length four
    public void boundedDFS(Map<Integer, List<Integer>> graph, Integer current, Set<List<Integer>> loops, List<Integer> path) {
        path.add(current);
        if (path.size() == 4) {
            if (graph.get(current).contains(path.get(0))) {
                List<Integer> cycle = new ArrayList<>(path);
                Collections.sort(cycle);
                loops.add(cycle);
            }
        } else {
            for (Integer neighbor : graph.get(current)) {
                if (!path.contains(neighbor)) {
                    boundedDFS(graph, neighbor, loops, path);
                }
            }
        }
        path.remove(current);
    }
//    / Convert cycles to quadrilateral coordinates
    public List<MatOfPoint> cyclesToCoords(List<List<Integer>> cycles, List<Intersection> intersections) {
        List<MatOfPoint> quadrilaterals = new ArrayList<>();
        for (List<Integer> cycle : cycles) {
            List<Point> points = new ArrayList<>();
            for (Integer nodeId : cycle) {
                for (Intersection intersection : intersections) {
                    if (intersection.id == nodeId) {
                        points.add(intersection.coords);
                        break;
                    }
                }
            }
            if (points.size() == 4) {
                quadrilaterals.add(new MatOfPoint(points.toArray(new Point[0])));
            }
        }
        return quadrilaterals;
    }

//    #### **Drawing Functions**


    // Draw quadrilaterals on the image
    public void drawQuadrilaterals(List<MatOfPoint> quadrilaterals, Mat im) {
        if (quadrilaterals.isEmpty()) {
            return;
        }

        // For debugging: draw all quadrilaterals
        // for (MatOfPoint rect : quadrilaterals) {
        //     Imgproc.polylines(im, Arrays.asList(rect), true, new Scalar(0, 255, 0), 2);
        // }

        // Draw the best quadrilateral (the one with the largest area)
        MatOfPoint bestQuad = null;
        double maxArea = 0;
        for (MatOfPoint quad : quadrilaterals) {
            double area = Imgproc.contourArea(quad);
            if (area > maxArea) {
                maxArea = area;
                bestQuad = quad;
            }
        }

        if (bestQuad != null) {
            Imgproc.polylines(im, Arrays.asList(bestQuad), true, new Scalar(255, 0, 0), 4);
        }
    }

}
//class Line {
//    Point start;
//    Point end;
//
//    public Line(Point start, Point end) {
//        this.start = start;
//        this.end = end;
//    }
//
//    public boolean canMergeWith(Line other) {
//        // Check angle similarity (e.g., less than 10 degrees difference)
//        double angle1 = Math.atan2(end.y - start.y, end.x - start.x);
//        double angle2 = Math.atan2(other.end.y - other.start.y, other.end.x - other.start.x);
//        if (Math.abs(angle1 - angle2) > Math.toRadians(10)) {
//            return false;
//        }
//
//        // Check proximity of line segments
//        double distance = Math.min(
//                pointToLineDistance(start, other.start, other.end),
//                pointToLineDistance(end, other.start, other.end)
//        );
//        return distance < 20; // Adjust proximity threshold as needed
//    }
//
//    public void merge(Line other) {
//        // Extend the line to include the other segment
//        this.start = minPoint(start, other.start, other.end);
//        this.end = maxPoint(end, other.start, other.end);
//    }
//
//    private Point minPoint(Point p1, Point p2, Point p3) {
//        return (p1.x < p2.x) ? (p1.x < p3.x ? p1 : p3) : (p2.x < p3.x ? p2 : p3);
//    }
//
//    private Point maxPoint(Point p1, Point p2, Point p3) {
//        return (p1.x > p2.x) ? (p1.x > p3.x ? p1 : p3) : (p2.x > p3.x ? p2 : p3);
//    }
//
//    private double pointToLineDistance(Point p, Point lineStart, Point lineEnd) {
//        double A = p.x - lineStart.x;
//        double B = p.y - lineStart.y;
//        double C = lineEnd.x - lineStart.x;
//        double D = lineEnd.y - lineStart.y;
//
//        double dot = A * C + B * D;
//        double lenSq = C * C + D * D;
//        double param = lenSq == 0 ? -1 : dot / lenSq;
//
//        double xx, yy;
//
//        if (param < 0) {
//            xx = lineStart.x;
//            yy = lineStart.y;
//        } else if (param > 1) {
//            xx = lineEnd.x;
//            yy = lineEnd.y;
//        } else {
//            xx = lineStart.x + param * C;
//            yy = lineStart.y + param * D;
//        }
//
//        double dx = p.x - xx;
//        double dy = p.y - yy;
//
//        return Math.sqrt(dx * dx + dy * dy);
//    }
//}
