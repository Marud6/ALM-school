package rasterizers;

import models.LineStyle;
import models.Point;
import models.Polygon;

import java.util.List;

public class PolygonRasterizer {

    private final TrivialRasterizer lineRasterizer;

    public PolygonRasterizer(TrivialRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        List<Point> verts = polygon.getVertices();
        if (verts.size() < 2) return;

        int color = polygon.getColor().getRGB();
        LineStyle style = polygon.getLineStyle();
        int thick = polygon.getThickness();

        for (int i = 0; i < verts.size(); i++) {
            Point a = verts.get(i);
            Point b = verts.get((i + 1) % verts.size());
            lineRasterizer.rasterizeLine(a.getX(), a.getY(), b.getX(), b.getY(), color, style, thick);
        }
    }
}
