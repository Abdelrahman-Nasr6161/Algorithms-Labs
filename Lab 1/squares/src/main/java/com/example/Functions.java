package com.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Functions {
    public static int findMinDistance(Point[] arr) {
        Point[] arrX = arr.clone();
        Arrays.sort(arrX, Functions::comparatorX);
        Point[] arrY = arr.clone();
        Arrays.sort(arrY, Functions::comparatorY);
        return minDistance(arrX, arrY, 0, arr.length - 1);
    }

    private static int minDistance(Point[] arrX, Point[] arrY, int left, int right) {
        if (right - left == 1)
            return getDistance(arrX[left], arrX[right]);
        else if (right - left == 2) {
            return Math.min(getDistance(arrX[left], arrX[left + 1]),
                    Math.min(getDistance(arrX[left], arrX[right]),
                            getDistance(arrX[left + 1], arrX[right])));
        }
        int mid = (left + right) / 2;
        int midX = arrX[mid].x;
        int midY = arrX[mid].y;
        ArrayList<Point> leftY = new ArrayList<>();
        ArrayList<Point> rightY = new ArrayList<>();
        for (int i = left ; i <=right ; i++)
        {
            if(arrY[i].x < midX)
                leftY.add(arrY[i]);
            else if (arrY[i].x > midX)
                rightY.add(arrY[i]);
            else
                if(arrY[i].y <= midY)
                    leftY.add(arrY[i]);
                else 
                    rightY.add(arrY[i]);
        }
        Point[] leftYarr = new Point[leftY.size()];
        Point[] rightYarr = new Point[rightY.size()];
        leftY.toArray(leftYarr);
        rightY.toArray(rightYarr);
        int dl = minDistance(arrX, leftYarr, left, mid);
        int dr = minDistance(arrX, rightYarr, mid + 1, right);
        int delta = Math.min(dl, dr);
        ArrayList<Point> strip = new ArrayList<>();
        for (int i = 0; i < arrY.length; i++) {
            if (arrY[i].x >= midX - delta && arrY[i].x <= midX + delta) {
                strip.add(arrY[i]);
            }
        }

        int minStrip = delta;
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < delta; j++) {
                int dist = getDistance(strip.get(i), strip.get(j));
                if (dist < minStrip) {
                    minStrip = dist;
                }
            }
        }

        return Math.min(delta, minStrip);
    }

    private static int getDistance(Point A, Point B) {
        return Math.max(Math.abs(A.x - B.x), Math.abs(A.y - B.y));
    }

    private static int comparatorX(Point A, Point B) {
        int cmp = Integer.compare(A.x, B.x);
        if (cmp == 0) {
            return Integer.compare(A.y, B.y);
        }
        return cmp;
    }

    private static int comparatorY(Point A, Point B) {
        return Integer.compare(A.y, B.y);
    }
}
