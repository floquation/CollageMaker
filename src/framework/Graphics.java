package framework;

public interface Graphics {
    public static enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }

    public Pixmap rotatePixmap(Pixmap pix, double radian);
    
    public Pixmap newPixmap(String fileName, PixmapFormat format);

    public Pixmap newAbsolutePixmap(String absoluteFileName, PixmapFormat format);
    
    public void clear(int color);

    public void setFontSize(int size);
    public void setFontSize(double size, Camera cam);
    
    //With camera:
    
    public void drawPixel(double x, double y, int color, Camera cam);

    public void drawLine(double x, double y, double x2, double y2, int color, Camera cam);

    public void drawRect(double x, double y, double width, double height, int color, Camera cam);
    public void drawRect(double x, double y, double width, double height, int c_r, int c_g, int c_b, int c_alpha, Camera cam);
    public void drawRectRealSize(double x, double y, double width, double height, int c_r, int c_g, int c_b, int c_alpha, Camera cam);
    public void drawRect2(double x0, double y0, double x1, double y1, int color, Camera cam);
    
    public void drawPixmap(Pixmap pixmap, double x, double y, int srcX, int srcY,
            int srcWidth, int srcHeight, Camera cam);

    public void drawPixmap(Pixmap pixmap, double x, double y, double dstWidth, double dstHeight, Camera cam);
    
    public void drawPixmap(Pixmap pixmap, double x, double y, int srcX, int srcY,
            int srcWidth, int srcHeight, double w, double h, Camera cam);
    
    public void drawPixmap(Pixmap pixmap, double x, double y, Camera cam);
    
    
    //Without camera:
    
    public void drawPixel(int x, int y, int color);

    public void drawLine(int x, int y, int x2, int y2, int color);

    public void drawRect(int x, int y, int width, int height, int color);
    public void drawRect(int x, int y, int width, int height, int c_r, int c_g, int c_b, int c_alpha);
    public void drawRect2(int x0, int y0, int x1, int y1, int color);
    
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
            int srcWidth, int srcHeight);

    public void drawPixmap(Pixmap pixmap, int x, int y, int dstWidth, int dstHeight);
    
    public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
            int srcWidth, int srcHeight, int w, int h);
    
    public void drawPixmap(Pixmap pixmap, int x, int y);

    
    //Text:
    
    public void drawText(String text, int x, int y, int color);
    public void drawText(String text, double x, double y, int color, Camera cam);
    public void drawText(String text, int x, int y, int maxW, int maxH, int color);
    public void drawText(String text, int x, int y, int maxW, int maxH, int color, Camera cam);
    
    
    
    
    public void setClipBounds(int x1, int y1, int x2, int y2);
    
    public void resetClipBounds();
    
    
    public int getWidth();

    public int getHeight();
}
