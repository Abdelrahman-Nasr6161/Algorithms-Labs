package com.example;

import java.util.Arrays;

public class MedianOfMedians {
    public static int findMedian(int[] arr, int left, int right) {
        int length = right - left ;
        int k = left + length / 2;
        return quickSelect(arr, left, right, k);
    }

    private static int quickSelect(int[] arr, int left, int right, int k) {
        if (left == right)
            return arr[left];
        int pivotValue = selectPivot(arr, left, right);
        int pivotIndex = partition(arr, left, right, pivotValue);

        if (k == pivotIndex)
            return arr[pivotIndex];
        else if (k < pivotIndex)
            return quickSelect(arr, left, pivotIndex - 1, k);
        else
            return quickSelect(arr, pivotIndex + 1, right, k);
    }

    private static int selectPivot(int[] arr, int left, int right) {
        int n = right - left + 1;
        if (n <= 5) {
            Arrays.sort(arr, left, right + 1);
            return arr[left + n / 2];
        }
        int numMedians = 0;
        for (int i = left; i <= right; i += 5) {
            int subRight = Math.min(i + 4, right);
            Arrays.sort(arr, i, subRight + 1);
            int medianIndex = i + (subRight - i) / 2;
            swap(arr, left + numMedians, medianIndex);
            numMedians++;
        }
        return selectPivot(arr, left, left + numMedians - 1);
    }

    private static int partition(int[] arr, int left, int right, int pivotValue) {
        int pivotIndex = -1;
        for (int i = left; i <= right; i++) {
            if (arr[i] == pivotValue) {
                pivotIndex = i;
                break;
            }
        }
        swap(arr, pivotIndex, right);
        int storeIndex = left;
        for (int i = left; i <= right; i++) {
            if (arr[i] < pivotValue) {
                swap(arr, storeIndex, i);
                storeIndex++;
            }
        }
        swap(arr, storeIndex, right);
        return storeIndex;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
