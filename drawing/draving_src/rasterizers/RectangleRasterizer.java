package rasterizers;

import models.LineStyle;
import models.Point;
import models.RectangleShape;

public class RectangleRasterizer {

    private final TrivialRasterizer lineRasterizer;

    public RectangleRasterizer(TrivialRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(RectangleShape rect) {
        int x = rect.getTopLeft().getX();
        int y = rect.getTopLeft().getY();
        int w = rect.getWidth();
        int h = rect.getHeight();
        int color = rect.getColor().getRGB();
        LineStyle style = rect.getLineStyle();
        int thick = rect.getThickness();

        // 4 edges
        lineRasterizer.rasterizeLine(x, y, x + w, y, color, style, thick);
        lineRasterizer.rasterizeLine(x + w, y, x + w, y + h, color, style, thick);
        lineRasterizer.rasterizeLine(x + w, y + h, x, y + h, color, style, thick);
        lineRasterizer.rasterizeLine(x, y + h, x, y, color, style, thick);
    }
}
