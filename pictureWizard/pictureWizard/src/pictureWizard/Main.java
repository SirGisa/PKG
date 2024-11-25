package pictureWizard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Main {

    private static BufferedImage originalImage; // Объявляем поле класса

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Image Contrast Enhancement");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            JButton loadButton = new JButton("Загрузить изображение");
            JButton linearContrastButton = new JButton("Линейное контрастирование");
            JButton histogramEqualizationButton = new JButton("Выравнивание гистограммы RGB");
            JButton hsvEqualizationButton = new JButton("Выравнивание гистограммы HSV");
            JPanel buttonPanel = new JPanel();
            
            buttonPanel.add(loadButton);
            buttonPanel.add(linearContrastButton);
            buttonPanel.add(histogramEqualizationButton);
            buttonPanel.add(hsvEqualizationButton);
            frame.add(buttonPanel, BorderLayout.NORTH);
            JLabel imageLabel = new JLabel();
            frame.add(imageLabel, BorderLayout.CENTER);

            loadButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(file);
                        ImageIcon imageIcon = new ImageIcon(originalImage);
                        imageLabel.setIcon(imageIcon);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            linearContrastButton.addActionListener(e -> {
                if (originalImage != null) {
                    BufferedImage enhancedImage = LinearContrastEnhancement.enhance(originalImage);
                    imageLabel.setIcon(new ImageIcon(enhancedImage));
                }
            });

            histogramEqualizationButton.addActionListener(e -> {
                if (originalImage != null) {
                    BufferedImage equalizedImage = HistogramEqualization.equalize(originalImage);
                    imageLabel.setIcon(new ImageIcon(equalizedImage));
                }
            });

            hsvEqualizationButton.addActionListener(e -> {
                if (originalImage != null) {
                    BufferedImage hsvEqualizedImage = HSVHistogramEqualization.equalize(originalImage);
                    imageLabel.setIcon(new ImageIcon(hsvEqualizedImage));
                }
            });

            frame.setVisible(true);
        });
    }
}