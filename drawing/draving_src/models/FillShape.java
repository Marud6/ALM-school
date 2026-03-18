package models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FillShape extends Shape {

    private int x, y;
    private List<int[]> filledPixels;  // cached pixel positions from flood fill

    public FillShape(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasFilledPixels() {
        return filledPixels != null;
    }

    public List<int[]> getFilledPixels() {
        return filledPixels;
    }

    public void setFilledPixels(List<int[]> pixels) {
        this.filledPixels = pixels;
    }

    @Override
    public List<Point> getHandles() {
        return Collections.singletonList(new Point(x, y));
    }

    @Override
    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override
    public boolean contains(Point p) {
        return Math.abs(p.getX() - x) <= 5 && Math.abs(p.getY() - y) <= 5;
    }

    @Override
    public int[] getBoundingBox() {
        return new int[]{x, y, x, y};
    }

    @Override
    public void moveHandle(int handleIndex, Point newPos) {
        x = newPos.getX();
        y = newPos.getY();
    }
}
