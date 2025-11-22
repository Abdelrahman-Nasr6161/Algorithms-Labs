package com.example;

import java.util.Arrays;

public class SortMedian {
    public static int findMedian(int[] arr , int left , int right)
    {
        Arrays.sort(arr,left,right+1);
        int length = right - left;
        return arr[left + length /2];
    }
}
