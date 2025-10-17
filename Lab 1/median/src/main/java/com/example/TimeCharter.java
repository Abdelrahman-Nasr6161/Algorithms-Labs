package com.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Random;

public class TimeCharter extends JFrame {

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

        int[] sizes = {1000, 5000, 10000, 20000, 50000, 100000, 200000, 500000,1_000_000,2_000_000,5_000_000,10_000_000};

        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = rand.nextInt(1_000_000);

            // --- Random Median ---
            int[] copy1 = arr.clone();
            long t1 = System.nanoTime();
            RandomMedian.findMedian(copy1, 0, copy1.length - 1);
            long t2 = System.nanoTime();
            dataset.addValue((t2 - t1) / 1_000_000.0, "Randomized Quickselect", String.valueOf(n));

            // --- Median of Medians ---
            int[] copy2 = arr.clone();
            long t3 = System.nanoTime();
            MedianOfMedians.findMedian(copy2, 0, copy2.length - 1);
            long t4 = System.nanoTime();
            dataset.addValue((t4 - t3) / 1_000_000.0, "Median of Medians", String.valueOf(n));

            // --- Sort Median ---
            int[] copy3 = arr.clone();
            long t5 = System.nanoTime();
            SortMedian.findMedian(copy3, 0, copy3.length - 1);
            long t6 = System.nanoTime();
            dataset.addValue((t6 - t5) / 1_000_000.0, "Sorting", String.valueOf(n));
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
