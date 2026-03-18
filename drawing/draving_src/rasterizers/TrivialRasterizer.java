package rasterizers;

import models.Line;
import models.LineStyle;
import rasters.Raster;

import java.awt.Color;

public class TrivialRasterizer implements Rasterizer {

    private Color defaultColor;
    private final Raster raster;
    private LineStyle lineStyle = LineStyle.SOLID;
    private int thickness = 1;

    public TrivialRasterizer(Raster raster, Color defaultColor) {
        this.raster = raster;
        this.defaultColor = defaultColor;
    }

    @Override
    public void setColor(Color color) {
        defaultColor = color;
    }

    @Override
    public void setDotted(boolean dotted) {
        this.lineStyle = dotted ? LineStyle.DOTTED : LineStyle.SOLID;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    @Override
    public void rasterize(Line line) {
        int x1 = line.getP1().getX();
        int y1 = line.getP1().getY();
        int x2 = line.getP2().getX();
        int y2 = line.getP2().getY();

        Color col = line.getColor() != null ? line.getColor() : defaultColor;
        LineStyle style = line.getLineStyle() != null ? line.getLineStyle() : this.lineStyle;
        int thick = line.getThickness() > 0 ? line.getThickness() : this.thickness;

        rasterizeLine(x1, y1, x2, y2, col.getRGB(), style, thick);
    }

    public void rasterizeLine(int x1, int y1, int x2, int y2, int color, LineStyle style, int thick) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int x = x1;
        int y = y1;
        int counter = 0;

        while (true) {
            if (shouldDraw(style, counter)) {
                if (thick <= 1) {
                    setSafePixel(x, y, color);
                } else {
                    stampDisk(x, y, thick / 2, color);
                }
            }
            if (x == x2 && y == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
            counter++;
        }
    }

    static boolean shouldDraw(LineStyle style, int counter) {
        if (style == null || style == LineStyle.SOLID) return true;
        if (style == LineStyle.DASHED) {
            return (counter % 15) < 10; // 10 on, 5 off
        }
        // DOTTED: 2 on, 3 off
        return (counter % 5) < 2;
    }

    private void stampDisk(int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                if (dx * dx + dy * dy <= r * r) {
                    setSafePixel(cx + dx, cy + dy, color);
                }
            }
        }
    }

    void setSafePixel(int x, int y, int color) {
        if (x >= 0 && x < raster.getWidth()
                && y >= 0 && y < raster.getHeight()) {
            raster.setPixel(x, y, color);
        }
    }

    public Raster getRaster() {
        return raster;
    }
}
