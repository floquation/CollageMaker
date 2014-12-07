package framework.impl;

import java.awt.image.BufferedImage;

import framework.Graphics.PixmapFormat;
import framework.Pixmap;


public class PCPixmap implements Pixmap {
    BufferedImage bitmap;
    PixmapFormat format;
    
    public PCPixmap(BufferedImage bitmap, PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public PixmapFormat getFormat() {
        return format;
    }

    public void dispose() {
        bitmap.flush();
        bitmap = null;
        format = null;
    }      
}
