// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;
/**
 * Base class used to do different calculations, such as distances between a line and a point, or a point and another
 * @author Adrian_antochi
 *
 */
public class Point {

    double x;
    double y;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double distance(Point lineX1, Point lineX2, Point p) {
        double A = p.x - lineX1.x;
        double B = p.y - lineX1.y;
        double C = lineX2.x - lineX1.x;
        double D = lineX2.y - lineX1.y;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = dot / len_sq;
        double xx = lineX1.x + param * C;
        double yy = lineX1.y + param * D;
        double dx = p.x - xx;
        double dy = p.y - yy;
        return Math.sqrt(dx * dx + dy * dy);

    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + "]";
    }

}
