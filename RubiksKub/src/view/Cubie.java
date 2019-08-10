package view;

import com.sun.j3d.utils.geometry.Box;
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

public class Cubie extends TransformGroup { // start klass Cubie

    Vector3d position;      // cubiens startposition
    Vector3d koords = new Vector3d();
    Box box;                // cubien
    ArrayList<Color3f> colors = new ArrayList<Color3f>(); //lista med färger

    public Cubie(float x, float y, float z) { // start konstruktor cubie
        position = new Vector3d(x, y, z);
        koords = new Vector3d(x, y, z);
        //skapar listan med färger
        skapaFärgLista();
        //skapar själva boxen
        createCubie();
    } // slut konstruktor cubie

    //metoden som skapar cubien
    private void createCubie() { //start void crateCubie
        // gör cubien svart tills vidare (så att den är svart inuti sedan)
        Appearance ap = new Appearance();
        ap.setMaterial(new Material(colors.get(0), colors.get(0), colors.get(0), colors.get(0), 0.0f));
        box = new Box(1f, 1f, 1f, ap);

        //bestämmer dess position
        TransformGroup tg = new TransformGroup();
        Transform3D xform = new Transform3D();
        xform.setTranslation(position);
        tg.setTransform(xform);
        tg.addChild(box);
        //lägger till boxen
        addChild(tg);
    } // slut void createCubie

    //uppdaterar cubiens rotation
    public void uppdateraRotation(final String s) { // start void uppdateraRotation
        Runnable runner = new Runnable() { // start runnable
            @Override
            public void run() { // start void run
                Transform3D old = new Transform3D(); // all = identitetsmatris
                getTransform(old);
                for (int i = 0; i <= 128; i++) { // start animationsloop
                    Transform3D all = new Transform3D(); // all = identitetsmatris
                    switch (s) {
                        case "x":
                            all.rotX(i * Math.PI / 256);
                            break;
                        case "y":
                            all.rotY(i * Math.PI / 256);
                            break;
                        case "z":
                            all.rotZ(i * Math.PI / 256);
                            break;
                    }
                    all.mul(old);
                    Cubie.this.setTransform(all);
                    long nanos = System.nanoTime();
                    while (System.nanoTime() < nanos + 4000000) {
                        Thread.yield();
                    }

                } // slut animationsloop

                uppdateraPosition();

            } // slut void run

            private void uppdateraPosition() {
                switch (s) {
                    case "z":
                        double tmp = koords.x;
                        koords.x = -koords.y;
                        koords.y = tmp;
                        break;
                    case "y":
                        tmp = koords.x;
                        koords.x = koords.z;
                        koords.z = -tmp;
                        break;
                    case "x":
                        tmp = koords.z;
                        koords.z = koords.y;
                        koords.y = -tmp;
                        break;
                }
            }
        }; //slut runnable
        new Thread(runner).start();
    }// slut void uppdateraRotation

//    private Vector3d roundfix(Vector3d position) {
//        if (abs(position.getX()) > 2f) {
//            position.setX(2.1 * signum(position.getX()));
//        } else {
//            position.setX(0);
//        }
//
//        if (abs(position.getY()) > 2f) {
//            position.setY(2.1 * signum(position.getY()));
//        } else {
//            position.setY(0);
//        }
//
//        if (abs(position.getZ()) > 2f) {
//            position.setZ(2.1 * signum(position.getZ()));
//        } else {
//            position.setZ(0);
//        }
//        return position;
//    }
    
    //ger lämplig färg på varje sida beroende av vad som matats in. Använder sig av färglistan.
    public void setSideApprearance(int sida, int färg) { // start void setSideAppearance
        Appearance ap = new Appearance();
        ap.setMaterial(new Material(colors.get(färg), colors.get(färg), colors.get(färg), colors.get(färg), 0.0f));
        box.getShape(sida).setAppearance(ap);
    } // slut void setSideAppearance

    // lägger till de färger som ska användas
    private void skapaFärgLista() { //start void skapafärglista
        colors.add(new Color3f(0f, 0f, 0f));   // svart
        colors.add(new Color3f(1f, 1f, 1f));   // vit
        colors.add(new Color3f(1f, 1f, 0f));   // gul
        colors.add(new Color3f(1f, .6f, 0f));  // orange
        colors.add(new Color3f(1f, 0f, 0f));   // röd
        colors.add(new Color3f(0f, 1f, 0f));   // grön
        colors.add(new Color3f(0f, 0f, 1f));   // blå
    } // slut void skapafärglista

} // slut klass Cubie