import controller.MasterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


public class GolApplication extends Application {

    /**
     * Loads the manView fxml document and starts the application
     * @param stage the primary stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception{

        // load main fxml view
        FXMLLoader loader = new FXMLLoader(
                GolApplication.class.getResource("MainView.fxml"));

        try {
            // load root element of fxml to reference
            BorderPane root = loader.load();

            // set control to master controller
            MasterController rootController = loader.getController();

            // passing stage and root to master controller
            rootController.initialize(stage, root);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load FXML documents");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("");
        }
    }

    /**
     * launches the application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
