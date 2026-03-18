package models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Polygon extends Shape {

    private List<Point> vertices;

    public Polygon(List<Point> vertices, Color color, int thickness, LineStyle lineStyle) {
        this.vertices = new ArrayList<>(vertices);
        this.color = color;
        this.thickness = thickness;
        this.lineStyle = lineStyle;
    }

    public List<Point> getVertices() {
        return vertices;
    }

    @Override
    public List<Point> getHandles() {
        return new ArrayList<>(vertices);
    }

    @Override
    public void moveBy(int dx, int dy) {
        for (Point v : vertices) {
            v.setX(v.getX() + dx);
            v.setY(v.getY() + dy);
        }
    }

    @Override
    public boolean contains(Point p) {
        int tol = Math.max(5, thickness);
        for (int i = 0; i < vertices.size(); i++) {
            Point a = vertices.get(i);
            Point b = vertices.get((i + 1) % vertices.size());
            if (distToSegment(p, a, b) <= tol) return true;
        }
        return false;
    }

    @Override
    public int[] getBoundingBox() {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Point v : vertices) {
            minX = Math.min(minX, v.getX());
            minY = Math.min(minY, v.getY());
            maxX = Math.max(maxX, v.getX());
            maxY = Math.max(maxY, v.getY());
        }
        return new int[]{minX, minY, maxX, maxY};
    }

    @Override
    public void moveHandle(int handleIndex, Point newPos) {
        if (handleIndex >= 0 && handleIndex < vertices.size()) {
            vertices.set(handleIndex, newPos);
        }
    }

    private double distToSegment(Point p, Point a, Point b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double lenSq = dx * dx + dy * dy;
        if (lenSq == 0) return Math.hypot(p.getX() - a.getX(), p.getY() - a.getY());
        double t = ((p.getX() - a.getX()) * dx + (p.getY() - a.getY()) * dy) / lenSq;
        t = Math.max(0, Math.min(1, t));
        double projX = a.getX() + t * dx;
        double projY = a.getY() + t * dy;
        return Math.hypot(p.getX() - projX, p.getY() - projY);
    }
}
