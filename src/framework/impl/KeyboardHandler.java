package framework.impl;

import java.awt.Canvas;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import framework.Input.KeyEvent;
import framework.Pool;
import framework.Pool.PoolObjectFactory;

public class KeyboardHandler implements KeyListener {
    boolean[] pressedKeys = new boolean[128];
    Pool<KeyEvent> keyEventPool;
    List<KeyEvent> keyEventsBuffer = new ArrayList<KeyEvent>();    
    List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
    
    public KeyboardHandler(Canvas canvas) {
        PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
            public KeyEvent createObject() {
                return new KeyEvent();
            }
        };
        keyEventPool = new Pool<KeyEvent>(factory, 100);
        canvas.addKeyListener(this);
        canvas.requestFocus();
    }

    public boolean isKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode > 127)
            return false;
        return pressedKeys[keyCode];
    }

    public List<KeyEvent> getKeyEvents() {
        synchronized (this) {
            int len = keyEvents.size();
            for (int i = 0; i < len; i++) {
                keyEventPool.free(keyEvents.get(i));
            }
            keyEvents.clear();
            keyEvents.addAll(keyEventsBuffer);
            keyEventsBuffer.clear();
            return keyEvents;
        }
    }

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
        synchronized (this) {
            KeyEvent keyEvent = keyEventPool.newObject();
            keyEvent.keyCode = e.getKeyCode();
            keyEvent.keyChar = (char) e.getKeyChar();
            if(keyEvent.keyCode > 0 && keyEvent.keyCode < 127)
                pressedKeys[keyEvent.keyCode] = true;
            keyEventsBuffer.add(keyEvent);
        }	
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		synchronized (this) {
            KeyEvent keyEvent = keyEventPool.newObject();
            keyEvent.keyCode = e.getKeyCode();
            keyEvent.keyChar = (char) e.getKeyChar();
            if(keyEvent.keyCode > 0 && keyEvent.keyCode < 127)
                pressedKeys[keyEvent.keyCode] = false;
            keyEventsBuffer.add(keyEvent);
        }			
	}
}
