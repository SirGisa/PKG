package dots;

public class ImageInfo {
    private String fileName;
    private int width;
    private int height;
    private int resolutionX;
    private int resolutionY;
    private int colorDepth;
    private String compressionType;

    public ImageInfo(String fileName, int width, int height, int resolutionX, 
                     int resolutionY, int colorDepth, String compressionType) {
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.colorDepth = colorDepth;
        this.compressionType = compressionType;
    }

    // Getters
    public String getFileName() { return fileName; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getResolutionX() { return resolutionX; }
    public int getResolutionY() { return resolutionY; }
    public int getColorDepth() { return colorDepth; }
    public String getCompressionType() { return compressionType; }

    @Override
    public String toString() {
        return "ImageInfo{" +
               "fileName='" + fileName + '\'' +
               ", width=" + width +
               ", height=" + height +
               ", resolutionX=" + resolutionX +
               ", resolutionY=" + resolutionY +
               ", colorDepth=" + colorDepth +
               ", compressionType='" + compressionType + '\'' +
               '}';
    }
}