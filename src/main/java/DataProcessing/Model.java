package DataProcessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import static DataProcessing.MainApplication.*;

import java.util.List;

public class Model {


    public static Number linear(double t, double c, double d) {
        return c * t + d;
    }

    public static Number exp(double t, double a, double b) {
        return b * Math.exp(-a * t);
    }

    public static Number myRandom() {
        return (813 ^ System.nanoTime()) % 1000;
    }

    public static void initModelGraphs(GridPane layout, List<TextField> inputs, Button buttonLinear, Button buttonExp,
                                       Button buttonRandom, Button buttonMyRandom) {
        for (int i = 0; i < 4; i++) {
            LineChart<Number, Number> graph = new LineChart<>(new NumberAxis(), new NumberAxis());
            graph.setCreateSymbols(false);
            graph.setLegendVisible(false);
            graphs.add(graph);
            layout.add(graph, i % 2, i / 2);
        }
        VBox vbLinear = new VBox();
        VBox vbExp = new VBox();
        vbExp.getChildren().addAll(new Label("N:"), N, new Label("a:"), inputs.get(0),
                new Label("b:"), inputs.get(1), buttonExp);
        vbLinear.getChildren().addAll(buttonRandom, buttonMyRandom, new Label("c:"), inputs.get(2),
                new Label("d:"), inputs.get(3), buttonLinear, new Label("dt:"), inputs.get(4));
        layout.add(vbLinear, 2, 0);
        layout.add(vbExp, 2, 1);
        buttonLinear.setOnMouseClicked((event) -> trend(0, 0.0, 0.0,
                Double.parseDouble(inputs.get(2).getText()),
                Double.parseDouble(inputs.get(3).getText()),
                Double.parseDouble(inputs.get(4).getText())));
        buttonExp.setOnMouseClicked((event) -> trend(1,
                Double.parseDouble(inputs.get(0).getText()),
                Double.parseDouble(inputs.get(1).getText()),
                0.0, 0.0,
                Double.parseDouble(inputs.get(4).getText())));
        buttonRandom.setOnMouseClicked((event) -> random(0, Double.parseDouble(inputs.get(4).getText())));
        buttonMyRandom.setOnMouseClicked((event) -> random(1, Double.parseDouble(inputs.get(4).getText())));
    }

    public static void trend(int type, Double a, Double b, Double c, Double d, Double dt) {
        LineChart<Number, Number> graph;
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        for (double t = 0; t < Integer.parseInt(N.getText()) * dt; t += dt) {
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

    public static void random(int type, double dt) {
        LineChart<Number, Number> graph;
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        for (double t = 0; t < Integer.parseInt(N.getText()) * dt; t += dt) {
            if (type == 0) {
                data.add(new XYChart.Data<>(t, Math.random() * Integer.parseInt(N.getText())));
            } else {
                data.add(new XYChart.Data<>(t, myRandom()));
            }
        }
        graph = graphs.get(type);
        graph.getData().clear();
        graph.getData().add(new XYChart.Series<>(data));
    }
}
