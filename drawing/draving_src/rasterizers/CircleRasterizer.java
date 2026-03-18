package rasterizers;

import models.Circle;
import models.LineStyle;
import rasters.Raster;

public class CircleRasterizer {

    private final Raster raster;

    public CircleRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Circle circle) {
        int cx = circle.getCenter().getX();
        int cy = circle.getCenter().getY();
        int r = circle.getRadius();
        int color = circle.getColor().getRGB();
        LineStyle style = circle.getLineStyle();
        int thick = circle.getThickness();

        if (r <= 0) {
            setSafePixel(cx, cy, color);
            return;
        }

        int x = 0;
        int y = r;
        int d = 1 - r;
        int counter = 0;

        while (x <= y) {
            if (TrivialRasterizer.shouldDraw(style, counter)) {
                if (thick <= 1) {
                    plot8(cx, cy, x, y, color);
                } else {
                    stamp8(cx, cy, x, y, thick / 2, color);
                }
            }
            if (d < 0) {
                d += 2 * x + 3;
            } else {
                d += 2 * (x - y) + 5;
                y--;
            }
            x++;
            counter++;
        }
    }

    private void plot8(int cx, int cy, int x, int y, int color) {
        setSafePixel(cx + x, cy + y, color);
        setSafePixel(cx - x, cy + y, color);
        setSafePixel(cx + x, cy - y, color);
        setSafePixel(cx - x, cy - y, color);
        setSafePixel(cx + y, cy + x, color);
        setSafePixel(cx - y, cy + x, color);
        setSafePixel(cx + y, cy - x, color);
        setSafePixel(cx - y, cy - x, color);
    }

    private void stamp8(int cx, int cy, int x, int y, int r, int color) {
        stampDisk(cx + x, cy + y, r, color);
        stampDisk(cx - x, cy + y, r, color);
        stampDisk(cx + x, cy - y, r, color);
        stampDisk(cx - x, cy - y, r, color);
        stampDisk(cx + y, cy + x, r, color);
        stampDisk(cx - y, cy + x, r, color);
        stampDisk(cx + y, cy - x, r, color);
        stampDisk(cx - y, cy - x, r, color);
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

    private void setSafePixel(int x, int y, int color) {
        if (x >= 0 && x < raster.getWidth()
                && y >= 0 && y < raster.getHeight()) {
            raster.setPixel(x, y, color);
        }
    }
}
