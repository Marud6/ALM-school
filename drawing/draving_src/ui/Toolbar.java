package ui;

import models.LineStyle;
import models.ToolMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class Toolbar extends JPanel {

    private Color currentColor = Color.GREEN;
    private int currentThickness = 1;
    private LineStyle currentLineStyle = LineStyle.SOLID;
    private ToolMode currentTool = ToolMode.LINE;

    private Consumer<ToolMode> toolChangeListener;
    private Consumer<Color> colorChangeListener;
    private Consumer<Integer> thicknessChangeListener;
    private Consumer<LineStyle> lineStyleChangeListener;

    public Toolbar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));

        // Tool buttons
        ButtonGroup toolGroup = new ButtonGroup();
        ToolMode[] tools = ToolMode.values();
        for (ToolMode tool : tools) {
            JToggleButton btn = new JToggleButton(tool.name());
            btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            btn.setMargin(new Insets(2, 4, 2, 4));
            if (tool == ToolMode.LINE) btn.setSelected(true);
            btn.addActionListener(e -> {
                currentTool = tool;
                if (toolChangeListener != null) toolChangeListener.accept(tool);
            });
            toolGroup.add(btn);
            add(btn);
        }

        add(new JSeparator(SwingConstants.VERTICAL));

        // Color palette
        Color[] palette = {Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
                Color.YELLOW, Color.CYAN, Color.MAGENTA};
        for (Color c : palette) {
            JButton colorBtn = new JButton();
            colorBtn.setPreferredSize(new Dimension(20, 20));
            colorBtn.setBackground(c);
            colorBtn.setOpaque(true);
            colorBtn.setBorderPainted(true);
            colorBtn.addActionListener(e -> {
                currentColor = c;
                if (colorChangeListener != null) colorChangeListener.accept(c);
            });
            add(colorBtn);
        }

        JButton customColor = new JButton("...");
        customColor.setFont(new Font("SansSerif", Font.PLAIN, 10));
        customColor.setMargin(new Insets(2, 4, 2, 4));
        customColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose Color", currentColor);
            if (chosen != null) {
                currentColor = chosen;
                if (colorChangeListener != null) colorChangeListener.accept(chosen);
            }
        });
        add(customColor);

        add(new JSeparator(SwingConstants.VERTICAL));

        // Thickness spinner
        add(new JLabel("Thick:"));
        SpinnerNumberModel thickModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner thickSpinner = new JSpinner(thickModel);
        thickSpinner.setPreferredSize(new Dimension(50, 22));
        thickSpinner.addChangeListener(e -> {
            currentThickness = (Integer) thickSpinner.getValue();
            if (thicknessChangeListener != null) thicknessChangeListener.accept(currentThickness);
        });
        add(thickSpinner);

        // Line style combo
        add(new JLabel("Style:"));
        JComboBox<LineStyle> styleCombo = new JComboBox<>(LineStyle.values());
        styleCombo.setPreferredSize(new Dimension(80, 22));
        styleCombo.addActionListener(e -> {
            currentLineStyle = (LineStyle) styleCombo.getSelectedItem();
            if (lineStyleChangeListener != null) lineStyleChangeListener.accept(currentLineStyle);
        });
        add(styleCombo);
    }

    public void setToolChangeListener(Consumer<ToolMode> listener) {
        this.toolChangeListener = listener;
    }

    public void setColorChangeListener(Consumer<Color> listener) {
        this.colorChangeListener = listener;
    }

    public void setThicknessChangeListener(Consumer<Integer> listener) {
        this.thicknessChangeListener = listener;
    }

    public void setLineStyleChangeListener(Consumer<LineStyle> listener) {
        this.lineStyleChangeListener = listener;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public int getCurrentThickness() {
        return currentThickness;
    }

    public LineStyle getCurrentLineStyle() {
        return currentLineStyle;
    }

    public ToolMode getCurrentTool() {
        return currentTool;
    }
}
