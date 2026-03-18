package rasterizers;

import models.*;
import rasters.Raster;

public class ShapeCanvasRasterizer {

    private final TrivialRasterizer lineRasterizer;
    private final CircleRasterizer circleRasterizer;
    private final RectangleRasterizer rectangleRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final FillRasterizer fillRasterizer;

    public ShapeCanvasRasterizer(Raster raster, TrivialRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.circleRasterizer = new CircleRasterizer(raster);
        this.rectangleRasterizer = new RectangleRasterizer(lineRasterizer);
        this.polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        this.fillRasterizer = new FillRasterizer(raster);
    }

    public void rasterize(ShapeCanvas canvas) {
        // First pass: geometric shapes
        for (Shape shape : canvas.getShapes()) {
            if (shape instanceof FillShape) continue;
            rasterizeShape(shape);
        }
        // Second pass: fills (so they see the rendered geometry)
        for (Shape shape : canvas.getShapes()) {
            if (shape instanceof FillShape fill) {
                fillRasterizer.rasterize(fill);
            }
        }
    }

    private void rasterizeShape(Shape shape) {
        if (shape instanceof Line line) {
            lineRasterizer.rasterize(line);
        } else if (shape instanceof Circle circle) {
            circleRasterizer.rasterize(circle);
        } else if (shape instanceof RectangleShape rect) {
            rectangleRasterizer.rasterize(rect);
        } else if (shape instanceof Polygon polygon) {
            polygonRasterizer.rasterize(polygon);
        }
    }

    public TrivialRasterizer getLineRasterizer() {
        return lineRasterizer;
    }

    public CircleRasterizer getCircleRasterizer() {
        return circleRasterizer;
    }
}
