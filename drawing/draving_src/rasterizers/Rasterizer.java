package rasterizers;
import models.Line;

import java.awt.*;

public interface Rasterizer {

    void setColor(Color color);

    void setDotted(boolean dotted);

    void rasterize(Line line);

}
