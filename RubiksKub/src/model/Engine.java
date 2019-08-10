package model;

import view.CanvasPanel;
import view.Cubie;

/*
Klass som sköter logik 
 */
public class Engine { //start klass Engine

    public Engine() {
    } // konstruktor

    public void rotera(char key, CanvasPanel canvasPanel) {
        canvasPanel.skapaGrupper();
        if (key == 'a') {
//            canvasPanel.skapaGrupper();
            for (Cubie c : canvasPanel.blå) {
                c.uppdateraRotation("y");
            }
        }
        if (key == 'w') {
//            canvasPanel.skapaGrupper();
            for (Cubie c : canvasPanel.orang) {
                c.uppdateraRotation("x");
            }
        }
        if (key == 'e') {
//            canvasPanel.skapaGrupper();
            for (Cubie c : canvasPanel.vit) {
                c.uppdateraRotation("y");
            }
        }
        if (key == 'd') {
//            canvasPanel.skapaGrupper();
            for (Cubie c : canvasPanel.röd) {
                c.uppdateraRotation("x");
            }
        }
        if (key == 's') {
//            canvasPanel.skapaGrupper();
            for (Cubie c : canvasPanel.grön) {
                c.uppdateraRotation("z");
            }
        }
        if (key == 'q') {
//            canvasPanel.skapaGrupper();
            for (Cubie c : canvasPanel.gul) {
                c.uppdateraRotation("z");
            }
        }
        
    }

} // slut klass engine
