package otsech;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClippingVisualization extends JPanel {

    private int xmin, ymin, xmax, ymax; // Координаты окна отсечения
    private final List<int[]> segments = new ArrayList<>();
    private final List<int[]> clippedSegments = new ArrayList<>();
    private final List<int[]> polygon = new ArrayList<>();
    private final List<int[]> clippedPolygon = new ArrayList<>();

    public ClippingVisualization(String filename) throws IOException {
        // Чтение данных из файла
        readDataFromFile(filename);

        // Отсечение отрезков
        for (int[] segment : segments) {
            int[] clipped = cohenSutherlandClip(segment[0], segment[1], segment[2], segment[3]);
            if (clipped != null) {
                clippedSegments.add(clipped);
            }
        }

        // Отсечение многоугольника
        clippedPolygon.addAll(sutherlandHodgmanClip(polygon));
    }

    private void readDataFromFile(String filename) throws IOException {
        try (Scanner scanner = new Scanner(new File(filename))) {
            int n = scanner.nextInt(); // Читаем количество отрезков
            for (int i = 0; i < n; i++) {
                int x1 = scanner.nextInt();
                int y1 = scanner.nextInt();
                int x2 = scanner.nextInt();
                int y2 = scanner.nextInt();
                segments.add(new int[]{x1, y1, x2, y2});
            }
            xmin = scanner.nextInt();
            ymin = scanner.nextInt();
            xmax = scanner.nextInt();
            ymax = scanner.nextInt();

            // Пример выпуклого многоугольника (можно заменить на данные из файла)
            polygon.add(new int[]{50, 50});
            polygon.add(new int[]{300, 50});
            polygon.add(new int[]{300, 300});
            polygon.add(new int[]{50, 300});
        }
    }

    private int[] cohenSutherlandClip(int x1, int y1, int x2, int y2) {
        int code1 = computeRegionCode(x1, y1);
        int code2 = computeRegionCode(x2, y2);
        boolean accept = false;

        while (true) {
            if ((code1 | code2) == 0) {
                accept = true;
                break;
            } else if ((code1 & code2) != 0) {
                break;
            } else {
                int codeOut = (code1 != 0) ? code1 : code2;
                int x = 0, y = 0;
                if ((codeOut & 8) != 0) {
                    x = x1 + (x2 - x1) * (ymax - y1) / (y2 - y1);
                    y = ymax;
                } else if ((codeOut & 4) != 0) {
                    x = x1 + (x2 - x1) * (ymin - y1) / (y2 - y1);
                    y = ymin;
                } else if ((codeOut & 2) != 0) {
                    y = y1 + (y2 - y1) * (xmax - x1) / (x2 - x1);
                    x = xmax;
                } else if ((codeOut & 1) != 0) {
                    y = y1 + (y2 - y1) * (xmin - x1) / (x2 - x1);
                    x = xmin;
                }
                if (codeOut == code1) {
                    x1 = x;
                    y1 = y;
                    code1 = computeRegionCode(x1, y1);
                } else {
                    x2 = x;
                    y2 = y;
                    code2 = computeRegionCode(x2, y2);
                }
            }
        }
        return accept ? new int[]{x1, y1, x2, y2} : null;
    }

    private int computeRegionCode(int x, int y) {
        int code = 0;
        if (x < xmin) code |= 1;
        else if (x > xmax) code |= 2;
        if (y < ymin) code |= 4;
        else if (y > ymax) code |= 8;
        return code;
    }

    private List<int[]> sutherlandHodgmanClip(List<int[]> polygon) {
        List<int[]> clipped = new ArrayList<>(polygon);
        int[][] edges = {
                {xmin, ymin, xmax, ymin},
                {xmax, ymin, xmax, ymax},
                {xmax, ymax, xmin, ymax},
                {xmin, ymax, xmin, ymin}
        };
        for (int[] edge : edges) {
            List<int[]> input = new ArrayList<>(clipped);
            clipped.clear();
            for (int i = 0; i < input.size(); i++) {
                int[] current = input.get(i);
                int[] prev = input.get((i - 1 + input.size()) % input.size());
                boolean currentInside = isInside(current, edge);
                boolean prevInside = isInside(prev, edge);
                if (currentInside && prevInside) {
                    clipped.add(current);
                } else if (currentInside) {
                    clipped.add(intersect(prev, current, edge));
                    clipped.add(current);
                } else if (prevInside) {
                    clipped.add(intersect(prev, current, edge));
                }
            }
        }
        return clipped;
    }

    private boolean isInside(int[] point, int[] edge) {
        int x = point[0], y = point[1];
        int x1 = edge[0], y1 = edge[1], x2 = edge[2], y2 = edge[3];
        return (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1) >= 0;
    }

    private int[] intersect(int[] p1, int[] p2, int[] edge) {
        int x1 = p1[0], y1 = p1[1], x2 = p2[0], y2 = p2[1];
        int x3 = edge[0], y3 = edge[1], x4 = edge[2], y4 = edge[3];
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denom;
        double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denom;
        return new int[]{(int) px, (int) py};
    }

    private void drawArrowhead(Graphics2D g2d, int x, int y, int arrowLength, int arrowWidth) {
        double angle = Math.toRadians(135);
        int x1 = x - (int)(arrowLength * Math.cos(angle));
        int y1 = y - (int)(arrowLength * Math.sin(angle));
        int x2 = x - (int)(arrowLength * Math.cos(angle + Math.toRadians(90)));
        int y2 = y - (int)(arrowLength * Math.sin(angle + Math.toRadians(90)));


        int x3 = x - (int)(arrowLength * Math.cos(angle - Math.toRadians(90)));
        int y3 = y - (int)(arrowLength * Math.sin(angle - Math.toRadians(90)));

        int[] xPoints = {x, x1, x3};
        int[] yPoints = {y, y1, y3};
        g2d.fillPolygon(xPoints, yPoints, 3);

    }    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(0, centerY, width, centerY); // X-axis
        g2d.drawLine(centerX, 0, centerX, height); // Y-axis

        drawArrowhead(g2d, width, centerY, 10, 5); //X-axis arrow
        drawArrowhead(g2d, centerX, height, 5, 10); //Y-axis arrow

        // Numbering for X-axis
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        int numXLabels = 10; // Number of labels
        int xLabelSpacing = width / numXLabels;
        for (int i = 1; i <= numXLabels; i++) {
          int x = i * xLabelSpacing;
          String label = String.valueOf(i- (numXLabels/2)); // Adjust starting number
          int labelWidth = g2d.getFontMetrics().stringWidth(label);
          g2d.drawString(label, x - labelWidth / 2, centerY + 15); //Position below X-axis

        }

        // Numbering for Y-axis
        int numYLabels = 10;
        int yLabelSpacing = height / numYLabels;

        for (int i = 1; i <= numYLabels; i++) {
            int y = i * yLabelSpacing;
            String label = String.valueOf( (numYLabels/2) - i); // Adjust starting number
            int labelWidth = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, centerX - labelWidth - 5, y + 5); // Position to the left of Y-axis
        }
        // Масштабирование
        int scale = 1;

        // Окно отсечения
        g.setColor(Color.RED);
        g.drawRect(xmin * scale, ymin * scale, (xmax - xmin) * scale, (ymax - ymin) * scale);

        // Исходные отрезки
        g.setColor(Color.BLUE);
        for (int[] segment : segments) {
            g.drawLine(segment[0] * scale, segment[1] * scale, segment[2] * scale, segment[3] * scale);
        }

        // Отсечённые отрезки
        g.setColor(Color.GREEN);
        for (int[] segment : clippedSegments) {
            g.drawLine(segment[0] * scale, segment[1] * scale, segment[2] * scale, segment[3] * scale);
        }

        // Исходный многоугольник
        g.setColor(Color.ORANGE);
        for (int i = 0; i < polygon.size(); i++) {
            int[] p1 = polygon.get(i);
            int[] p2 = polygon.get((i + 1) % polygon.size());
            g.drawLine(p1[0] * scale, p1[1] * scale, p2[0] * scale, p2[1] * scale);
        }

        // Отсечённый многоугольник
        g.setColor(Color.MAGENTA);
        for (int i = 0; i < clippedPolygon.size(); i++) {
            int[] p1 = clippedPolygon.get(i);
            int[] p2 = clippedPolygon.get((i + 1) % clippedPolygon.size());
            g.drawLine(p1[0] * scale, p1[1] * scale, p2[0] * scale, p2[1] * scale);
        }
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Отсечение отрезков и многоугольников");
            ClippingVisualization panel = new ClippingVisualization("input.txt");
            frame.add(panel);
            frame.setSize(800, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}