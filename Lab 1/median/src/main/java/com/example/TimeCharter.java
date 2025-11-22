package com.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Random;

public class TimeCharter extends JFrame {

    private static final int RUNS = 10;

    public TimeCharter(String title) {
        super(title);

        DefaultCategoryDataset dataset = runBenchmarks();

        JFreeChart chart = ChartFactory.createLineChart(
                "Median Algorithm Benchmark",
                "Array Size",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private DefaultCategoryDataset runBenchmarks() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Random rand = new Random();

        int[] sizes = {
                1000, 5000, 10000, 20000, 50000, 100000, 200000,
                500000, 1_000_000, 2_000_000, 5_000_000, 10_000_000
        };

        for (int n : sizes) {
            System.out.println("Running benchmarks for size: " + n);

            // Generate the base array once per size
            int[] baseArray = new int[n];
            for (int i = 0; i < n; i++) baseArray[i] = rand.nextInt(1_000_000);

            // --- Randomized Quickselect ---
            double totalRandom = 0;
            for (int r = 0; r < RUNS; r++) {
                int[] arr = baseArray.clone();
                long start = System.nanoTime();
                RandomMedian.findMedian(arr, 0, arr.length - 1);
                long end = System.nanoTime();
                totalRandom += (end - start) / 1_000_000.0;
            }
            double avgRandom = totalRandom / RUNS;
            dataset.addValue(avgRandom, "Randomized Quickselect", String.valueOf(n));

            // --- Median of Medians ---
            double totalMoM = 0;
            for (int r = 0; r < RUNS; r++) {
                int[] arr = baseArray.clone();
                long start = System.nanoTime();
                MedianOfMedians.findMedian(arr, 0, arr.length - 1);
                long end = System.nanoTime();
                totalMoM += (end - start) / 1_000_000.0;
            }
            double avgMoM = totalMoM / RUNS;
            dataset.addValue(avgMoM, "Median of Medians", String.valueOf(n));

            // --- Sorting ---
            double totalSort = 0;
            for (int r = 0; r < RUNS; r++) {
                int[] arr = baseArray.clone();
                long start = System.nanoTime();
                SortMedian.findMedian(arr, 0, arr.length - 1);
                long end = System.nanoTime();
                totalSort += (end - start) / 1_000_000.0;
            }
            double avgSort = totalSort / RUNS;
            dataset.addValue(avgSort, "Sorting", String.valueOf(n));
        }

        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TimeCharter chart = new TimeCharter("Median Algorithms Benchmark");
            chart.setSize(900, 600);
            chart.setLocationRelativeTo(null);
            chart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            chart.setVisible(true);
        });
    }
}
