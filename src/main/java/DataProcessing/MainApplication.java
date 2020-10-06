package DataProcessing;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static DataProcessing.Analysis.initAnalysis;
import static DataProcessing.Model.*;

public class MainApplication extends Application {

    public static TextField N = new TextField("1000");

    public static ArrayList<LineChart<Number, Number>> graphs = new ArrayList<>();
    GridPane layout = new GridPane();
    List<TextField> inputs = Arrays.asList(
            new TextField("1"),
            new TextField("1"),
            new TextField("1"),
            new TextField("1"),
            new TextField("0.1"));
    Button buttonLinear = new Button("Draw");
    Button buttonExp = new Button("Draw");
    Button buttonRandom = new Button("Rand");
    Button buttonMyRandom = new Button("My rand");
    Button buttonDistribution = new Button("Draw distr");

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(layout, 950, 600);
        initModelGraphs(layout, inputs, buttonLinear, buttonExp, buttonRandom, buttonMyRandom);
        initAnalysis(buttonDistribution);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}