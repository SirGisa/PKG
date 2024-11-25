package pictureWizard;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HSVHistogramEqualization {

    public static BufferedImage equalize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram = new int[256];
        BufferedImage equalizedImage = new BufferedImage(width, height, image.getType());

        // Вычисляем гистограмму яркости
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                int brightness = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                histogram[brightness]++;
            }
        }

        // Нормализуем гистограмму
        for (int i = 1; i < histogram.length; i++) {
            histogram[i] += histogram[i - 1]; // кумулятивная гистограмма
        }

        // Применяем выравнивание гистограммы
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                // Вычисляем яркость
                int brightness = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int newBrightness = (int) ((histogram[brightness] / (double) (width * height)) * 255);

                // Устанавливаем новый цвет с эквализированной яркостью
                equalizedImage.setRGB(x, y, new Color(newBrightness, newBrightness, newBrightness).getRGB());
            }
        }

        return equalizedImage;
    }
}
