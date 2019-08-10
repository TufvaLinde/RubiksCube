package view;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import static com.sun.j3d.utils.geometry.Box.BACK;
import static com.sun.j3d.utils.geometry.Box.BOTTOM;
import static com.sun.j3d.utils.geometry.Box.FRONT;
import static com.sun.j3d.utils.geometry.Box.LEFT;
import static com.sun.j3d.utils.geometry.Box.RIGHT;
import static com.sun.j3d.utils.geometry.Box.TOP;
import com.sun.j3d.utils.universe.SimpleUniverse;
import controller.RotationController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/*
 Klassen som utgör canvas för spelet
 Innehåller canvas, universum, cubies
 */
public class CanvasPanel extends JPanel { // start klass CanvasPanel

    private TransformGroup viewTransform;       // Där kameran är
    private Canvas3D canvas3D;                          // Canvasen
    private BranchGroup scen;                           // Central BranchGroup
    private SimpleUniverse universum;                   // Universumet

    //Lista över alla cubies i kuben
    public ArrayList<Cubie> allCubies = new ArrayList();
    // Rotationsgrupper. Namngivna efter färgen på mitten-cubien (eftersom denna aldrig byter position)
    public ArrayList<Cubie> blå = new ArrayList();
    public ArrayList<Cubie> orang = new ArrayList();
    public ArrayList<Cubie> grön = new ArrayList();
    public ArrayList<Cubie> röd = new ArrayList();
    public ArrayList<Cubie> gul = new ArrayList();
    public ArrayList<Cubie> vit = new ArrayList();

    public CanvasPanel() { // start konstruktor CanvasPanel

        setLayout(new BorderLayout());              // Gör så att canvas3d blir synlig i panelen
        setPreferredSize(new Dimension(800, 700));  // Strlk på panel
        setVisible(true);   

    } // slut konstruktor canvas

    // initerar canvasen och skapar allt i den
    public void initieraCanvas() { // start void initieraCanvas
        setFocusable(true);
        skapaUniversum();
        skapaKub();

        //lägg till kamerarörelse
        addMouseNavigator(universum, canvas3D);

        //komplierar scenen? Bra för...?
        scen.compile();

        //lägg till scenen i universum
        universum.addBranchGraph(scen);

    } // slut void initieraCanvas

    // skapar komponenterna i universumet, bakgrund etc, men ej kuben
    private void skapaUniversum() { // start void skapaUniversum

        // skapar en canvas och lägger till i panelen
        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add("Center", canvas3D);

        // skapar ett universum med canvas i
        universum = new SimpleUniverse(canvas3D);

        // sätter storlek av canvas? hur långt man kan se. Spelar inte så stor roll då vi inte rör på oss i rummet
        canvas3D.getView().setBackClipDistance(10000);

        // skapar scenen
        scen = new BranchGroup();

        //skapar en bakgrund
        BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 1000.0);
        Background bg = new Background(new Color3f(0, 0, 0.22f));
        bg.setApplicationBounds(bounds);
        scen.addChild(bg);

        skapaKamera();

    } // slut void skapaUniversum

    //initierar kameran
    private void skapaKamera() { // start void skapaKamera

        viewTransform = universum.getViewingPlatform().getViewPlatformTransform();
        //sätter kameran 20 enheter utanför origo
        Vector3f position = new Vector3f(0, 0, 20);
        Transform3D t3d2 = new Transform3D();
        t3d2.setTranslation(position);
        viewTransform.setTransform(t3d2);

    } // slut void skapakamera

    // skapar komponenterna i rubikskuben
    private void skapaKub() { // start void skapaKub
        // skapar cubies och lägger till i scenen och i listan allCubies
        for (float x = -2.1f; x <= 2.1f; x += 2.1f) {
            for (float y = -2.1f; y <= 2.1f; y += 2.1f) {
                for (float z = -2.1f; z <= 2.1f; z += 2.1f) {
                    Cubie c = new Cubie(x, y, z);
                    allCubies.add((Cubie) c);
                    c.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //bra för något kanske
                    scen.addChild(c);
                }
            }
        }
        //sätt cubiesarna i en grupp och färglägg sidorna
        skapaGrupper();
        setSideAp();

    } // slut void skapaKub

    // tillåter användaren att flytta runt kameran i en sfär runt kuben som befinner sig i origo
    private void addMouseNavigator(SimpleUniverse su, Canvas3D canvas) { // start void addMouseNavigator

        OrbitBehavior orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ROTATE);
        orbit.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000.0));
        su.getViewingPlatform().setViewPlatformBehavior(orbit);

    }// slut void MouseNavigator

    // gör så att rotationcontroller är canvasens keylistener. dvs så att user input går genom controller och ej i view.
    void setController(RotationController rotationController) { // start void setController

        canvas3D.addKeyListener((KeyListener) rotationController);
        canvas3D.requestFocus();
    } // slut void setController

    // tilldelar cubiesarna en grupp beroende av dess position
    public void skapaGrupper() { // start void skapaGrupper
        grön.clear();
        gul.clear();
        röd.clear();
        blå.clear();
        orang.clear();
        vit.clear();

        for (Cubie c : allCubies) {
            Vector3d v = c.koords;

            if (v.getY() < -2f) {
                blå.add(c);
            }
            if (v.getY() > 2f) {
                vit.add(c);
            }
            if (v.getX() < -2f) {
                orang.add(c);
            }
            if (v.getX() > 2f) {
                röd.add(c);
            }
            if (v.getZ() < -2f) {
                gul.add(c);
            }
            if (v.getZ() > 2f) {
                grön.add(c);
            }

            int xx = (c.koords.x < -2) ? 0 : (c.koords.x < 2) ? 1 : 2;
            int yy = (c.koords.y < -2) ? 0 : (c.koords.y < 2) ? 1 : 2;
            int zz = (c.koords.z < -2) ? 0 : (c.koords.z < 2) ? 1 : 2;
        }
    } // slut void skapaGrupper

    // sätter färgen av varje cubie i varje grupp till färgen på den sida som ska färgas. 
    private void setSideAp() { // start void setSideAp

        // Tal som underlättar för färgning av kubens sidor
        int black = 0;
        int white = 1;
        int yellow = 2;
        int orange = 3;
        int red = 4;
        int green = 5;
        int blue = 6;

        for (Cubie c : blå) {
            c.setSideApprearance(BOTTOM, blue);
        }
        for (Cubie c : röd) {
            c.setSideApprearance(RIGHT, red);
        }
        for (Cubie c : orang) {
            c.setSideApprearance(LEFT, orange);
        }
        for (Cubie c : gul) {
            c.setSideApprearance(BACK, yellow);
        }
        for (Cubie c : vit) {
            c.setSideApprearance(TOP, white);
        }
        for (Cubie c : grön) {
            c.setSideApprearance(FRONT, green);
        }

    } // slut void setSideAp


} //slut klass CanvasPanel