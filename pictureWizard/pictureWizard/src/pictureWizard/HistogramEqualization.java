package pictureWizard;

import java.awt.image.BufferedImage;

public class HistogramEqualization {

    public static BufferedImage equalize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram = new int[256];
        BufferedImage equalizedImage = new BufferedImage(width, height, image.getType());

        // Вычисляем гистограмму
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int brightness = (rgb >> 16) & 0xff; // берем только красный канал
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
                int rgb = image.getRGB(x, y);
                int r = ((rgb >> 16) & 0xff);
                int g = ((rgb >> 8) & 0xff);
                int b = (rgb & 0xff);

                // Выравниваем только красный канал
                r = (int) ((histogram[r] / (double) (width * height)) * 255);
                g = (int) ((histogram[g] / (double) (width * height)) * 255);
                b = (int) ((histogram[b] / (double) (width * height)) * 255);

                // Ограничиваем значения от 0 до 255
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                // Устанавливаем новый RGB
                equalizedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }

        return equalizedImage;
    }
}