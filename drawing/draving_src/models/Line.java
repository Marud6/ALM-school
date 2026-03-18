package models;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Line extends Shape {

    private Point p1, p2;

    public Line(Point p1, Point p2, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }

    public Line(Point p1, Point p2, Color color, boolean dotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.lineStyle = dotted ? LineStyle.DOTTED : LineStyle.SOLID;
    }

    public Line(Point p1, Point p2, Color color, int thickness, LineStyle lineStyle) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.thickness = thickness;
        this.lineStyle = lineStyle;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public boolean isDotted() {
        return lineStyle == LineStyle.DOTTED;
    }

    @Override
    public List<Point> getHandles() {
        return Arrays.asList(p1, p2);
    }

    @Override
    public void moveBy(int dx, int dy) {
        p1.setX(p1.getX() + dx);
        p1.setY(p1.getY() + dy);
        p2.setX(p2.getX() + dx);
        p2.setY(p2.getY() + dy);
    }

    @Override
    public boolean contains(Point p) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double lenSq = dx * dx + dy * dy;
        if (lenSq == 0) return Math.hypot(p.getX() - p1.getX(), p.getY() - p1.getY()) <= Math.max(5, thickness);
        double t = ((p.getX() - p1.getX()) * dx + (p.getY() - p1.getY()) * dy) / lenSq;
        t = Math.max(0, Math.min(1, t));
        double projX = p1.getX() + t * dx;
        double projY = p1.getY() + t * dy;
        return Math.hypot(p.getX() - projX, p.getY() - projY) <= Math.max(5, thickness);
    }

    @Override
    public int[] getBoundingBox() {
        return new int[]{
                Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
                Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY())
        };
    }

    @Override
    public void moveHandle(int handleIndex, Point newPos) {
        if (handleIndex == 0) p1 = newPos;
        else p2 = newPos;
    }
}
