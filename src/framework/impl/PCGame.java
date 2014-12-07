package framework.impl;


import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import framework.Camera;
import framework.FileIO;
import framework.Game;
import framework.Input;
import framework.MyXML;
import framework.Screen;
import globals.GlobalVars;

//XML:
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;


public abstract class PCGame implements Game {
	JFrame frame;
//    PCFastRenderView renderView;
//  Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    public Camera cam;
    
    protected GlobalVars globvar = new GlobalVars();
    
        
    public PCGame(String title){    	
    	frame = new JFrame(title);
    	frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//    	frame.setExtendedState(Frame.MAXIMIZED_BOTH);
//    	frame.setUndecorated(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setResizable(false);
    	    	        
//        renderView = new PCFastRenderView(this, frameBuffer, canvas.getGraphics());
        fileIO = new PCFileIO("/files", "/assets");
        //audio = new AndroidAudio(this);
//        input = new PCInput(canvas, scaleX, scaleY);

		//Prepare the settings
		readSettingsXML();
		
        //Prepare the first screen
        screen = getStartScreen();
        screen.init();
        screen.resume();
        screen.update(0);
//        renderView.resume();

    	frame.setVisible(true);
    }
    
    public GlobalVars getGlobalVars(){
    	return globvar;
    }
    
    protected void readSettingsXML(){};
    
    public void saveSettingsXML(){};
    

    public void setWindowContainer(JPanel container){
    	frame.add(container);
    }
    public void setWindowSize(int width, int height){
    	frame.setSize(width, height);
    }

    public void setWindowPosition(int x, int y){
    	frame.setLocation(x,y);
    }
    public void setWindowCentered(boolean centered){
    	if(centered){
	    	Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
	    	setWindowPosition((screensize.width-frame.getWidth())/2, (screensize.height-frame.getHeight())/2); 
    	}else{
    		setWindowPosition(0,0);
    	}
    }
    
    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

//    public Graphics getGraphics() {
//        return graphics;
//    }

    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("(PCGame.setScreen(Screen))-> Screen must not be null");

        frame.setVisible(false);
        this.screen.pause();
        this.screen.dispose();
        screen.init();
        screen.resume();
        screen.update(0);
        frame.removeAll();
        this.screen = screen;
        frame.setVisible(true);
    }

    public Screen getCurrentScreen() {
        return screen;
    }
    
    public Camera getCamera(){
    	return cam;
    }
    
    public Frame getContainer(){
    	return frame;
    }
}
