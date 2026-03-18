package rasterizers;

import models.FillShape;
import rasters.Raster;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class FillRasterizer {

    private final Raster raster;

    public FillRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(FillShape fill) {
        int fillColor = fill.getColor().getRGB();

        if (fill.hasFilledPixels()) {
            // Replay cached pixels
            for (int[] px : fill.getFilledPixels()) {
                if (px[0] >= 0 && px[0] < raster.getWidth() && px[1] >= 0 && px[1] < raster.getHeight()) {
                    raster.setPixel(px[0], px[1], fillColor);
                }
            }
            return;
        }

        // First time: run flood fill and cache the pixels
        int seedX = fill.getX();
        int seedY = fill.getY();
        if (seedX < 0 || seedX >= raster.getWidth() || seedY < 0 || seedY >= raster.getHeight()) return;

        int targetColor = raster.getPixel(seedX, seedY);
        if (targetColor == fillColor) return;

        List<int[]> filledPixels = new ArrayList<>();
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{seedX, seedY});

        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int x = pos[0];
            int y = pos[1];

            if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) continue;
            if (raster.getPixel(x, y) != targetColor) continue;

            // scan left
            int left = x;
            while (left > 0 && raster.getPixel(left - 1, y) == targetColor) left--;

            // scan right
            int right = x;
            while (right < raster.getWidth() - 1 && raster.getPixel(right + 1, y) == targetColor) right++;

            // fill the span
            boolean abovePushed = false;
            boolean belowPushed = false;
            for (int i = left; i <= right; i++) {
                raster.setPixel(i, y, fillColor);
                filledPixels.add(new int[]{i, y});

                // check above
                if (y > 0) {
                    if (raster.getPixel(i, y - 1) == targetColor) {
                        if (!abovePushed) {
                            stack.push(new int[]{i, y - 1});
                            abovePushed = true;
                        }
                    } else {
                        abovePushed = false;
                    }
                }

                // check below
                if (y < raster.getHeight() - 1) {
                    if (raster.getPixel(i, y + 1) == targetColor) {
                        if (!belowPushed) {
                            stack.push(new int[]{i, y + 1});
                            belowPushed = true;
                        }
                    } else {
                        belowPushed = false;
                    }
                }
            }
        }

        fill.setFilledPixels(filledPixels);
    }
}
