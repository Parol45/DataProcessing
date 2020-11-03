package DataProcessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

import java.util.*;

import static DataProcessing.MainApplication.*;

public class Analysis {

    public static void initAnalysis() {
        for (LineChart<Number, Number> graph : graphs) {
            graph.setOnMouseClicked((event) -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    getStatistics(graph);
                } else {
                    getStationary(graph);
                }
            });
        }
    }

    static double min(ObservableList<XYChart.Data<Number, Number>> data) {
        double min = data.get(0).getYValue().doubleValue();
        for (XYChart.Data<Number, Number> point : data) {
            if (min > point.getYValue().doubleValue()) {
                min = point.getYValue().doubleValue();
            }
        }
        return min;
    }

    static double max(ObservableList<XYChart.Data<Number, Number>> data) {
        double max = data.get(0).getYValue().doubleValue();
        for (XYChart.Data<Number, Number> point : data) {
            if (max < point.getYValue().doubleValue()) {
                max = point.getYValue().doubleValue();
            }
        }
        return max;
    }

    static double mean(ObservableList<XYChart.Data<Number, Number>> data) {
        double acc = 0;
        for (XYChart.Data<Number, Number> point : data) {
            acc += point.getYValue().doubleValue();
        }
        return acc / data.size();
    }

    static double dispersion(ObservableList<XYChart.Data<Number, Number>> data, double mean) {
        double acc = 0;
        for (XYChart.Data<Number, Number> point : data) {
            acc += Math.pow(point.getYValue().doubleValue() - mean, 2);
        }
        return acc / data.size();
    }

    static double rootMeanSquare(ObservableList<XYChart.Data<Number, Number>> data) {
        double acc = 0;
        for (XYChart.Data<Number, Number> point : data) {
            acc += Math.pow(point.getYValue().doubleValue(), 2);
        }
        return acc / data.size();
    }

    static double meanDeviation(double dispersion) {
        return Math.sqrt(dispersion);
    }

    static double asymmetry(ObservableList<XYChart.Data<Number, Number>> data) {
        double acc = 0;
        for (XYChart.Data<Number, Number> point : data) {
            acc += Math.pow(point.getYValue().doubleValue(), 3);
        }
        return acc / data.size();
    }

    static double excess(ObservableList<XYChart.Data<Number, Number>> data) {
        double acc = 0;
        for (XYChart.Data<Number, Number> point : data) {
            acc += Math.pow(point.getYValue().doubleValue(), 4);
        }
        return acc / data.size();
    }

    static void getStatistics(LineChart<Number, Number> graph) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData();
        double mean = mean(data),
                dispersion = dispersion(data, mean),
                meanDev = meanDeviation(dispersion);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Some statistics");
        alert.setContentText(
                "Min = " + min(data) +
                        "\nMax = " + max(data) +
                        "\nMean = " + mean +
                        "\nD = " + dispersion +
                        "\nrootMeanSq = " + rootMeanSquare(data) +
                        "\nMeanDev = " + meanDev +
                        "\nAsym = " + asymmetry(data) / Math.pow(meanDev, 3) +
                        "\nExcess = " + (excess(data) / Math.pow(meanDev, 4) - 3));
        alert.showAndWait();
    }

    static Double minInList(List<Double> list) {
        Double min = list.get(0);
        for (Double elem : list) {
            if (elem < min) {
                min = elem;
            }
        }
        return min;
    }

    static Double maxInList(List<Double> list) {
        Double max = list.get(0);
        for (Double elem : list) {
            if (elem > max) {
                max = elem;
            }
        }
        return max;
    }

    static void getStationary(LineChart<Number, Number> graph) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                range = FXCollections.observableArrayList();
        double min = min(data), max = max(data);
        int intervals = Integer.parseInt(N.getText()) / 10, k = 0, interval = 1;
        List<Double> means = new ArrayList<>(),
                dispersions = new ArrayList<>(),
                rootMeanSqs = new ArrayList<>(),
                meanDevs = new ArrayList<>();
        for (XYChart.Data<Number, Number> point : data) {
            range.add(point);
            k++;
            if (k > intervals * interval) {
                means.add(mean(range));
                dispersions.add(dispersion(range, means.get(interval - 1)));
                rootMeanSqs.add(rootMeanSquare(range));
                meanDevs.add(meanDeviation(dispersions.get(interval - 1)));
                range = FXCollections.observableArrayList();
                interval++;
            }
        }
        boolean meansStat = true,
                dispStat = true,
                rootStat = true,
                meanDevStat = true;
        double delta = 0.1;
        if (Math.abs(maxInList(means) - minInList(means)) > (max - min) * delta) {
            meansStat = false;
        }
        if (Math.abs(maxInList(dispersions) - minInList(dispersions)) > (max - min) * delta) {
            dispStat = false;
        }
        if (Math.abs(maxInList(rootMeanSqs) - minInList(rootMeanSqs)) > (max - min) * delta) {
            rootStat = false;
        }
        if (Math.abs(maxInList(meanDevs) - minInList(meanDevs)) > (max - min) * delta) {
            meanDevStat = false;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Stationary info");
        alert.setContentText("Mean - " + (meansStat ? "Yes" : "No") +
                "\nD - " + (dispStat ? "Yes" : "No") +
                "\nrootMeanSq - " + (rootStat ? "Yes" : "No") +
                "\nMeanDev - " + (meanDevStat ? "Yes" : "No"));
        alert.showAndWait();
    }

    static ObservableList<XYChart.Data<Number, Number>> autoCorrelation(LineChart<Number, Number> graph) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        double mean = mean(data),
                divider = 0;
        for (int i = 0; i < data.size(); i++) {
            divider += Math.pow(data.get(i).getYValue().doubleValue() - mean, 2);
        }

        for (int funShift = 0; funShift < data.size(); funShift++) {
            double accumulator = 0;
            for (int i = 0; i < data.size() - funShift; i++) {
                accumulator += (data.get(i).getYValue().doubleValue() - mean) *
                        (data.get(i + funShift).getYValue().doubleValue() - mean);
            }
            result.add(new XYChart.Data<>(data.get(funShift).getXValue(), accumulator / divider));
        }
        return result;
    }

    static ObservableList<XYChart.Data<Number, Number>> crossCorrelation(LineChart<Number, Number> graph1, LineChart<Number, Number> graph2) {
        ObservableList<XYChart.Data<Number, Number>> data1 = graph1.getData().get(0).getData(),
                data2 = graph2.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        double mean1 = mean(data1),
                mean2 = mean(data2),
                divider, acc1 = 0, acc2 = 0;
        for (int i = 0; i < data1.size(); i++) {
            acc1 += Math.pow(data1.get(i).getYValue().doubleValue() - mean1, 2);
            acc2 += Math.pow(data2.get(i).getYValue().doubleValue() - mean2, 2);
        }
        divider = Math.sqrt(acc1) * Math.sqrt(acc2);

        for (int funShift = 0; funShift < data1.size(); funShift++) {
            double accumulator = 0;
            for (int i = 0; i < data1.size() - funShift; i++) {
                accumulator += (data1.get(i).getYValue().doubleValue() - mean1) *
                        (data2.get(i + funShift).getYValue().doubleValue() - mean2);
            }
            result.add(new XYChart.Data<>(data1.get(funShift).getXValue(), accumulator / divider));
        }
        return result;
    }

    static ObservableList<XYChart.Data<Number, Number>> shift(LineChart<Number, Number> graph, double shift)  {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        for (int i = 0; i < data.size(); i++) {
            result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i).getYValue().doubleValue() + shift));
        }
        return result;
    }

    static ObservableList<XYChart.Data<Number, Number>> fixShift(LineChart<Number, Number> graph)  {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        double mean = mean(data);
        for (int i = 0; i < data.size(); i++) {
            result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i).getYValue().doubleValue() - mean));
        }
        return result;
    }

    static ObservableList<XYChart.Data<Number, Number>> ejaculation(LineChart<Number, Number> graph, int n) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        List<Integer> randoms = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            randoms.add((int)Math.floor(Math.random() * Integer.parseInt(N.getText())));
        }
        for (int i = 0; i < data.size(); i++) {
            if (randoms.contains(i)) {
                result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i).getYValue().doubleValue() * 100));
            } else {
                result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i).getYValue().doubleValue()));
            }
        }
        return result;
    }

    static ObservableList<XYChart.Data<Number, Number>> fixEjaculation(LineChart<Number, Number> graph) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        for (int i = 0; i < data.size(); i++) {
            double temp = data.get(i).getYValue().doubleValue();
            if (temp < 0 || temp > Integer.parseInt(N.getText())) {
                if (i != 0 && i != data.size() - 1) {
                    result.add(new XYChart.Data<>(data.get(i).getXValue(), (data.get(i - 1).getYValue().doubleValue() +
                            data.get(i + 1).getYValue().doubleValue())/2));
                } else {
                    if (i == 0) {
                        result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i + 1).getYValue().doubleValue()));
                    } else {
                        result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i - 1).getYValue().doubleValue()));
                    }
                }
            } else {
                result.add(new XYChart.Data<>(data.get(i).getXValue(), data.get(i).getYValue().doubleValue()));
            }
        }
        return result;
    }

    public static ObservableList<XYChart.Data<Number, Number>> spectre(LineChart<Number, Number> graph, double dt) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        double re = 0, im = 0, df = 1.0 / dt / Integer.parseInt(N.getText());
        for (int i = 0; i < data.size() / 2; i++) {
            for (int j = 0; j < data.size(); j++) {
                re += data.get(j).getYValue().doubleValue() *
                        Math.cos(2 * Math.PI * dt * i * data.get(j).getXValue().doubleValue() / Double.parseDouble(N.getText()));
                im += data.get(j).getYValue().doubleValue() *
                        Math.sin(2 * Math.PI * dt * i * data.get(j).getXValue().doubleValue() / Double.parseDouble(N.getText()));
            }
            re /= Double.parseDouble(N.getText());
            im /= Double.parseDouble(N.getText());
            result.add(new XYChart.Data<>(data.get(i).getXValue(), Math.sqrt(Math.pow(re, 2) + Math.pow(im, 2))));
        }
        return result;
    }

    public static ObservableList<XYChart.Data<Number, Number>> distribution(LineChart<Number, Number> graph) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                result = FXCollections.observableArrayList();
        Integer n = Integer.parseInt(N.getText());
        double[] f = new double[n];
        for (XYChart.Data<Number, Number> point : data) {
            f[(int)Math.floor(point.getYValue().doubleValue())] += 1;
        }
        for (int i = 0; i < f.length; i++) {
            result.add(new XYChart.Data<>(i, f[i] / n));
        }
        return result;
    }
}
