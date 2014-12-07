package framework.impl;

import java.awt.Graphics;
import java.awt.Image;

import framework.Screen;

public class PCFastRenderView implements Runnable {
    private static final int TARGET_FPS = 35;
    private static final boolean SHOW_FPS = true;
    
    PCGame game;
    Graphics g;
    Image framebuffer;
    Thread renderThread = null;
    volatile boolean running = false;
    private int frameWidth;
    private int frameHeight;
    public float FPS = TARGET_FPS;
    
    public PCFastRenderView(PCGame game, Image frameBuffer, Graphics g) {
        this.game = game;
        this.framebuffer = frameBuffer;
        this.g = g;
        frameWidth = game.frame.getWidth();
        frameHeight = game.frame.getHeight();
    }

    public void resume() { 
        running = true;
        renderThread = new Thread(this);
        renderThread.start();         
    }      

    public void run() {
        long startTime = System.nanoTime();
        Screen screen;
        float elapsedTime;
        while(running) {   
            
            float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
            startTime = System.nanoTime();
            
            //Update the game
            screen = game.getCurrentScreen();;
            screen.update(deltaTime);
            screen.present(deltaTime);
            
            //Buffer2Screen
            if(SHOW_FPS){
            	FPS = 0.9f*FPS + 0.1f/deltaTime;
//            	game.getGraphics().drawText(String.format("%.1f", FPS), game.getGraphics().getWidth()-40,20, 0xffaaaa00);
            }
            g.drawImage(framebuffer, 0, 0, frameWidth, frameHeight, null);
            
            
            //Ensure the TARGET_FPS at most
            elapsedTime = (System.nanoTime()-startTime) / 1000000000.0f;
//          System.out.println("FPS = " + FPS + "; elapsedTime = " + elapsedTime + "; deltaTime = " + deltaTime);
            try {
//            	float sleepTime = (Math.max(0, 1000*(1f/TARGET_FPS-elapsedTime)));
//            	Thread.sleep((long)sleepTime, (int)(1000000*(sleepTime - (long)sleepTime)) );
//            	System.out.println("sleepTime = " + sleepTime + "; Sleep_ms = " + (long)sleepTime + "; Sleep_ns = " + (int)(1000000*(sleepTime - (long)sleepTime)) );
				Thread.sleep( (long)(Math.max(0, 1+1000*(1f/TARGET_FPS-elapsedTime))) );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

    public void pause() {                        
        running = false;                        
        while(true) {
            try {
                renderThread.join();
                return;
            } catch (InterruptedException e) {
                // retry
            }
        }
    }        
}
