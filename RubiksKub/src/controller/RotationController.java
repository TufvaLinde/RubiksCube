package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import static java.lang.Thread.sleep;
import model.Engine;
import view.CanvasPanel;

/*
 Klassen som kommunicerar user input med view och logiken i model. 
 */
public class RotationController implements KeyListener {
    long pressTime= System.currentTimeMillis();
    boolean[] keys = new boolean[256];
    private Engine engine;
    private CanvasPanel canvasPanel;

    public RotationController(Engine engine, CanvasPanel canvasPanel) {
        this.engine = engine;
        this.canvasPanel = canvasPanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if( System.currentTimeMillis()<pressTime+200){
            return;
        }
        keys[e.getKeyCode()] = true;
        engine.rotera(e.getKeyChar(), canvasPanel);
        pressTime= System.currentTimeMillis();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
