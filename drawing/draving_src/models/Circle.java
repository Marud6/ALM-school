package models;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Circle extends Shape {

    private Point center;
    private int radius;

    public Circle(Point center, int radius, Color color, int thickness, LineStyle lineStyle) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.thickness = thickness;
        this.lineStyle = lineStyle;
    }

    public Point getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = Math.max(0, radius);
    }

    @Override
    public List<Point> getHandles() {
        return Arrays.asList(
                center,
                new Point(center.getX() + radius, center.getY()),
                new Point(center.getX(), center.getY() - radius),
                new Point(center.getX() - radius, center.getY()),
                new Point(center.getX(), center.getY() + radius)
        );
    }

    @Override
    public void moveBy(int dx, int dy) {
        center.setX(center.getX() + dx);
        center.setY(center.getY() + dy);
    }

    @Override
    public boolean contains(Point p) {
        double dist = Math.hypot(p.getX() - center.getX(), p.getY() - center.getY());
        return Math.abs(dist - radius) <= Math.max(5, thickness);
    }

    @Override
    public int[] getBoundingBox() {
        return new int[]{
                center.getX() - radius, center.getY() - radius,
                center.getX() + radius, center.getY() + radius
        };
    }

    @Override
    public void moveHandle(int handleIndex, Point newPos) {
        if (handleIndex == 0) {
            center = newPos;
        } else {
            radius = (int) Math.hypot(newPos.getX() - center.getX(), newPos.getY() - center.getY());
        }
    }
}
