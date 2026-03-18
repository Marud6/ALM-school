import models.*;
import models.Point;
import models.Polygon;
import models.Shape;
import rasterizers.*;
import rasters.Raster;
import rasters.RasterBufferedImage;
import ui.Toolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class App {

    private final JPanel panel;
    private final Raster raster;
    private final Toolbar toolbar;

    private TrivialRasterizer lineRasterizer;
    private ShapeCanvasRasterizer canvasRasterizer;
    private ShapeCanvas shapeCanvas;

    // Current drawing state
    private ToolMode currentTool = ToolMode.LINE;
    private Color currentColor = Color.GREEN;
    private int currentThickness = 1;
    private LineStyle currentLineStyle = LineStyle.SOLID;

    // Mouse interaction state
    private Point pressPoint;
    private boolean isDragging = false;
    private boolean dottedMode = false;

    // Polygon drawing state
    private List<Point> polygonVertices = new ArrayList<>();

    // Selection state
    private Shape selectedShape;
    private int dragHandleIndex = -1;
    private Point dragOffset;

    // P-mode (legacy polygon via P key)
    private boolean pMode = false;
    private boolean pWasDownAtLastDraw = false;
    private Point lastEnd;

    private static final int HANDLE_SIZE = 5;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Delta : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                raster.repaint(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        toolbar = new Toolbar();
        toolbar.setToolChangeListener(t -> {
            currentTool = t;
            selectedShape = null;
            polygonVertices.clear();
            redraw();
        });
        toolbar.setColorChangeListener(c -> currentColor = c);
        toolbar.setThicknessChangeListener(t -> currentThickness = t);
        toolbar.setLineStyleChangeListener(s -> currentLineStyle = s);

        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        lineRasterizer = new TrivialRasterizer(raster, Color.CYAN);
        shapeCanvas = new ShapeCanvas();
        canvasRasterizer = new ShapeCanvasRasterizer(raster, lineRasterizer);

        setupMouseHandlers();
        setupKeyHandlers();
    }

    public void start() {
        raster.setClearColor(0x000000);
        raster.clear();
        panel.repaint();
    }

    // REDRAW

    private void redraw() {
        raster.clear();
        canvasRasterizer.rasterize(shapeCanvas);
        drawSelection();
        panel.repaint();
    }

    private void drawSelection() {
        if (selectedShape == null) return;

        int[] bb = selectedShape.getBoundingBox();
        int pad = 3;
        // dashed bounding box
        Line top = new Line(new Point(bb[0] - pad, bb[1] - pad), new Point(bb[2] + pad, bb[1] - pad), Color.WHITE, 1, LineStyle.DASHED);
        Line right = new Line(new Point(bb[2] + pad, bb[1] - pad), new Point(bb[2] + pad, bb[3] + pad), Color.WHITE, 1, LineStyle.DASHED);
        Line bottom = new Line(new Point(bb[2] + pad, bb[3] + pad), new Point(bb[0] - pad, bb[3] + pad), Color.WHITE, 1, LineStyle.DASHED);
        Line left = new Line(new Point(bb[0] - pad, bb[3] + pad), new Point(bb[0] - pad, bb[1] - pad), Color.WHITE, 1, LineStyle.DASHED);
        lineRasterizer.rasterize(top);
        lineRasterizer.rasterize(right);
        lineRasterizer.rasterize(bottom);
        lineRasterizer.rasterize(left);

        // handles
        List<Point> handles = selectedShape.getHandles();
        for (Point h : handles) {
            drawHandle(h);
        }
    }

    private void drawHandle(Point p) {
        int rgb = Color.WHITE.getRGB();
        for (int dy = -HANDLE_SIZE / 2; dy <= HANDLE_SIZE / 2; dy++) {
            for (int dx = -HANDLE_SIZE / 2; dx <= HANDLE_SIZE / 2; dx++) {
                int px = p.getX() + dx;
                int py = p.getY() + dy;
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, rgb);
                }
            }
        }
    }

    // MOUSE HANDLERS

    private void setupMouseHandlers() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();
                Point p = new Point(e.getX(), e.getY());
                isDragging = false;

                switch (currentTool) {
                    case SELECT -> handleSelectPress(p);
                    case POLYGON -> {} // clicks handled in mouseReleased
                    default -> pressPoint = p;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point releaseRaw = new Point(e.getX(), e.getY());

                switch (currentTool) {
                    case LINE -> handleLineRelease(releaseRaw, e.isShiftDown());
                    case CIRCLE -> handleCircleRelease(releaseRaw);
                    case RECTANGLE -> handleRectRelease(releaseRaw, false);
                    case SQUARE -> handleRectRelease(releaseRaw, true);
                    case POLYGON -> handlePolygonClick(releaseRaw, e.getClickCount());
                    case SELECT -> handleSelectRelease();
                    case ERASER -> handleEraserClick(releaseRaw);
                    case FILL -> handleFillClick(releaseRaw);
                }

                pressPoint = null;
                isDragging = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                isDragging = true;
                Point dragRaw = new Point(e.getX(), e.getY());

                switch (currentTool) {
                    case LINE -> previewLine(dragRaw, e.isShiftDown());
                    case CIRCLE -> previewCircle(dragRaw);
                    case RECTANGLE -> previewRect(dragRaw, false);
                    case SQUARE -> previewRect(dragRaw, true);
                    case SELECT -> handleSelectDrag(dragRaw);
                    default -> {}
                }
            }
        };

        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
    }

    // LINE

    private void handleLineRelease(Point release, boolean shift) {
        if (pressPoint == null) return;
        Point target = shift ? snapToNearestAngle(pressPoint, release) : release;
        LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
        Line line = new Line(pressPoint, target, currentColor, currentThickness, style);
        shapeCanvas.addShape(line);
        redraw();
    }

    private void previewLine(Point drag, boolean shift) {
        if (pressPoint == null) return;
        Point target = shift ? snapToNearestAngle(pressPoint, drag) : drag;
        LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
        Line preview = new Line(pressPoint, target, Color.RED, currentThickness, style);

        raster.clear();
        canvasRasterizer.rasterize(shapeCanvas);
        lineRasterizer.rasterize(preview);
        panel.repaint();
    }

    // CIRCLE

    private void handleCircleRelease(Point release) {
        if (pressPoint == null) return;
        int radius = (int) Math.hypot(release.getX() - pressPoint.getX(), release.getY() - pressPoint.getY());
        LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
        Circle circle = new Circle(pressPoint, radius, currentColor, currentThickness, style);
        shapeCanvas.addShape(circle);
        pressPoint = null; // don't reuse center point
        redraw();
    }

    private void previewCircle(Point drag) {
        if (pressPoint == null) return;
        int radius = (int) Math.hypot(drag.getX() - pressPoint.getX(), drag.getY() - pressPoint.getY());
        LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
        Circle preview = new Circle(new Point(pressPoint.getX(), pressPoint.getY()), radius, Color.RED, currentThickness, style);

        raster.clear();
        canvasRasterizer.rasterize(shapeCanvas);
        canvasRasterizer.getCircleRasterizer().rasterize(preview);
        panel.repaint();
    }

    // RECTANGLE / SQUARE

    private void handleRectRelease(Point release, boolean square) {
        if (pressPoint == null) return;
        RectangleShape rect = createRect(pressPoint, release, square, currentColor);
        shapeCanvas.addShape(rect);
        redraw();
    }

    private void previewRect(Point drag, boolean square) {
        if (pressPoint == null) return;
        RectangleShape preview = createRect(pressPoint, drag, square, Color.RED);

        raster.clear();
        canvasRasterizer.rasterize(shapeCanvas);
        new RectangleRasterizer(lineRasterizer).rasterize(preview);
        panel.repaint();
    }

    private RectangleShape createRect(Point p1, Point p2, boolean square, Color color) {
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();
        int w = Math.abs(dx);
        int h = Math.abs(dy);
        if (square) {
            int side = Math.max(w, h);
            w = side;
            h = side;
        }
        int x = dx >= 0 ? p1.getX() : p1.getX() - w;
        int y = dy >= 0 ? p1.getY() : p1.getY() - h;
        LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
        return new RectangleShape(new Point(x, y), w, h, square, color, currentThickness, style);
    }

    // POLYGON

    private void handlePolygonClick(Point click, int clickCount) {
        if (clickCount >= 2 && polygonVertices.size() >= 2) {
            // Close polygon
            polygonVertices.add(click);
            LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
            Polygon polygon = new Polygon(polygonVertices, currentColor, currentThickness, style);
            shapeCanvas.addShape(polygon);
            polygonVertices.clear();
            redraw();
        } else {
            polygonVertices.add(click);
            // Preview current polygon edges
            raster.clear();
            canvasRasterizer.rasterize(shapeCanvas);
            if (polygonVertices.size() > 1) {
                LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
                for (int i = 0; i < polygonVertices.size() - 1; i++) {
                    Point a = polygonVertices.get(i);
                    Point b = polygonVertices.get(i + 1);
                    lineRasterizer.rasterizeLine(a.getX(), a.getY(), b.getX(), b.getY(),
                            Color.RED.getRGB(), style, currentThickness);
                }
            }
            panel.repaint();
        }
    }

    // SELECT

    private void handleSelectPress(Point p) {
        // Check if clicking on a handle of selected shape
        if (selectedShape != null) {
            List<Point> handles = selectedShape.getHandles();
            for (int i = 0; i < handles.size(); i++) {
                Point h = handles.get(i);
                if (Math.abs(h.getX() - p.getX()) <= HANDLE_SIZE && Math.abs(h.getY() - p.getY()) <= HANDLE_SIZE) {
                    dragHandleIndex = i;
                    pressPoint = p;
                    return;
                }
            }
        }

        // Check if clicking on a shape
        Shape hit = shapeCanvas.shapeAt(p);
        if (hit != null) {
            selectedShape = hit;
            dragHandleIndex = -1;
            pressPoint = p;
            dragOffset = p;
        } else {
            selectedShape = null;
            dragHandleIndex = -1;
        }
        redraw();
    }

    private void handleSelectDrag(Point drag) {
        if (selectedShape == null || pressPoint == null) return;

        if (dragHandleIndex >= 0) {
            selectedShape.moveHandle(dragHandleIndex, drag);
        } else if (dragOffset != null) {
            int dx = drag.getX() - dragOffset.getX();
            int dy = drag.getY() - dragOffset.getY();
            selectedShape.moveBy(dx, dy);
            dragOffset = drag;
        }
        redraw();
    }

    private void handleSelectRelease() {
        dragHandleIndex = -1;
        dragOffset = null;
    }

    // ERASER

    private void handleEraserClick(Point p) {
        Shape hit = shapeCanvas.shapeAt(p);
        if (hit != null) {
            shapeCanvas.removeShape(hit);
            if (hit == selectedShape) selectedShape = null;
            redraw();
        }
    }

    // FILL

    private void handleFillClick(Point p) {
        FillShape fill = new FillShape(p.getX(), p.getY(), currentColor);
        shapeCanvas.addShape(fill);
        redraw();
    }

    // KEYBOARD

    private void setupKeyHandlers() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedMode = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    shapeCanvas.clear();
                    selectedShape = null;
                    polygonVertices.clear();
                    raster.clear();
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (selectedShape != null) {
                        shapeCanvas.removeShape(selectedShape);
                        selectedShape = null;
                        redraw();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Close polygon if in polygon mode
                    if (currentTool == ToolMode.POLYGON && polygonVertices.size() >= 3) {
                        LineStyle style = dottedMode ? LineStyle.DOTTED : currentLineStyle;
                        Polygon polygon = new Polygon(polygonVertices, currentColor, currentThickness, style);
                        shapeCanvas.addShape(polygon);
                        polygonVertices.clear();
                        redraw();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedMode = false;
                }
            }
        });
    }

    // UTILITY

    private Point snapToNearestAngle(Point origin, Point target) {
        int ox = origin.getX(), oy = origin.getY();
        int tx = target.getX(), ty = target.getY();
        double dx = tx - ox, dy = ty - oy;
        double len = Math.hypot(dx, dy);
        if (len == 0) return target;

        double angle = Math.atan2(dy, dx);
        double step = Math.PI / 4.0;
        double k = Math.round(angle / step);
        double snapped = k * step;

        int nx = ox + (int) Math.round(len * Math.cos(snapped));
        int ny = oy + (int) Math.round(len * Math.sin(snapped));
        return new Point(nx, ny);
    }
}
