package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class RasterizationDemo extends JFrame {
    private BufferedImage canvas;
    private JPanel canvasPanel;
    private JTextField x1Field, y1Field, x2Field, y2Field, rField;
    private double scale = 1.0;
    private int offsetX = 0, offsetY = 0;
    private int dragStartX, dragStartY;

    public RasterizationDemo() {
        setTitle("Rasterization Algorithms");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        canvas = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        
        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(offsetX, offsetY);
                g2d.scale(scale, scale);
                g2d.drawImage(canvas, 0, 0, this);
            }
        };

        add(new JScrollPane(canvasPanel), BorderLayout.CENTER);
        clearCanvas();
        drawGrid();
        drawAxes();
        setupUI();
        setupZoomAndPanControls();
    }

    private void clearCanvas() {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawGrid();
        drawAxes();
    }

    private void drawPixel(int x, int y, Color color) {
        if (x >= 0 && x < canvas.getWidth() && y >= 0 && y < canvas.getHeight()) {
            canvas.setRGB(x, y, color.getRGB());
        }
    }

    private void drawLineDDA(int x1, int y1, int x2, int y2, Color color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        float xIncrement = dx / (float) steps;
        float yIncrement = dy / (float) steps;
        float x = x1;
        float y = y1;
        for (int i = 0; i <= steps; i++) {
            drawPixel(Math.round(x), Math.round(y), color);
            x += xIncrement;
            y += yIncrement;
        }
    }

    private void drawLineBresenham(int x1, int y1, int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            drawPixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawCircleBresenham(int xc, int yc, int r, Color color) {
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;
        while (y >= x) {
            drawPixel(xc + x, yc + y, color);
            drawPixel(xc - x, yc + y, color);
            drawPixel(xc + x, yc - y, color);
            drawPixel(xc - x, yc - y, color);
            drawPixel(xc + y, yc + x, color);
            drawPixel(xc - y, yc + x, color);
            drawPixel(xc + y, yc - x, color);
            drawPixel(xc - y, yc - x, color);
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
        }
    }

    private void drawStepByStep(int x1, int y1, int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            drawPixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawAntiAliasedLine(int x1, int y1, int x2, int y2) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.ORANGE);
        g2d.drawLine(x1, y1, x2, y2);
        g2d.dispose();
    }

    private void drawGrid() {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < canvas.getWidth(); i += 10) {
            g.drawLine(i, 0, i, canvas.getHeight());
            g.drawLine(0, i, canvas.getWidth(), i);
        }
    }

    private void drawAxes() {
        Graphics g = canvas.getGraphics();
        g.setColor(Color.BLACK);
        g.drawLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, canvas.getHeight());
        g.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2);
        
        g.drawString("X", canvas.getWidth() - 10, canvas.getHeight() / 2 - 5);
        g.drawString("Y", canvas.getWidth() / 2 + 5, 10);

        for (int i = 50; i < canvas.getWidth(); i += 50) {
            g.drawString(String.valueOf(i - 250), i, canvas.getHeight() / 2 + 15);
            g.drawString(String.valueOf(250 - i), canvas.getWidth() / 2 + 5, i);
        }
    }

    private void setupUI() {
        JPanel panel = new JPanel();
        x1Field = new JTextField(3);
        y1Field = new JTextField(3);
        x2Field = new JTextField(3);
        y2Field = new JTextField(3);
        rField = new JTextField(3);

        JButton ddaButton = new JButton("Draw Line (DDA)");
        JButton bresenhamLineButton = new JButton("Draw Line (Bresenham)");
        JButton bresenhamCircleButton = new JButton("Draw Circle (Bresenham)");
        JButton stepByStepButton = new JButton("Draw Step-by-Step");
        JButton antiAliasedButton = new JButton("Anti-Aliased Line");
        JButton clearButton = new JButton("Clear");

        ddaButton.addActionListener(e -> drawLine("DDA"));
        bresenhamLineButton.addActionListener(e -> drawLine("Bresenham"));
        bresenhamCircleButton.addActionListener(e -> drawCircle());
        stepByStepButton.addActionListener(e -> drawLine("StepByStep"));
        antiAliasedButton.addActionListener(e -> drawLine("AntiAliased"));
        clearButton.addActionListener(e -> {
            clearCanvas();
            canvasPanel.repaint();
        });

        panel.add(new JLabel("x1:"));
        panel.add(x1Field);
        panel.add(new JLabel("y1:"));
        panel.add(y1Field);
        panel.add(new JLabel("x2:"));
        panel.add(x2Field);
        panel.add(new JLabel("y2:"));
        panel.add(y2Field);
        panel.add(new JLabel("r:"));
        panel.add(rField);

        panel.add(ddaButton);
        panel.add(bresenhamLineButton);
        panel.add(bresenhamCircleButton);
        panel.add(stepByStepButton);
        panel.add(antiAliasedButton);
        panel.add(clearButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void drawLine(String method) {
        try {
            int x1 = Integer.parseInt(x1Field.getText()) + 250;
            int y1 = 250 - Integer.parseInt(y1Field.getText());
            int x2 = Integer.parseInt(x2Field.getText()) + 250;
            int y2 = 250 - Integer.parseInt(y2Field.getText());

            switch (method) {
                case "DDA":
                    drawLineDDA(x1, y1, x2, y2, Color.RED);
                    break;
                case "Bresenham":
                    drawLineBresenham(x1, y1, x2, y2, Color.BLUE);
                    break;
                case "StepByStep":
                    drawStepByStep(x1, y1, x2, y2, Color.MAGENTA);
                    break;
                case "AntiAliased":
                    drawAntiAliasedLine(x1, y1, x2, y2);
                    break;
            }
            canvasPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for coordinates.");
        }
    }

    private void drawCircle() {
        try {
            int xc = Integer.parseInt(x1Field.getText()) + 250;
            int yc = 250 - Integer.parseInt(y1Field.getText());
            int r = Integer.parseInt(rField.getText());

            drawCircleBresenham(xc, yc, r, Color.GREEN);
            canvasPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for coordinates.");
        }
    }

    private void setupZoomAndPanControls() {
        canvasPanel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                scale *= 1.1;
            } else {
                scale /= 1.1;
            }
            canvasPanel.revalidate();
            canvasPanel.repaint();
        });

        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX() - offsetX;
                dragStartY = e.getY() - offsetY;
            }
        });

        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                offsetX = e.getX() - dragStartX;
                offsetY = e.getY() - dragStartY;
                canvasPanel.repaint();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RasterizationDemo demo = new RasterizationDemo();
            demo.setVisible(true);
        });
    }
}