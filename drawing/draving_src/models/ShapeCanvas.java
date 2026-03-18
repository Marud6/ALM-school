package models;

import java.util.ArrayList;
import java.util.List;

public class ShapeCanvas {

    private final List<Shape> shapes = new ArrayList<>();

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void clear() {
        shapes.clear();
    }

    public Shape shapeAt(Point p) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape s = shapes.get(i);
            if (!(s instanceof FillShape) && s.contains(p)) {
                return s;
            }
        }
        return null;
    }
}
