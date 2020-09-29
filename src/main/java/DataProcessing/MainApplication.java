package DataProcessing;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application {

    GridPane layout = new GridPane();
    static ArrayList<LineChart<Number, Number>> graphs = new ArrayList<>();
    List<TextField> inputs = Arrays.asList(
            new TextField(),
            new TextField(),
            new TextField(),
            new TextField(),
            new TextField());
    Button buttonLinear = new Button("Draw");
    Button buttonExp = new Button("Draw");

    public static Number linear(double t, double c, double d) {
        return c * t + d;
    }

    public static Number exp(double t, double a, double b) {
        return b * Math.exp(-a * t);
    }

    @Override
    public void start(Stage stage) {

        for (int i = 0; i < 4; i++) {
            LineChart<Number, Number> graph = new LineChart<>(new NumberAxis(),new NumberAxis());
            graph.setCreateSymbols(false);
            graph.setLegendVisible(false);
            graphs.add(graph);
            layout.add(graph, i % 2, i / 2);
        }
        VBox vbLinear = new VBox();
        VBox vbExp = new VBox();
        vbExp.getChildren().addAll(new Label("a:"), inputs.get(0), new Label("b:"), inputs.get(1), buttonExp);
        vbLinear.getChildren().addAll(new Label("c:"), inputs.get(2), new Label("d:"),
                inputs.get(3), buttonLinear, new Label("dt:"), inputs.get(4));
        layout.add(vbLinear, 2, 0);
        layout.add(vbExp, 2, 1);
        Scene scene = new Scene(layout, 950, 600);
        buttonLinear.setOnMouseClicked((event) -> {
            trend(0, 0.0, 0.0,
                    Double.parseDouble(inputs.get(2).getText()),
                    Double.parseDouble(inputs.get(3).getText()),
                    Double.parseDouble(inputs.get(4).getText()));
        });
        buttonExp.setOnMouseClicked((event) -> {
            trend(1,
                    Double.parseDouble(inputs.get(0).getText()),
                    Double.parseDouble(inputs.get(1).getText()),
                    0.0, 0.0,
                    Double.parseDouble(inputs.get(4).getText()));
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void trend(int type, Double a, Double b, Double c, Double d, Double dt) {
        LineChart<Number, Number> graph;
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        for (double t = 0; t < 1000 * dt; t+= dt) {
            if (type == 0) {
                data.add(new XYChart.Data<>(t, linear(t, c, d)));
            } else {
                data.add(new XYChart.Data<>(t, exp(t, a, b)));
            }
        }
        if (type == 0) {
            graph = graphs.get(type + (c > 0 ? 0 : 1));
        } else {
            graph = graphs.get(type + (a < 0 ? 1 : 2));
        }
        graph.getData().clear();
        graph.getData().add(new XYChart.Series<>(data));
    }


    public static void main(String[] args) {
        launch();
    }

}