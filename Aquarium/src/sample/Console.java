package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.INFORMATION;

/**
 * class that define the console, the IHM on which the user will interact
 */
public class Console extends Stage {

    //Window Structure
    public Aquarium aquarium;
    private ToolBar toolbar;
    private String id;

    //Display
    Pane entry;
    VBox vb;
    protected TextArea display;
    private TextField input;
    private ComboBox comboBox;
    private int width;
    private int height;

    //History
    protected List<String> history;
    protected int historyCount = 0;

    //Others
    Thread continuously;
    protected Parser parser;

    /**
     * constructor of the console
     * @param a aquarium related to the console
     * @param w width of the console
     * @param h height of the console
     */
    public Console(Aquarium a, int w, int h) {
        aquarium = a;
        width = w;
        height = h;
        this.setX(Screen.getPrimary().getVisualBounds().getWidth()-width-100);
        this.setY(100);

        initDisplay();

        parser = new Parser(this, display);
    }

    /**
     * initialize the severals elements of the display
     */
    private void initDisplay() {
        this.setTitle("Console");
        entry = new Pane();

        display = new TextArea();
        display.setEditable(false);

        history = new ArrayList<>();

        initTab();
        initInput();
        initComboBox();

        vb = new VBox();
        display.setMinHeight(height-input.getHeight()-toolbar.getHeight()-50); //-50 for the height of windows itself
        display.setMaxHeight(height-input.getHeight()-toolbar.getHeight()-50);
        vb.getChildren().addAll(toolbar, display, input);
        entry.getChildren().add(vb);

        this.setScene(new Scene(entry, width, height));
    }

    /**
     * write a string on the display of the console
     * @param s string to write
     */
    public void writeDisplay(String s) {
        display.appendText("< " + s + System.lineSeparator());
    }

    /**
     * allow to know if the reicever thread is over
     * @return 1 if the thread is over, 0 else
     */
    public boolean threadIsOver() {
        return (parser.communicator.receiver != null && parser.communicator.receiver.getState() == Thread.State.TERMINATED);
    }

    /**
     * set the id of the view
     * @param id new id of the view
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * set a string in the input of the console
     * @param s string that we want suggest
     */
    public void suggestInput(String s) {
        input.setText(s);
    }

    /**
     * initialise the severals element of the input field
     */
    private void initInput() {
        input = new TextField ();
        input.setPrefColumnCount(20);
        input.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    String action = input.getText();
                    history.add(action);
                    historyCount= history.size();
                    display.appendText("> " + action + System.lineSeparator());
                    input.clear();

                    aquarium.writeLogs("Nouvelle entree : "+ action+ "/n");
                    parser.parser(action);
                    break;
                case UP:
                    if (historyCount == 0) {
                        break;
                    }
                    historyCount--;
                    input.setText(history.get(historyCount));
                    input.selectAll();
                    break;
                case DOWN:
                    if (historyCount == history.size() - 1) {
                        historyCount = history.size();
                        input.clear();
                        break;
                    }
                    if (historyCount < history.size()) {
                        historyCount++;
                        input.setText(history.get(historyCount));
                        input.selectAll();
                    }
                    break;
                default:
                    historyCount = history.size();
                    break;
            }
        });
    }

    /**
     * allow to log out from the console
     */
    public void logOut() {
        parser.communicator.logOut();
        this.close();
    }

    /**
     * initialise every elements of the combo box for the quick orders
     */
    private void initComboBox() {
        comboBox = new ComboBox();
        comboBox.getItems().addAll("addFish PoissonRouge at 50x50, 15x15, RandomWayPoint",
                                                "addFish PoissonClown at 50x50, 10x10, VerticalWayPoint",
                                                "addFish PoissonNain at 50x50, 10x10, HorizontalWayPoint",
                                                "startFish PoissonRouge",
                                                "delFish PoissonRouge",
                                                "getFishes",
                                                "getFishesContinuously");
        comboBox.setPromptText("Quick order");
        comboBox.setEditable(true);
        comboBox.setMinWidth(width);
        comboBox.setMaxWidth(width);
        comboBox.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    String action = input.getText();
                    history.add(action);
                    historyCount= history.size();
                    display.appendText("> " + action + System.lineSeparator());
                    comboBox.setValue(null);
                    input.clear();

                    aquarium.writeLogs("Nouvelle entree : "+ action+ "/n");
                    parser.parser(action);
                    break;
                case UP:
                    if (historyCount == 0) {
                        break;
                    }
                    historyCount--;
                    comboBox.setValue(history.get(historyCount));
                    input.setText(history.get(historyCount));
                    input.selectAll();
                    break;
                case DOWN:
                    if (historyCount == history.size() - 1) {
                        historyCount = history.size();
                        input.clear();
                        comboBox.setValue(null);
                        break;
                    }
                    if (historyCount < history.size()) {
                        historyCount++;
                        comboBox.setValue(history.get(historyCount));
                        input.setText(history.get(historyCount));
                        input.selectAll();
                    }
                    break;
                default:
                    historyCount = history.size();
                    break;
            }});

        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                input.setText(t1);
            }
        });
    }

    /**
     * initialise the tab bar of the console
     */
    private void initTab() {

        Label tab1 = new Label("Fishes");
        initInfoTab(tab1,"Fishes", "Fishes available : ", "Here the fishes available : " + System.lineSeparator() +getFishesAvailable() );

        Label tab2 = new Label("Mobility Models");
        initInfoTab(tab2, "Mobility Models", "Mobility models available : ", "Here the models available : " + System.lineSeparator() + getModelsAvailable());

        Label tab3 = new Label("Help");
        initInfoTab(tab3, "Help", "Commands available : ", "Here the command available : " + System.lineSeparator()
                                                                        + " - hello [in as ID]" + System.lineSeparator()
                                                                        + " - status " + System.lineSeparator()
                                                                        + " - startFish name " + System.lineSeparator()
                                                                        + " - addFish name x y w h modelMoving " + System.lineSeparator()
                                                                        + " - delFish name " + System.lineSeparator()
                                                                        + " - setGoal name x y delay" + System.lineSeparator()
                                                                        + " - getFishes " + System.lineSeparator()
                                                                        + " - getFishesContinuously " + System.lineSeparator());

        Label tab4 = new Label("About");
        initInfoTab(tab4, "About", "About the software", "This software was made for a student's project as part of ENSEIRB-MATMECA's studies." + System.lineSeparator()
                                                                        + " Contributors : " + System.lineSeparator()
                                                                        + " Paul Breton " + System.lineSeparator()
                                                                        + " Paul Gaulier " + System.lineSeparator()
                                                                        + " Louise Mouret" + System.lineSeparator()
                                                                        + " Laurie-Anne Parant " + System.lineSeparator()
                                                                        + " Nicolas Vidal " + System.lineSeparator());

        Label tab5 = new Label("Scenario");
        tab5.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                new ScenarioWindow(input).show();
            }
        });

        CheckBox cb = new CheckBox("Quick order");
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                //cb.setSelected(!newValue);
                if (newValue) {
                    vb.getChildren().remove(input);
                    vb.getChildren().add(comboBox);
                    entry.getChildren().setAll(vb);
                }
                else {
                    vb.getChildren().remove(comboBox);
                    vb.getChildren().add(input);
                    entry.getChildren().setAll(vb);
                }
            }
        });

        toolbar = new ToolBar(tab1, new Separator(), tab2, new Separator(), tab3, new Separator(), tab4, new Separator(), tab5, new Separator(), cb);
    }

    /**
     * initialise a tab
     * @param l label of the new tab
     * @param title title of the new tab
     * @param header header of the new tab
     * @param content content of the new tab
     */
    private void initInfoTab(Label l, String title, String header, String content) {
        l.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    Alert alert = new Alert(INFORMATION);
                    alert.setHeaderText(header);
                    alert.setTitle(title);
                    alert.setHeight(200);
                    alert.setContentText(content);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    });
                }
            }
        });
    }

    /**
     * get the models of mobility that are available
     * @return a string that contains every models available
     */
    private String getModelsAvailable() {
        String res="";
        try {

            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/resources/MobilityModels.txt"), StandardCharsets.UTF_8);
            for (String s : lines) {
                res += "- " + s + System.lineSeparator();
            }
        } catch (IOException e) {System.out.println("Exception : " + e.toString());
            aquarium.writeLogs("Exception lors de la lecture du fichier de modeles de mobilite/n");}

        return res;
    }

    /**
     * get the name of fish that are available
     * @return a string that contains every name of fish available
     */
    private String getFishesAvailable() {
            File directory = new File(  System.getProperty("user.dir") + "/resources/Images/");
            System.out.println("DEBUG : Directory " + directory.getAbsolutePath() + " and LIST=" + directory.list());
            String[] listFiles = directory.list();
            String res = new String("");
            for(int i=0;i<listFiles.length;i++) {
                listFiles[i] = listFiles[i].substring(0, listFiles[i].indexOf("."));
                if (!listFiles[i].equalsIgnoreCase("bg") && !listFiles[i].equalsIgnoreCase("bb") && !listFiles[i].contains("2")) { //to avaoid background
                    res += "- " + listFiles[i] + System.lineSeparator();
                }
            }
            return res;
    }
}