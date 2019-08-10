package view;

import controller.RotationController;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import model.Engine;

/*
 "god-class" med mainmetod. Utgör själva fönstret
 */
public class RubiksKub extends JFrame {// start klass rubikskub

    Engine Engine;              // spelets model
    CanvasPanel canvaspanel;    // spelets panel
    SettingsPanel SettingsPanel;
    RotationController rotcon;
    public RubiksKub() { //start rubiksKub konstruktor
        
        // skapar model, view och controller
        Engine = new Engine();
        canvaspanel = new CanvasPanel();
        SettingsPanel = new SettingsPanel();
        rotcon = new RotationController(Engine, canvaspanel);

        //lägg till canvas i framen och initiera den
        add("North", canvaspanel);
        canvaspanel.initieraCanvas();

        //sätter canvas controller till controllern
        canvaspanel.setController(rotcon);
        
        //lägg till settingsPanel
        add("South", SettingsPanel);

        pack();                                     //pack?
        setVisible(true);                           //gör synlig
        setDefaultCloseOperation(EXIT_ON_CLOSE);    //gör så man kan stänga fönstret
        
        blandaKub();
        
    } // slut rubikskub konstruktor

    private void blandaKub() {
        String k="QWEASD";
        for (int i = 0; i < 20; i++) {
            int ix= (int)(k.length()*Math.random());
            KeyEvent ke= new KeyEvent(this, 42, 7654321, 0, k.charAt(ix), (char)(k.charAt(ix)|32));
            rotcon.keyPressed(ke );
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }
    
    
    //skapa fönstret och gör en rubikskub i den
    public static void main(String[] args) { // start mainmetod
        
        new RubiksKub();
        
    } // slut mainmetod
    
} // slut klass rubikskub