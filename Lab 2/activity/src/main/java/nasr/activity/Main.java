package nasr.activity;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import lombok.Getter;

public class Main {
    private final static int STUDENT_ID = 22010887; 
    @Getter
    static class Activity implements Comparable<Activity>{
        int start , end , weight;
        Activity(int s , int e , int w){
            this.start = s;
            this.end = e;
            this.weight = w;
        }
        @Override
        public int compareTo(Activity other){
            return Integer.compare(this.end, other.end);
        }
    }
    public static void main(String[] args) {
        if (args.length!=1){
            System.err.println("Please enter absolute path of input file");
        }
        String inputPath = args[0];
        File inputFile = new File(inputPath);
        if(!inputFile.exists()){
            System.err.println("input file does not exist: "+inputPath);
            return;
        }
        try {
            List<Activity> activities = readInput(inputFile);

            int result = weightedActivitySelection(activities);
            File outputFile = makeOutputFile(inputFile);
            writeOutput(outputFile, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static List<Activity> readInput(File file){
        List<Activity> activities = new ArrayList<>();

        try (Scanner sc = new Scanner(file)) {
            int n = sc.nextInt();
            for (int i = 0; i < n ; i++)
            {
                int s = sc.nextInt();
                int e = sc.nextInt();
                int w = sc.nextInt();
                activities.add(new Activity(s, e, w));
            }
        } catch (Exception e) {
            System.err.println("Error occured while reading file");
        }
        return activities;
    }
    
    private static File makeOutputFile(File inputFile) {
        String name = inputFile.getName();
        int dot = name.lastIndexOf('.');
        String base = (dot == -1) ? name : name.substring(0, dot);

        String outputName = base + "_" + STUDENT_ID + ".out";

        return new File(inputFile.getParentFile(), outputName);
    }

    private static void writeOutput(File outFile, int result) throws Exception {
        try (PrintWriter pw = new PrintWriter(outFile)) {
            pw.println(result);
        }
    }
    private static int weightedActivitySelection(List<Activity> activities){
        Collections.sort(activities);
        int n = activities.size();
        int[] dp = new int[n];
        int[] p = computeP(activities);

        dp[0] = activities.get(0).weight;
        for (int i = 1 ; i < n ; i++){
            int include = activities.get(i).getWeight();
            if(p[i]!=-1)
                include+=dp[p[i]];
            int exclude = dp[i-1];
            dp[i] = Math.max(include, exclude);
        }
        return dp[n-1];
    }
    private static int[] computeP(List<Activity> activities){
        int n = activities.size();
        int[] p = new int[n];
        int[] endTimes = activities.stream().mapToInt(Activity::getEnd).toArray();
        for (int i = 0 ; i < activities.size() ; i++)
        {
            int start = activities.get(i).getStart();
            p[i] = binarySearchLastCompatible(endTimes,start);
        }
        return p;
    }
    private static int binarySearchLastCompatible(int[] endTimes, int start){
        int lo=0,hi=endTimes.length-1;
        int ans =-1;
        while (lo<=hi) {
            int mid = (lo+hi)/2;
            if(endTimes[mid] <= start){
                ans = mid;
                lo = mid+1;
            } else{
                hi = mid-1;
            }
        }
        return ans;
    }
}