package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SettingsPanel extends JPanel{ // Start klass SettingsPanel
    BufferedImage img;
    public SettingsPanel(){ // Start konstruktor SettingsPanel
        
        setPreferredSize(new Dimension(800, 180));   // Strlk på panel
        setVisible(true);                           // Gör synlig
        setBackground(new Color(0,0,56));           // Ge samma bgc som universum
        läggtillKnappar();
        
    } // Slut konstruktor SettingsPanel

    private void läggtillKnappar() {
        JButton undo = new JButton("UNDO");
        JButton shuffle = new JButton("SHUFFLE");
        
        
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/view/Knapp.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JButton solve = new JButton(new ImageIcon(img,"kli"));
        add(solve);
    }
    
} // Slut klass Settingspanel