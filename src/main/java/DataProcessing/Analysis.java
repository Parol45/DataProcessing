package DataProcessing;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

import java.util.*;

import static DataProcessing.MainApplication.*;

public class Analysis {

    public static void initAnalysis(Button buttonDistribution) {
        for (LineChart<Number, Number> graph : graphs) {
            graph.setOnMouseClicked((event) -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    getStatistics(graph);
                } else {
                    getStationary(graph);
                }
            });
        }
        buttonDistribution.setOnMouseClicked((event) -> distribution(graphs));
    }

    public static void distribution(ArrayList<LineChart<Number, Number>> graphs) {
        ArrayList<Integer> distr1 = new ArrayList<>(),
                distr2 = new ArrayList<>();
        ObservableList<XYChart.Data<Number, Number>> data1 = graphs.get(0).getData().get(0).getData(),
                data2 = graphs.get(1).getData().get(0).getData();
        for (XYChart.Data<Number, Number> point : data1) {
            distr1.get((int) (Math.floor(point.getYValue().doubleValue())));
        }
        for (XYChart.Data<Number, Number> point : data2) {
            distr2.get((int) (Math.floor(point.getYValue().doubleValue())));
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
}
