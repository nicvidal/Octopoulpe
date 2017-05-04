package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.net.URL;

public class Fish {

    //View
    private ImageView view1;
    private ImageView view2;

    //attributes
    private String name;
    private int x;
    private int y;
    private Point goal;
    private long timeGoal; //in millisecond
    private boolean started;

    public Fish(int x, int y, int w, int h, String s) {

        initImage(s, w, h);
        setPosition(x, y);
        name = s;
        started = false;
        goal = new Point(-1, -1);
    }

    private String[] listImage(){
        File directory = new File(  System.getProperty("user.dir") + "/src/sample/Images/");
        String[] listFiles = directory.list();

        for(int i=0;i<listFiles.length;i++) {
            listFiles[i] = listFiles[i].substring(0, listFiles[i].indexOf("."));
        }
        return listFiles;
    }

    private void initImage(String s, int w, int h) {
        int index = s.indexOf('_');
        String pictureFile = (index == -1) ? new String(s) : new String(s.substring(0, index));
        Boolean exist = false;
        String [] listFiles = listImage();
        for (String f: listFiles) {
             if (f.equalsIgnoreCase(pictureFile) && !f.equalsIgnoreCase("bg") && !f.equalsIgnoreCase("bb")) { //to prevent bug with background
              exist = true;
             }
        }
        if (! exist)
            pictureFile = new String("PoissonNain"); //Default image

        final URL url1 = getClass().getResource( "Images/" + pictureFile + ".png");
        final URL url2 = getClass().getResource("Images/" + pictureFile + "2.png");
        final Image img1 = new Image(url1.toExternalForm());
        final Image img2 = new Image(url2.toExternalForm());

        view1 =  new ImageView(img1);
        view2 =  new ImageView(img2);
        view1.setFitHeight(h);
        view1.setFitWidth(w);
        view2.setFitHeight(h);
        view2.setFitWidth(w);
    }

    public void setStarted(boolean s) {
        started=s;
    }
    public String toString(int width, int height) {
        return (name + " at " + x + "x" + y+ ", " + (view1.getFitWidth()*100/width) + "x" + (view1.getFitHeight()*100/height) + (started ? " started": " not started"));
    }

    public void setSize(int w, int h) {
        view1.setFitHeight(h);
        view1.setFitWidth(w);
        view2.setFitHeight(h);
        view2.setFitWidth(w);
    }

    public void setPosition(int new_x, int new_y) {
        x = new_x;
        y = new_y;

        view1.setX(x);
        view1.setY(y);
        view2.setX(x);
        view2.setY(y);
    }

    public String getName() {
        return name;
    }

    public void setGoal(int x, int y, long d) {
        goal.x = x;
        goal.y = y;
        timeGoal = d;
    }

    public ImageView get_View(int nb) {
        if (nb == 1 && started)
            return view2;
        else
            return view1;
    }

    public void update(long timeElapsed) {

        if (!goal.equals(new Point(-1, -1))) {
            timeGoal -= timeElapsed;
            //System.out.println("DEBUG : Il s'est ecoule " + timeElapsed + " millisecondes");
            //System.out.println("DEBUG : Il nous reste " + timeGoal + " millisecondes");

            if (timeGoal <= 0) {
                x = goal.x;
                y = goal.y;
                setPosition(x, y);
                goal.x = -1;
                goal.y = -1;
            }
            else {
                int distToGoalX = Math.abs(x - goal.x);
                int distToGoalY = Math.abs(y - goal.y);
                long distToDoX = timeElapsed * distToGoalX / timeGoal;
                long distToDoY = timeElapsed * distToGoalY / timeGoal;

                //System.out.println("DEBUG : On doit faire " + distToDoX + " en X");
                //System.out.println("DEBUG : On doit faire " + distToDoY + " en Y");

                //System.out.println("DEBUG : On etait en " + x + "/" + y);

                if (goal.x > x)
                    x += distToDoX;
                else if (goal.x < x)
                    x -= distToDoX;

                if (goal.y > y)
                    y += distToDoY;
                else if (goal.y < y)
                    y -= distToDoY;

                setPosition(x, y);

                if (goal.equals(new Point(x, y))) {
                    view1.setRotate(0);
                    view2.setRotate(0);
                    goal.x = -1;
                    goal.y = -1;
                }
            }

        }
    }
}
