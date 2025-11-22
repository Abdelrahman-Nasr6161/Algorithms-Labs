package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Ensure input file path is provided
        if (args.length < 1) {
            System.err.println("Usage: java -jar program.jar <input-file-path>");
            return;
        }

        String inputPath = args[0];
        File inputFile = new File(inputPath);
        String parentDir = inputFile.getParent();
        String outputPath = parentDir + File.separator + "output.txt";

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputPath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                int n = Integer.parseInt(line);
                Point[] arr = new Point[n];

                for (int i = 0; i < n; i++) {
                    String[] parts = br.readLine().trim().split("\\s+");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    arr[i] = new Point(x, y);
                }

                int result = Functions.findMinDistance(arr);
                bw.write(String.valueOf(result));
                bw.newLine();
            }

            System.out.println("Output written to: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
