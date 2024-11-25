package pictureWizard;

import java.awt.image.BufferedImage;

public class LinearContrastEnhancement {

    public static BufferedImage enhance(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage enhancedImage = new BufferedImage(width, height, image.getType());

        // Определяем минимальное и максимальное значения яркости
        int min = 255, max = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int brightness = (rgb >> 16) & 0xff; // берем только красный канал
                min = Math.min(min, brightness);
                max = Math.max(max, brightness);
            }
        }

        // Линейное контрастирование
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = ((rgb >> 16) & 0xff);
                int g = ((rgb >> 8) & 0xff);
                int b = (rgb & 0xff);

                // Применяем линейное контрастирование
                r = (int) ((r - min) * 255.0 / (max - min));
                g = (int) ((g - min) * 255.0 / (max - min));
                b = (int) ((b - min) * 255.0 / (max - min));

                // Ограничиваем значения от 0 до 255
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                // Устанавливаем новый RGB
                enhancedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }

        return enhancedImage;
    }
}