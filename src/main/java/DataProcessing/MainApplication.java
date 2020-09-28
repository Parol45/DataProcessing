package DataProcessing;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MainApplication extends Application {

    GridPane layout = new GridPane();
    ArrayList<LineChart<Number, Number>> graphs = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        for (int i = 0; i < 4; i++) {
            LineChart<Number, Number> graph = new LineChart<>(new NumberAxis(),new NumberAxis());
            graph.setCreateSymbols(false);
            graphs.add(graph);
            layout.add(graph, i % 2, i / 2);
        }
        Scene scene = new Scene(layout, 800, 800);
        trend(0, 0, 0, 0, 0, 0);
        stage.setScene(scene);
        stage.show();
    }

    public void trend(int type, int a, int b, int c, int d, double dt) {
        LineChart<Number, Number> graph = graphs.get(type);
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        for (int i = 0; i < 100; i++) {
            data.add(new XYChart.Data<>(i, i));
        }
        graph.getData().add(new XYChart.Series<>(data));
    }


    public static void main(String[] args) {
        launch();
    }

}