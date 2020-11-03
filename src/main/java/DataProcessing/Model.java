package DataProcessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import static DataProcessing.MainApplication.*;

import java.util.List;

public class Model {

    // random - using a linear congruential formula;
    //

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
                                       Button buttonRandom, Button buttonMyRandom, Button buttonAutoCorrelation,
                                       Button buttonCrossCorrelation, Button buttonShift, Button buttonFixShift,
                                       Button buttonEjaculation, Button buttonFixEjaculation,
                                       Button buttonHarmonic, Button buttonPolyHarmonic, Button buttonSpectre,
                                       Button buttonDistr) {
        for (int i = 0; i < 4; i++) {
            LineChart<Number, Number> graph = new LineChart<>(new NumberAxis(), new NumberAxis());
            graph.setCreateSymbols(false);
            graph.setLegendVisible(false);
            graphs.add(graph);
            layout.add(graph, i % 2, i / 2);
        }
        VBox vbLinear = new VBox();
        VBox vbExp = new VBox();

        Menu m = new Menu("Options");

        MenuItem m1 = new MenuItem("Draw linear");
        MenuItem m2 = new MenuItem("Draw exp");
        MenuItem m3 = new MenuItem("menu item 3");

        m.getItems().add(m1);
        m.getItems().add(m2);
        m.getItems().add(m3);

        MenuBar mb = new MenuBar();

        mb.getMenus().add(m);
        VBox vb = new VBox(mb);

        vbExp.getChildren().addAll(new Label("N:"), N, new Label("a:"), inputs.get(0),
                new Label("b:"), inputs.get(1), inputs.get(3), new Label("from:"),
                inputs.get(5), new Label("to:"), inputs.get(6), buttonAutoCorrelation, buttonCrossCorrelation,
                buttonEjaculation, buttonFixEjaculation, buttonSpectre, buttonDistr);
        vbLinear.getChildren().addAll(buttonRandom, buttonMyRandom, new Label("c:"), inputs.get(2),
                new Label("d:"), inputs.get(3), new Label("dt:"), inputs.get(4),
                new Label("shift:"), inputs.get(7), buttonShift, buttonFixShift,
                buttonHarmonic, buttonPolyHarmonic);
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

        buttonAutoCorrelation.setOnMouseClicked((event) -> autoCorrelation(Integer.parseInt(inputs.get(5).getText()),
                Integer.parseInt(inputs.get(6).getText())));
        buttonCrossCorrelation.setOnMouseClicked((event) -> crossCorrelation(0, 1, Integer.parseInt(inputs.get(6).getText())));

        buttonShift.setOnMouseClicked((event) -> shift(Integer.parseInt(inputs.get(5).getText()), Integer.parseInt(inputs.get(7).getText())));
        buttonFixShift.setOnMouseClicked((event) -> fixShift(Integer.parseInt(inputs.get(5).getText())));

        buttonEjaculation.setOnMouseClicked((event) -> ejaculation(Integer.parseInt(inputs.get(5).getText()), Integer.parseInt(inputs.get(6).getText())));
        buttonFixEjaculation.setOnMouseClicked((event) -> fixEjaculation(Integer.parseInt(inputs.get(5).getText())));

        buttonHarmonic.setOnMouseClicked((event) -> harmonic(Integer.parseInt(inputs.get(5).getText()),
                Double.parseDouble(inputs.get(0).getText()),
                Double.parseDouble(inputs.get(1).getText()),
                Double.parseDouble(inputs.get(4).getText())));
        buttonPolyHarmonic.setOnMouseClicked((event) -> polyHarmonic(Integer.parseInt(inputs.get(5).getText()),
                Double.parseDouble(inputs.get(0).getText()),
                Double.parseDouble(inputs.get(1).getText()),
                Double.parseDouble(inputs.get(2).getText()),
                Double.parseDouble(inputs.get(3).getText()),
                Double.parseDouble(inputs.get(4).getText())));

        buttonSpectre.setOnMouseClicked((event) -> spectre(Integer.parseInt(inputs.get(5).getText()),
                Integer.parseInt(inputs.get(6).getText()),
                Double.parseDouble(inputs.get(4).getText())));

        buttonDistr.setOnMouseClicked((event) -> distribution(Integer.parseInt(inputs.get(5).getText()),
                Integer.parseInt(inputs.get(6).getText())));
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

    public static void harmonic(int type0, Double a, Double b, double dt) {
        LineChart<Number, Number> graph;
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        double bound = Integer.parseInt(N.getText()) * dt;
        for (double t = 0; t < bound; t += dt) {
            data.add(new XYChart.Data<>(t, a * Math.sin(2 * Math.PI * t * b)));
        }
        graph = graphs.get(type0);
        graph.getData().clear();
        graph.getData().add(new XYChart.Series<>(data));
    }

    public static void polyHarmonic(int type0, Double a, Double b, Double c, Double d, Double dt) {
        LineChart<Number, Number> graph;
        ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
        double bound = Integer.parseInt(N.getText()) * dt;
        for (double t = 0; t < bound; t += dt) {
            data.add(new XYChart.Data<>(t, a * Math.sin(2 * Math.PI * t * b + c) +
                    c * Math.cos(2 * Math.PI * t * d + a) +
                    c * Math.sin(2 * Math.PI * t * b + d)));
        }
        graph = graphs.get(type0);
        graph.getData().clear();
        graph.getData().add(new XYChart.Series<>(data));
    }

    public static void autoCorrelation(int type0, int type1) {
        LineChart<Number, Number> from = graphs.get(type0),
                to = graphs.get(type1);
        to.getData().clear();
        to.getData().add(new XYChart.Series<>(Analysis.autoCorrelation(from)));
    }

    public static void crossCorrelation(int type0, int type1, int type2) {
        LineChart<Number, Number> from1 = graphs.get(type0),
                from2 = graphs.get(type1),
                to = graphs.get(type2);
        to.getData().clear();
        to.getData().add(new XYChart.Series<>(Analysis.crossCorrelation(from1, from2)));
    }

    public static void shift(int type0, int shift) {
        LineChart<Number, Number> from = graphs.get(type0);
        ObservableList<XYChart.Data<Number, Number>> r = Analysis.shift(from, shift);
        from.getData().clear();
        from.getData().add(new XYChart.Series<>(r));
    }

    public static void fixShift(int type0) {
        LineChart<Number, Number> from = graphs.get(type0);
        ObservableList<XYChart.Data<Number, Number>> r = Analysis.fixShift(from);
        from.getData().clear();
        from.getData().add(new XYChart.Series<>(r));
    }

    public static void ejaculation(int type0, int N) {
        LineChart<Number, Number> from = graphs.get(type0);
        ObservableList<XYChart.Data<Number, Number>> r = Analysis.ejaculation(from, N);
        from.getData().clear();
        from.getData().add(new XYChart.Series<>(r));
    }

    public static void fixEjaculation(int type0) {
        LineChart<Number, Number> from = graphs.get(type0);
        ObservableList<XYChart.Data<Number, Number>> r = Analysis.fixEjaculation(from);
        from.getData().clear();
        from.getData().add(new XYChart.Series<>(r));
    }

    public static void spectre(int type0, int type1, Double dt) {
        LineChart<Number, Number> from = graphs.get(type0),
                to = graphs.get(type1);
        to.getData().clear();
        to.getData().add(new XYChart.Series<>(Analysis.spectre(from, dt)));
    }

    public static void distribution(int type0, int type1) {
        LineChart<Number, Number> from = graphs.get(type0),
                to = graphs.get(type1);
        to.getData().clear();
        to.getData().add(new XYChart.Series<>(Analysis.distribution(from)));
    }
}
