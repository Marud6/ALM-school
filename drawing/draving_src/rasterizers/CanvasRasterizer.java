package rasterizers;
import models.Line;
import models.LineCanvas;

public class CanvasRasterizer {

    private Rasterizer lineRasterizer;
    private Rasterizer dottedLineRasterizer;

    public CanvasRasterizer(Rasterizer lineRasterizer, Rasterizer dottedLineRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.dottedLineRasterizer = dottedLineRasterizer;
    }
    public void rasterize(LineCanvas lineCanvas) {
        for (Line line : lineCanvas.getLines()) {
            if (line.getColor() != null) {
                lineRasterizer.setColor(line.getColor());
            }
            lineRasterizer.setDotted(line.isDotted());
            lineRasterizer.rasterize(line);
        }
    }

}
