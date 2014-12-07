package framework;

import globals.GlobalVars;

import javax.swing.JPanel;

public interface Game {
    
    public Input getInput();

    public FileIO getFileIO();

    public void setWindowContainer(JPanel container);
    public void setWindowSize(int width, int height);
    public void setWindowPosition(int x, int y);
    public void setWindowCentered(boolean centered);

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();
    
    public Camera getCamera();
    
    public Object getContainer();
    
    public GlobalVars getGlobalVars();
    
    public void saveSettingsXML();
}