package s305061.statistics;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import model.DynamicGameOfLife;
import model.GameOfLife;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by And on 06.04.2016.
 */
public class StatController {

    @FXML private LineChart<Double, Double> graph;
    @FXML private TextField textField;
    @FXML private ProgressIndicator progressIndicator;

    private ObservableList<XYChart.Series<Double, Double>> lineChartData;

    private LineChart.Series<Double, Double> livingSeries;
    private LineChart.Series<Double, Double> growthSeries;
    private LineChart.Series<Double, Double> similaritySeries;

    private final float ALPHA = 0.5f;
    private final float BETA = 3.0f;
    private final float GAMMA = 0.25f;

    private DynamicGameOfLife gol;

    public void setGol(DynamicGameOfLife gol){
        this.gol = gol;
    }

    private void setProgress(double progress){ progressIndicator.setProgress(progress); }

    /**
     * Initialization method. Called when statistics window opens
     */
    @FXML
    private void initialize() {

        lineChartData = FXCollections.observableArrayList();

        livingSeries = new LineChart.Series<>();
        livingSeries.setName("Live cells");
        lineChartData.add(livingSeries);

        growthSeries = new LineChart.Series<>();
        growthSeries.setName("Cell growth");
        lineChartData.add(growthSeries);

        similaritySeries = new LineChart.Series<>();
        similaritySeries.setName("Similarity measure");
        lineChartData.add(similaritySeries);

        graph.setData(lineChartData);
    }

    /**
     * Clears the statistics data elements from the line chart.
     */
    public void clearStats(){

        livingSeries.getData().clear();
        growthSeries.getData().clear();
        similaritySeries.getData().clear();
    }

    /**
     * Collects statistics for a specified number of iterations. Number of live cells, cell growth and similarity measure will be collected
     * @param iterations Number of iterations to evolve and collect statistics from
     * @return  An array of integer values representing live cells, cell growth and similarity measure for each iteration
     */
    public int[][] getStatistics(int iterations){

        int[][] stats = new int[3][iterations];
        double[] representations = new double[iterations];

        DynamicGameOfLife clonedGol = gol.clone();

        int previousLiving = 0;

        setProgress(0);

        // for each iteration, add live cell count and cell growth to the array stats,
        // then add the iteration's reduced representation to the array representations
        for(int iteration = 0; iteration < iterations; iteration++){

            int currentLiving = clonedGol.getCellCount();
            int growth = 0;

            if(iteration > 0)
                growth = currentLiving - previousLiving;

            stats[0][iteration] = currentLiving;
            stats[1][iteration] = growth;

            representations[iteration] = ALPHA*currentLiving + BETA*growth + GAMMA*getGeometricFactor(clonedGol);

            clonedGol.nextGeneration();
            previousLiving = currentLiving;

            setProgress(((double)iteration/(double)iterations));
        }

        // compare all the reduced representations with each other, return the best match for each iteration
        for(int repA = 0; repA < iterations; repA++) {

            int maxSimilarity = 0;

            for(int repB = 0; repB < iterations; repB++) {

                if(repA == repB)
                    continue;

                int similarity = compareRepresentations(representations[repA], representations[repB]);

                if (similarity > maxSimilarity){
                    maxSimilarity = similarity;
                }
            }

            stats[2][repA] = maxSimilarity;

            //setProgress(0.9 + 0.1*((double)repA/(double)(iterations)));
        }

        setProgress(1);

        return stats;
    }

    /**
     * Compares two reduced representations and calculate the similarity in percent
     * @param repA A reduced representation of an iteration
     * @param repB A reduced representation of another iteration
     * @return Similarity of the two representations in percent
     */
    private int compareRepresentations(double repA, double repB) {

        repA = Math.abs(repA);
        repB = Math.abs(repB);

        double min = Math.min(repA, repB);
        double max = Math.max(repA, repB);

        int similarity =(int)(100*min/max);

        return similarity;
    }

    /**
     * Sums the x and y coordinates of all the live cells on the grid.
     * Used in calculation of the similarity measure, enables the position of patterns to be a factor
     * @param gol Reference to the DynamicGameOfLife object to gather information from
     * @return The geometric factor, the sum of x and y coordinates of live cells
     */
    private double getGeometricFactor(DynamicGameOfLife gol) {

        double geoFactor = 0;

        for(int x = 0; x < gol.getGridWidth(); x++)
            for (int y = 0; y < gol.getGridHeight(); y++)
                if(gol.isCellAlive(x,y))
                    geoFactor += x + y;
        return geoFactor;
    }

    /**
     * Displays statistics at the line chart
     * Calls clearStats() to empty the line chart, then loops through getStatistics(iterations) and adds the statistics to line chart
     * @param stats An array of statistic data elements
     */
    private void displayStatistics(int[][] stats){

        clearStats();

        int iterations = stats[0].length;

        for(int iteration = 0; iteration < iterations; iteration++){

            livingSeries.getData().add(new XYChart.Data<>(
                    (double)iteration, (double)stats[0][iteration]));

            growthSeries.getData().add(new XYChart.Data<>(
                    (double)iteration, (double)stats[1][iteration]));

            similaritySeries.getData().add(new XYChart.Data<>(
                    (double)iteration, (double)stats[2][iteration]));
        }
    }

    /**
     *  Called when GUI button "Show statistics" is clicked,
     *  or if the enter key is pressed while in the iteration text field.
     *  If the text in the text field is a number,
     *  displayStatistics(iterations) will show statistics for that number of iterations
     */
    @FXML
    public void onInputEntered() {

        String string = textField.getText();

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(string);

        if(m.matches()) {
            int iterations = Integer.parseInt(m.group());

            Task task = createTask(iterations);
            new Thread(task).start();
        }
        else {
            textField.setText("");
            textField.setPromptText("Could not parse number");
        }
    }

    private Task createTask(int iterations){

        return new Task() {
            @Override
            protected Object call() throws Exception {

                int[][] stats = getStatistics(iterations);

                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        displayStatistics(stats);
                    }
                });

                return true;
            }
        };
    }
}
