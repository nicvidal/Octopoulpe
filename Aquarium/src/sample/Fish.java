package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * class to represent the fish and its display
 */
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
    private double toDoX, toDoY;
    private int doEachX, doEachY;
    private int cpt;
    private long lastElapsed;

    /**
     * constructor of a fish
     * @param x position in x of the fish
     * @param y position in y of the fish
     * @param w width of the fish
     * @param h height of the fish
     * @param n name of the fish
     */
    public Fish(int x, int y, int w, int h, String n) {

        initImage(n, w, h);
        setPosition(x, y);
        name = n;
        started = false;
        goal = new Point(-1, -1);
    }

    /**
     * list the file available in the directory Images
     * @return an array of string that contains every name of files in the directory Images
     */
    private String[] listImage(){
        File directory = new File(System.getProperty("user.dir") + "/resources/Images");
        String[] listFiles = directory.list();

        for(int i=0;i<listFiles.length;i++) {
            listFiles[i] = listFiles[i].substring(0, listFiles[i].indexOf("."));
        }
        return listFiles;
    }

    /**
     * initialise the two images for the fish
     * @param s name of the fish
     * @param w width of the fish
     * @param h height of the fish
     */
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

        if (pictureFile.charAt(pictureFile.length()-1) == '2') {
            pictureFile = pictureFile.substring(0, pictureFile.length() - 1);
        }

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

    /**
     * update the attributes started of the fish
     * @param s boolean that specify if the fish is started or not
     */
    public void setStarted(boolean s) {
        started=s;
    }

    /**
     * allow to get a description of the fish
     * @param width the width of the general view (to get the relative position and width)
     * @param height the height of the general view (to get the relative position and width)
     * @return a string that describe the fish
     */
    public String toString(int width, int height) {
        return (name + " at " + x + "x" + y+ ", " + (view1.getFitWidth()*100/width) + "x" + (view1.getFitHeight()*100/height) + (started ? " started": " not started"));
    }

    /**
     * update the size of the fish
     * @param w new width of the fish
     * @param h new height of the fish
     */
    public void setSize(int w, int h) {
        view1.setFitHeight(h);
        view1.setFitWidth(w);
        view2.setFitHeight(h);
        view2.setFitWidth(w);
    }

    /**
     * update the position of the fish
     * @param new_x new position in x of the fish
     * @param new_y new position in y of the fish
     */
    public void setPosition(int new_x, int new_y) {
        x = new_x;
        y = new_y;

        view1.setX(x);
        view1.setY(y);
        view2.setX(x);
        view2.setY(y);
    }

    /**
     * allow to get the name of the fish
     * @return the name of the fish
     */
    public String getName() {
        return name;
    }

    /**
     * update the goal of the fish
     * @param x position in x of the goal
     * @param y position in y of the goal
     * @param d duration of the goal
     * @param averageElapsed average duration of an iteration (a tick)
     */
    public void setGoal(int x, int y, long d, long averageElapsed) {
        goal.x = x;
        goal.y = y;
        timeGoal = d;
        cpt=0;
        doEachX = 1;
        doEachY = 1;
        double nbUpdateInit = d/averageElapsed;
        double nbUpdate = d/averageElapsed;
        this.lastElapsed = averageElapsed;
        if (Math.abs(x-this.x) < nbUpdateInit) {
            toDoX = (x - this.x) == 0 ? 0 : (x - this.x) / Math.abs(x - this.x);
        }
        else {
            toDoX = (x - this.x)/nbUpdate;
            while (Math.abs(toDoX) < 1)
            {
                nbUpdate = nbUpdateInit/doEachX;
                doEachX++;
                toDoX = (x - this.x) / nbUpdate;
            }
        }
        nbUpdate = d/averageElapsed;

        if (Math.abs(y-this.y) < nbUpdateInit) {
            toDoY = (y - this.y) == 0 ? 0 : (y - this.y) / Math.abs(y - this.y);
        }
        else {
            toDoY = (y - this.y) / nbUpdate;
            while (Math.abs(toDoY) < 1)
            {
                nbUpdate = nbUpdateInit/doEachY;
                doEachY++;
                toDoY = (y - this.y) / nbUpdate;
            }
        }
    }

    /**
     * allow to get a view of the fish
     * @param nb specify wich view we want (0 or 1 : for the animation)
     * @return the view we want of the fish
     */
    public ImageView get_View(int nb) {
        if (nb == 1 && started)
            return view2;
        else
            return view1;
    }

    /**
     * update the position of the fish to move it toward its goal
     * @param timeElapsed time elpased since the last move
     */
    public void update(long timeElapsed) {

        if (!goal.equals(new Point(-1, -1))) {
            timeGoal -= timeElapsed;
            cpt++;

            if (timeGoal<=1000 && timeGoal >0) {
                setGoal(goal.x, goal.y, timeGoal, lastElapsed); //to try to rectify a possible mistake of timing
            }

            if (timeGoal <= 0) {
                x=goal.x;
                y=goal.y;
                goal.x = -1;
                goal.y = -1;
            }
            else {
                if (cpt % doEachX == 0) {
                    if (goal.x != x) {
                        if (goal.x < x && x + toDoX < goal.x)
                            x = goal.x;
                        else if (goal.x > x && x + toDoX > goal.x)
                            x = goal.x;
                        else
                            x += toDoX;
                    }
                }

                if (cpt % doEachY == 0) {

                    if (goal.y != y) {
                        if (goal.y < y && y + toDoY < goal.y)
                            y = goal.y;
                        else if (goal.y > y && y + toDoY > goal.y)
                            y = goal.y;
                        else
                            y += toDoY;
                    }
                }

                setPosition(x, y);

                if (goal.equals(new Point(x, y))) {
                    goal.x = -1;
                    goal.y = -1;
                }
            }
        }
    }
}
