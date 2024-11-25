package dots;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class CustomImageReader {

    public static Optional<ImageInfo> readImageInfo(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                String fileName = file.getName();
                String compressionType = "Unknown"; // Default value
                ImageReader reader = ImageIO.getImageReaders(ImageIO.createImageInputStream(file)).next();
                reader.setInput(new FileImageInputStream(file));
                int resolutionX = reader.getWidth(0);
                int resolutionY = reader.getHeight(0);
                
                ImageInfo imageInfo = new ImageInfo(
                        fileName,
                        image.getWidth(),
                        image.getHeight(),
                        resolutionX,
                        resolutionY,
                        image.getColorModel().getPixelSize(),
                        compressionType
                );
                return Optional.of(imageInfo);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Improved error handling
        }
        return Optional.empty();
    }
}