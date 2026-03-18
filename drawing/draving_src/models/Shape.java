package models;

import java.awt.Color;
import java.util.List;

public abstract class Shape {

    protected Color color = Color.GREEN;
    protected int thickness = 1;
    protected LineStyle lineStyle = LineStyle.SOLID;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public abstract List<Point> getHandles();

    public abstract void moveBy(int dx, int dy);

    public abstract boolean contains(Point p);

    public abstract int[] getBoundingBox(); // {minX, minY, maxX, maxY}

    public abstract void moveHandle(int handleIndex, Point newPos);
}
