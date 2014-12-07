package framework.impl;

import java.awt.Canvas;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import framework.Input.TouchEvent;
import framework.Pool;
import framework.Pool.PoolObjectFactory;



public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener{
	boolean isTouched;
    int touchX;
    int touchY;
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    float scaleX;
    float scaleY;

    public MouseHandler(Canvas canvas, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            public TouchEvent createObject() {
                return new TouchEvent();
            }            
        };
        touchEventPool = new Pool<TouchEvent>(factory, 100);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public boolean isTouchDown(int pointer) {
        synchronized(this) {
            if(pointer == 0)
                return isTouched;
            else
                return false;
        }
    }

    public int getTouchX(int pointer) {
        synchronized(this) {
            return touchX;
        }
    }

    public int getTouchY(int pointer) {
        synchronized(this) {
            return touchY;
        }
    }

    public List<TouchEvent> getTouchEvents() {
        synchronized(this) {     
            int len = touchEvents.size();
            for( int i = 0; i < len; i++ )
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            touchEvent.type = TouchEvent.TOUCH_DOWN;
            isTouched = true;
            
            touchEvent.x = touchX = (int)(e.getX() * scaleX);
            touchEvent.y = touchY = (int)(e.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);  
            
            touchEvent.cntrlDown = e.isControlDown(); 
            touchEvent.altDown = e.isAltDown();
            touchEvent.shiftDown = e.isShiftDown();       
            
            touchEvent.button = e.getButton();
            
            
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            touchEvent.type = TouchEvent.TOUCH_UP;
            isTouched = false;
            
            touchEvent.x = touchX = (int)(e.getX() * scaleX);
            touchEvent.y = touchY = (int)(e.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);  
            
            touchEvent.cntrlDown = e.isControlDown(); 
            touchEvent.altDown = e.isAltDown();
            touchEvent.shiftDown = e.isShiftDown();  
            
            touchEvent.button = e.getButton(); 
                        
            
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//TODO
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            touchEvent.type = TouchEvent.TOUCH_DRAGGED;
            isTouched = true;
            
            touchEvent.x = touchX = (int)(e.getX() * scaleX);
            touchEvent.y = touchY = (int)(e.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);  
            
            touchEvent.cntrlDown = e.isControlDown(); 
            touchEvent.altDown = e.isAltDown();
            touchEvent.shiftDown = e.isShiftDown();
            
            touchEvent.button = e.getButton();           
            
            
        }
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
