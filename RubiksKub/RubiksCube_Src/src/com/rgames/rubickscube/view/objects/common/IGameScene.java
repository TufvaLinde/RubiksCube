package com.rgames.rubickscube.view.objects.common;

/**
 */
public interface IGameScene {

    void init();
    
    void render(boolean simple);
    
    void mouseUp(int button, int x, int y);
    
    void mouseDown(int button, int x, int y);
    
    void mouseMove(int x, int y);
    
    void keyUp(int keyCode);
    
    void keyDown(int keyCode);
    
    void reset();
    
    long getTimeCreated();
}
