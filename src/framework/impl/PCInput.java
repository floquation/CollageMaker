package framework.impl;

import java.awt.Canvas;
import java.util.List;

import framework.Input;


public class PCInput implements Input {    
    KeyboardHandler keyHandler;
    MouseHandler touchHandler;

    public PCInput(Canvas canvas, float scaleX, float scaleY) {
        keyHandler = new KeyboardHandler(canvas);
        touchHandler = new MouseHandler(canvas, scaleX, scaleY);        
    }

    public boolean isKeyPressed(int keyCode) {
        return keyHandler.isKeyPressed(keyCode);
    }

    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    public int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }

    public List<TouchEvent> getTouchEvents() {
        return touchHandler.getTouchEvents();
    }
    
    public List<KeyEvent> getKeyEvents() {
        return keyHandler.getKeyEvents();
    }

}
