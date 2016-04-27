package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputDialog;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import s305073.controller.EditorController;


import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the menu bars on top of the stage
 * */
public class MenuController {

    private MasterController masterController;

    @FXML
    private MenuItem openBtn;
    @FXML
    RadioMenuItem theStripS305080, statsS305080, markupS305080;

    /**
     * Stores the reference to the masterController
     * @param masterController reference to the masterController
     */
    public void initialize(MasterController masterController) {

        this.masterController = masterController;
    }

    /**
     * Opens the fileChooser so the user can choose a pattern to import
     */
    public void openFileChooser(){

        masterController.choosePattern();
    }

    public void saveFile(){
       masterController.getCanvasController().saveToFile();
    }
    public void onAbout(ActionEvent actionEvent) {

        System.out.println("About clicked");
    }

    public void setConwayRule(ActionEvent actionEvent) {

        masterController.getCanvasController().setRule("classic");
    }

    public void setHighLifeRule(ActionEvent actionEvent) {

        masterController.getCanvasController().setRule("highlife");
    }

    /**
     * opens a dialog so the user can choose a custom rule
     * @param actionEvent
     */
    public void setCustomRule(ActionEvent actionEvent) {

        TextInputDialog dialog = new TextInputDialog(masterController.getCanvasController().gol.getRule().toString());

        dialog.setTitle("Custom rule");
        dialog.setHeaderText("Enter custom rule code");
        dialog.setContentText("B: Neighbours needed for birth\nS: Neighbours needed for survival\n" +
                "Example: Conway's rule would be B3/S23");

        Optional<String> result = dialog.showAndWait();

        // Traditional way to get the response value.
        if (result.isPresent()){
            System.out.println("Custom rule set: " + result.get());

            masterController.getCanvasController().setRule(result.get());
        }
    }

    public void setLWDRule(ActionEvent actionEvent) {
        masterController.getCanvasController().setRule("B3/S012345678");
    }

    public void setSeedsRule(ActionEvent actionEvent) {
        masterController.getCanvasController().setRule("B2/S");
    }

    public void setDiamoebaRule(ActionEvent actionEvent) {
        masterController.getCanvasController().setRule("B35678/S5678");
    }

    public void setReplicatorRule(ActionEvent actionEvent) {
        masterController.getCanvasController().setRule("B1357/S1357");
    }

    public void setDNNRule(ActionEvent actionEvent) {
        masterController.getCanvasController().setRule("B3678/S34678");
    }

    public void clearGrid(ActionEvent actionEvent) { masterController.getCanvasController().clearGrid(); }



    //region s305080
    public void showTheStrip(){

        if(!theStripS305080.isSelected())
            masterController.closeTheStrip();
        else
            masterController.showTheStrip();
    }
    /**
     * Changes the selected status of the radioMenuItem
     * @param theStripIsShowing True if it should be selected, false if not
     */
    public void setTheStripIsShowing(boolean theStripIsShowing) {
        theStripS305080.setSelected(theStripIsShowing);
    }

    public void showS305080Stats(){
        if(!statsS305080.isSelected())
            masterController.closeStats();
        else
            masterController.showStats();
    }
    /**
     * Changes the selected status of the radioMenuItem
     * @param theStripIsShowing True if it should be selected, false if not
     */
    public void setStatsShowing(boolean theStripIsShowing) {
        statsS305080.setSelected(theStripIsShowing);
    }
    // endregion

    public void launchEditor(ActionEvent actionEvent) {
        Stage editor = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../s305073/view/EditorView.fxml"));
        editor.initModality(Modality.WINDOW_MODAL);
        editor.initOwner(masterController.stage);

        GridPane root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 1000, 700);
        editor.setScene(scene);

        EditorController editorController = loader.getController();
        editorController.getDeepCopyGol(masterController.getCanvasController().gol);
        editorController.loadPattern();

        masterController.getCanvasController().stopAnimation();
        masterController.getToolController().changeIconToPlay();

        editor.setTitle("Pattern Editor");

        editor.setX(550);
        editor.setY(250);
        editor.showAndWait();
        editor = null;
    }

    public void openStatWindow(ActionEvent actionEvent) {

        masterController.openStatWindow();
    }

    public void activateMarkup() {
        if (markupS305080.isSelected()){
            masterController.getCanvasController().activateMarkup();
        }
        else {
            masterController.getCanvasController().deactivateMarkup();
        }
    }
}
