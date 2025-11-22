package com.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner nasr = new Scanner(System.in);
        System.err.print("Enter the number of elements : ");
        int n = nasr.nextInt();
        int arr[] = new int[n];
        System.out.println("Enter elements");
        for(int i = 0 ; i < n ; i++)
        {
            arr[i] = nasr.nextInt();
        }
        System.out.println("1. Randomized Median");
        System.out.println("2. Median of Medians Median");
        System.out.println("3. Sort Median");
        System.out.print("Pick an algorithm : ");
        int choice = nasr.nextInt();
        long startTime = System.nanoTime();
        int median = -1;
        
        switch (choice) {
            case 1:
                median = RandomMedian.findMedian(arr.clone(), 0, arr.length - 1);
                break;
            case 2:
                median = MedianOfMedians.findMedian(arr.clone(), 0, arr.length - 1);
                break;
            case 3:
                median = SortMedian.findMedian(arr.clone(), 0, arr.length - 1);
                break;
            default:
                System.out.println("Invalid choice");
                return;
        }
        long endTime = System.nanoTime();
        double elapsedMillis = (endTime - startTime) / 1_000_000.0;

        System.out.println("Median = " + median);
        System.out.println("Execution time = " + elapsedMillis + " ms");
    }
}
