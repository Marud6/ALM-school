package models;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class RectangleShape extends Shape {

    private Point topLeft;
    private int width;
    private int height;
    private boolean square;

    public RectangleShape(Point topLeft, int width, int height, boolean square,
                          Color color, int thickness, LineStyle lineStyle) {
        this.topLeft = topLeft;
        this.width = width;
        this.height = height;
        this.square = square;
        this.color = color;
        this.thickness = thickness;
        this.lineStyle = lineStyle;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isSquare() {
        return square;
    }

    @Override
    public List<Point> getHandles() {
        int x = topLeft.getX(), y = topLeft.getY();
        return Arrays.asList(
                new Point(x, y),
                new Point(x + width, y),
                new Point(x + width, y + height),
                new Point(x, y + height)
        );
    }

    @Override
    public void moveBy(int dx, int dy) {
        topLeft.setX(topLeft.getX() + dx);
        topLeft.setY(topLeft.getY() + dy);
    }

    @Override
    public boolean contains(Point p) {
        int x = topLeft.getX(), y = topLeft.getY();
        int px = p.getX(), py = p.getY();
        int tol = Math.max(5, thickness);
        boolean nearTop = py >= y - tol && py <= y + tol && px >= x - tol && px <= x + width + tol;
        boolean nearBottom = py >= y + height - tol && py <= y + height + tol && px >= x - tol && px <= x + width + tol;
        boolean nearLeft = px >= x - tol && px <= x + tol && py >= y - tol && py <= y + height + tol;
        boolean nearRight = px >= x + width - tol && px <= x + width + tol && py >= y - tol && py <= y + height + tol;
        return nearTop || nearBottom || nearLeft || nearRight;
    }

    @Override
    public int[] getBoundingBox() {
        return new int[]{topLeft.getX(), topLeft.getY(),
                topLeft.getX() + width, topLeft.getY() + height};
    }

    @Override
    public void moveHandle(int handleIndex, Point newPos) {
        int x = topLeft.getX(), y = topLeft.getY();
        switch (handleIndex) {
            case 0: // top-left
                width += x - newPos.getX();
                height += y - newPos.getY();
                topLeft = newPos;
                break;
            case 1: // top-right
                width = newPos.getX() - x;
                height += y - newPos.getY();
                topLeft.setY(newPos.getY());
                break;
            case 2: // bottom-right
                width = newPos.getX() - x;
                height = newPos.getY() - y;
                break;
            case 3: // bottom-left
                width += x - newPos.getX();
                height = newPos.getY() - y;
                topLeft.setX(newPos.getX());
                break;
        }
    }
}
