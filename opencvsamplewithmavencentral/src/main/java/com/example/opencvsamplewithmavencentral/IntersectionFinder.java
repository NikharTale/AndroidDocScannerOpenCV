package com.example.opencvsamplewithmavencentral;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class IntersectionFinder {

    public static class Intersection {
        public int id;
        public DocDetect.Line line1;
        public DocDetect.Line line2;
        public Point coords;

        public Intersection(int id, DocDetect.Line line1, DocDetect.Line line2, Point coords) {
            this.id = id;
            this.line1 = line1;
            this.line2 = line2;
            this.coords = coords;
        }
    }

    // Draw intersections as small circles
    public static void drawIntersections(Mat image, List<IntersectionFinder.Intersection> intersections, Scalar color, int radius, int thickness) {
        for (IntersectionFinder.Intersection intersection : intersections) {
            // Use the intersection's coordinates (Point coords)
            Point point = intersection.coords;
            Imgproc.circle(image, point, radius, color, thickness);
        }
    }

    public static List<Intersection> findIntersections(List<DocDetect.Line> lines, int width, int height, double angleThreshold) {
        List<Intersection> intersections = new ArrayList<>();
        int vertexId = 0;

        // Iterate over all pairs of lines
        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                DocDetect.Line line1 = lines.get(i);
                DocDetect.Line line2 = lines.get(j);

                // Calculate the angle between lines, skip similar angles
                if (linesAngle(line1, line2) < angleThreshold) {
                    continue;
                }

                // Find intersection coordinates
                Point coords = findIntersectionCoords(line1, line2);

                if (coords != null && coords.x >= 0 && coords.x < width && coords.y >= 0 && coords.y < height) {
                    intersections.add(new Intersection(vertexId++, line1, line2, coords));
                }
            }
        }
        return intersections;
    }

    private static Point findIntersectionCoords(DocDetect.Line line1, DocDetect.Line line2) {
        double rho1 = line1.rho;
        double theta1 = line1.theta;
        double rho2 = line2.rho;
        double theta2 = line2.theta;

        // Calculate intersection point
        double a1 = Math.cos(theta1);
        double b1 = Math.sin(theta1);
        double a2 = Math.cos(theta2);
        double b2 = Math.sin(theta2);

        double det = a1 * b2 - a2 * b1;
        if (Math.abs(det) < 1e-6) {
            return null;  // Lines are parallel, no intersection
        }

        double x = (b2 * rho1 - b1 * rho2) / det;
        double y = (a1 * rho2 - a2 * rho1) / det;

        return new Point(x, y);
    }

    private static double linesAngle(DocDetect.Line line1, DocDetect.Line line2) {
        return Math.abs(line1.theta - line2.theta) * 180 / Math.PI;
    }
}


