package DataProcessing;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
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

    static void getStationary(LineChart<Number, Number> graph) {
        ObservableList<XYChart.Data<Number, Number>> data = graph.getData().get(0).getData(),
                range = FXCollections.observableArrayList();
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
        for (int i = 1; i < means.size(); i++) {
            if (Math.abs(means.get(i - 1) - means.get(i)) > means.get(i) * 0.01) {
                meansStat = false;
            }
            if (Math.abs(dispersions.get(i - 1) - dispersions.get(i)) > dispersions.get(i) * 0.01) {
                dispStat = false;
            }
            if (Math.abs(rootMeanSqs.get(i - 1) - rootMeanSqs.get(i)) > rootMeanSqs.get(i) * 0.01) {
                rootStat = false;
            }
            if (Math.abs(meanDevs.get(i - 1) - meanDevs.get(i)) > meanDevs.get(i) * 0.01) {
                meanDevStat = false;
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Stationary info");
        alert.setContentText("Mean - " + (meansStat ? "Yes" : "No") +
                "\nD - " + (dispStat ? "Yes" : "No")  +
                "\nrootMeanSq - " + (rootStat ? "Yes" : "No")  +
                "\nMeanDev - " + (meanDevStat ? "Yes" : "No") );
        alert.showAndWait();
    }
}
