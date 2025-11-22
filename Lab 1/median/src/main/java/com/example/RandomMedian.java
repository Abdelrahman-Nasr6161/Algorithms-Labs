package com.example;

import java.util.Random;

public class RandomMedian {
    private static final Random rand = new Random();
    public static int findMedian(int[] arr , int left , int right)
    {
        int length = right - left;
        int k = left + length/2 ;
        return quickSelect(arr,left,right,k);
    }
    private static int quickSelect(int[] arr , int left , int right , int k)
    {
        if (left == right)
            return arr[left];
        int pivotIndex = randomPivot(arr, left , right , k);
        pivotIndex = partition(arr, left, right, pivotIndex);

        if (k==pivotIndex)
            return arr[k];
        else if(pivotIndex > k)
            return quickSelect(arr, left, pivotIndex - 1, k);
        else
            return quickSelect(arr, pivotIndex + 1, right, k);
    }
    public static int randomPivot(int[] arr, int left , int right , int offset)
    {
        return rand.nextInt(left,right+1);
    }
    private static int partition(int[] arr , int left , int right , int pivotIndex)
    {
        int pivotElement = arr[pivotIndex];
        swap(arr,pivotIndex,right);
        int storeIndex = left;
        for(int i = left ; i < right ; i++)
        {
            if(arr[i] < pivotElement)
            {
                swap(arr, storeIndex, i);
                storeIndex++;
            }
        }
        swap(arr, storeIndex, right);
        return storeIndex;
    }
    private static void swap(int[] arr , int i , int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
