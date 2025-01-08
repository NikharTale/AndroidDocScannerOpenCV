package com.example.opencvsamplewithmavencentral;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.features2d.MSER;
import java.util.*;

public class DocumentDetector {
    private MSER mser;

    public DocumentDetector() {
        mser = MSER.create();
    }

    public Mat onCameraFrame(Mat inputFrame) {
        Mat frame = inputFrame;
        List<MatOfPoint> quadrilaterals = process(frame);
        drawQuadrilaterals(frame, quadrilaterals);
        return frame;
    }

    private List<MatOfPoint> process(Mat image) {
        Core.bitwise_not(image, image);
        Mat edges = detectEdges(image, 7);
        List<double[]> lines = detectLines(edges);
        List<Map<String, Object>> intersections = findIntersections(lines, image);
        return findQuadrilaterals(intersections);
    }

    private Mat detectEdges(Mat image, int blurRadius) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        List<Mat> channels = new ArrayList<>();
        Core.split(hsv, channels);
        Mat saturation = channels.get(2);

        if (blurRadius > 0) {
            Imgproc.medianBlur(saturation, saturation, blurRadius);
        }

        Mat edges = new Mat();
        Imgproc.Canny(saturation, edges, 50, 100);

        // Updated MSER detection
        List<MatOfPoint> msers = new ArrayList<>();
        MatOfRect bboxes = new MatOfRect();
        mser.detectRegions(saturation, msers, bboxes);

        // Remove detected text regions
        for (Rect rect : bboxes.toArray()) {
            Imgproc.rectangle(edges, rect, new Scalar(0), -1);
        }

        return edges;
    }

    private List<double[]> detectLines(Mat edges) {
        Mat lines = new Mat();
        Imgproc.HoughLines(edges, lines, 1, Math.PI/90, 65);

        List<double[]> uniqueLines = new ArrayList<>();
        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            if (!isDuplicate(line, uniqueLines, 30)) {
                uniqueLines.add(line);
            }
        }
        return uniqueLines;
    }

    private boolean isDuplicate(double[] line, List<double[]> lines, double threshold) {
        for (double[] existingLine : lines) {
            if (Math.abs(Math.abs(line[0]) - Math.abs(existingLine[0])) < threshold) {
                return true;
            }
        }
        return false;
    }

    private List<Map<String, Object>> findIntersections(List<double[]> lines, Mat image) {
        List<Map<String, Object>> intersections = new ArrayList<>();
        int vertexId = 0;

        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                double[] line1 = lines.get(i);
                double[] line2 = lines.get(j);

                if (getLinesAngle(line1, line2) < 45) continue;

                Point intersection = findIntersectionPoint(line1, line2);
                if (intersection != null &&
                        intersection.x > 0 && intersection.x < image.cols() &&
                        intersection.y > 0 && intersection.y < image.rows()) {

                    Map<String, Object> vertex = new HashMap<>();
                    vertex.put("id", vertexId++);
                    vertex.put("lines", new double[][]{line1, line2});
                    vertex.put("coords", new Point[]{intersection});
                    intersections.add(vertex);
                }
            }
        }
        return intersections;
    }

    private Point findIntersectionPoint(double[] line1, double[] line2) {
        double rho1 = line1[0], theta1 = line1[1];
        double rho2 = line2[0], theta2 = line2[1];

        double[][] a = {
                {Math.cos(theta1), Math.sin(theta1)},
                {Math.cos(theta2), Math.sin(theta2)}
        };
        double[][] b = {{rho1}, {rho2}};

        try {
            // Solve system of equations
            double det = a[0][0] * a[1][1] - a[0][1] * a[1][0];
            if (Math.abs(det) < 1e-10) return null;

            double x = (b[0][0] * a[1][1] - a[0][1] * b[1][0]) / det;
            double y = (a[0][0] * b[1][0] - b[0][0] * a[1][0]) / det;

            return new Point(Math.round(x), Math.round(y));
        } catch (Exception e) {
            return null;
        }
    }

    private double getLinesAngle(double[] line1, double[] line2) {
        return Math.abs(line1[1] - line2[1]) * 180 / Math.PI;
    }

    private List<MatOfPoint> findQuadrilaterals(List<Map<String, Object>> intersections) {
        // Build adjacency graph
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (Map<String, Object> intersection : intersections) {
            graph.put((Integer)intersection.get("id"), new ArrayList<>());
        }

        // Connect vertices that share a line
        for (int i = 0; i < intersections.size(); i++) {
            for (int j = i + 1; j < intersections.size(); j++) {
                if (hasCommonLine(
                        (double[][])intersections.get(i).get("lines"),
                        (double[][])intersections.get(j).get("lines"))) {

                    int id1 = (Integer)intersections.get(i).get("id");
                    int id2 = (Integer)intersections.get(j).get("id");
                    graph.get(id1).add(id2);
                    graph.get(id2).add(id1);
                }
            }
        }

        // Find cycles of length 4 (quadrilaterals)
        List<List<Integer>> cycles = findCyclesOfLengthFour(graph);

        // Convert vertex IDs to points
        List<MatOfPoint> quadrilaterals = new ArrayList<>();
        for (List<Integer> cycle : cycles) {
            MatOfPoint quad = new MatOfPoint();
            Point[] points = new Point[4];
            for (int i = 0; i < 4; i++) {
                int id = cycle.get(i);
                for (Map<String, Object> intersection : intersections) {
                    if ((Integer)intersection.get("id") == id) {
                        points[i] = ((Point[])intersection.get("coords"))[0];
                        break;
                    }
                }
            }
            quad.fromArray(points);
            quadrilaterals.add(quad);
        }

        return quadrilaterals;
    }

    private boolean hasCommonLine(double[][] lines1, double[][] lines2) {
        for (double[] line1 : lines1) {
            for (double[] line2 : lines2) {
                if (Math.abs(line1[0] - line2[0]) < 1e-10 &&
                        Math.abs(line1[1] - line2[1]) < 1e-10) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<List<Integer>> findCyclesOfLengthFour(Map<Integer, List<Integer>> graph) {
        List<List<Integer>> cycles = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (Integer start : graph.keySet()) {
            dfs(start, start, 0, new ArrayList<>(), visited, graph, cycles);
            visited.clear();
        }

        return cycles;
    }

    private void dfs(int start, int current, int depth, List<Integer> path,
                     Set<Integer> visited, Map<Integer, List<Integer>> graph,
                     List<List<Integer>> cycles) {

        if (depth == 4) {
            if (graph.get(current).contains(start)) {
                cycles.add(new ArrayList<>(path));
            }
            return;
        }

        visited.add(current);
        path.add(current);

        for (int next : graph.get(current)) {
            if (!visited.contains(next)) {
                dfs(start, next, depth + 1, path, visited, graph, cycles);
            }
        }

        visited.remove(current);
        path.remove(path.size() - 1);
    }

    private void drawQuadrilaterals(Mat image, List<MatOfPoint> quadrilaterals) {
        if (quadrilaterals.isEmpty()) return;

        // Find largest quadrilateral
        MatOfPoint largest = quadrilaterals.get(0);
        double maxArea = Imgproc.contourArea(largest);

        for (int i = 1; i < quadrilaterals.size(); i++) {
            double area = Imgproc.contourArea(quadrilaterals.get(i));
            if (area > maxArea) {
                maxArea = area;
                largest = quadrilaterals.get(i);
            }
        }

        // Draw largest quadrilateral
        Imgproc.polylines(image, Arrays.asList(largest), true, new Scalar(0, 255, 0), 2);
    }
}
