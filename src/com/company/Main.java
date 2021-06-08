package com.company;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        List<Integer> numberOfSegmentations = new ArrayList<>(Arrays.asList(100, 1000, 10000, 100000));
        List<Integer> numberOfTrajectories = new ArrayList<>(Arrays.asList(100, 1000, 10000, 100000));

        NormalDistribution normalDistribution = new NormalDistribution(0,1);

        try(PrintWriter ex2 = new PrintWriter("ex2_results.txt")){
            Double from = 0.0;
            Double to = 1.0;
            for(Integer segNum : numberOfSegmentations){
                for(Integer trajNum : numberOfTrajectories){
                    Double result = calcExercise2(segNum, trajNum, normalDistribution, from, to);
                    String toWrite = "number of segmentation: " + segNum.toString() +
                            " number of trajectories: " + trajNum.toString() +
                            " result: " + result +
                            " error: " + Math.abs((to-from)-result);

                    ex2.println(toWrite);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        List<Double> experimentTimes = new ArrayList<>(Arrays.asList(1.0, 5.0, 10.0));
        try(PrintWriter ex3 = new PrintWriter("ex3_results.txt")){
            for(Integer segNum : numberOfSegmentations){
                for(Integer trajNum : numberOfTrajectories){
                    for(Double time: experimentTimes){
                        Double result = calcExercise3(segNum, trajNum, time, normalDistribution);
                        String toWrite = "number of segmentation: " + segNum.toString() +
                                " number of trajectories: " + trajNum.toString() +
                                " experiment time: " + time.toString() +
                                " result: " + result +
                                " error: " + Math.abs(0.0-result);

                        ex3.println(toWrite);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Double calcExercise2(Integer segmentationNumber, Integer trajectoriesNumber, NormalDistribution nd,
                                        Double from, Double to){
        Double sumOfsums = 0.0;
        for(int i=0; i<trajectoriesNumber; i++){
            Double sum = 0.0;
            for(int j=0; j<segmentationNumber; j++){
                sum += Math.pow(nd.sample(), 2);
            }
            sumOfsums += sum;
        }
        Double expectation = sumOfsums / trajectoriesNumber; //expected value approximation
        return expectation * (to-from)/segmentationNumber; //times dt;
    }

    public static Double calcExercise3(Integer segmentationNumber, Integer trajectoriesNumber, Double T, NormalDistribution nd){
        List<Double> segmentationOf0toT = splitRangeRandom(0.0, T, segmentationNumber, new Random());

        Double sumOfIntegrals = 0.0;
        for(int i=0; i<trajectoriesNumber; i++){
            List<Double> wienerProcessValues = wienerProcessValues(segmentationOf0toT, nd);
            Double result = 0.0;

            for(int j=1; j<wienerProcessValues.size(); j++){
                result += wienerProcessValues.get(j) * (segmentationOf0toT.get(j) - segmentationOf0toT.get(j-1));
            }
            sumOfIntegrals += result;
        }
        return sumOfIntegrals/trajectoriesNumber;
    }

    public static List<Double> splitRangeRandom (Double from, Double to, Integer numberOfChunks, Random random){
        Double rangeLength = to-from;
        List<Double> chunks = new ArrayList<>();

        chunks.add(from);
        for(int i=0; i<numberOfChunks-2; i++){
            chunks.add(random.nextDouble() * rangeLength);
        }
        chunks.add(to);
        return chunks.stream()
                .distinct() // returned list may have less than "numberOfChunks" elements, but it is not important for exercise result
                .sorted().collect(Collectors.toList());
    }

    public static List<Double> wienerProcessValues(List<Double> segmentationOf0toT, NormalDistribution nd){
        List<Double> wienerProcessValues = new ArrayList<>();
        wienerProcessValues.add(0.0);

        for(int i=1; i<segmentationOf0toT.size(); i++){
            wienerProcessValues.add(
                    wienerProcessValues.get(i-1)
                    + (nd.sample() * Math.sqrt(segmentationOf0toT.get(i) - segmentationOf0toT.get(i-1)))
            );
        }
        return wienerProcessValues;
    }
}
