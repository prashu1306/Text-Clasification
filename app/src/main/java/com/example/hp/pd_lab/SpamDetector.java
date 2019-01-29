package com.example.hp.pd_lab;

import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import android.widget.Toast;

/**
 * Machine-learning spam detector using Bayesian Algorithm
 *
 * @author raju rama krishna
 *
 */
public class SpamDetector {

    int spamCount = 0;
    int hamCount = 0;
    Map<String, Node> map = new HashMap<String, Node>();
    List<String> commonWords = new ArrayList<String>();

    public void init() throws Exception {
        BufferedReader br = new BufferedReader( new FileReader( new File("E:\\common-words.txt")));
        String line = br.readLine();
        while(line != null) {
            commonWords.add(line);
            line = br.readLine();
        }

        br = new BufferedReader( new FileReader( new File("E:\\spam.txt")));
        line = br.readLine();
        while(line != null) {
            line = line.toLowerCase();
            String[] words = line.split(" ");
            for( String s: words ) {
                if(s.length() > 3 && !commonWords.contains(s)) {
                    spamCount++;
                    if( map.containsKey(s)) {
                        map.get(s).spamCount++;
                    } else {
                        map.put(s, new Node( 1, 0));
                    }
                }
            }
            line = br.readLine();
        }

        br = new BufferedReader( new FileReader( new File("E:\\ham.txt")));
        line = br.readLine();
        while(line != null) {
            line = line.toLowerCase();
            String[] words = line.split(" ");
            for( String s: words ) {
                if(s.length() > 3 && !commonWords.contains(s)) {
                    hamCount++;
                    if( map.containsKey(s)) {
                        map.get(s).hamCount++;
                    } else {
                        map.put(s, new Node( 0, 1));
                    }
                }
            }
            line = br.readLine();
        }


        Set<String> keys = map.keySet();
        for( String key: keys ) {
            Node node = map.get(key);
            double res = ((node.spamCount)/(double)(spamCount))/(double)(((node.spamCount)/(double)(spamCount)) + (node.hamCount)/(double)(hamCount));
            node.probability = res;
        }

        br.close();
    }

    public void detect( String s ) {
        boolean result = false;
        s = s.toLowerCase();
        String[] sArr = s.split(" ");
        TreeMap<Double, List<Double>> interestMap = new TreeMap<Double, List<Double>>(Collections.reverseOrder());
        for( String x: sArr ) {
            if(x.length()> 3 && !commonWords.contains(x)) {
                double i = 0.5;
                double p = 0.5;
                if(map.containsKey(x)) {
                    p = map.get(x).probability;
                }
                i = Math.abs(i - p);
                if( !interestMap.containsKey(i) ) {
                    List<Double> values = new ArrayList<Double>();
                    values.add(p);
                    interestMap.put(i, values);
                } else {
                    interestMap.get(i).add(p);
                }
            }
        }

        List<Double> probabilities = new ArrayList<Double>();
        int count = 0;
        Set<Double> set = interestMap.keySet();
        for( Double d: set ) {
            List<Double> list = interestMap.get(d);
            for(Double x: list) {
                count++;
                probabilities.add(x);
                if(count == 200) {
                    break;
                }
            }
            if(count == 200) {
                break;
            }
        }

        double res = 1;
        double numerator = 1;
        double denominator = 1;
        for( Double d: probabilities ) {
            numerator = numerator * d;
            denominator = denominator * (1- d);
        }
        res = numerator/(double)(numerator +denominator);
        if(res >= 0.8) {

            result = true;
        }

        if( result ) {
            //System.out.println("'" +s+ "' is spam");
            //Spam_fragment.arrayAdapter3.add(s);
        } else {
            //System.out.println("'" +s+ "' is not a spam");
            //primary_fragment.arrayAdapter2.add(s);
        }
    }

    /*public void moveToSpam( String s ) {
        s = s.toLowerCase();
        String[] sArr = s.split(" ");
        for( String x: sArr ) {
            if(x.length()> 3 && !commonWords.contains(x)) {
                spamCount++;
                if( map.containsKey(x)) {
                    map.get(x).spamCount++;
                } else {
                    map.put(x, new Node( 1, 0));
                }
            }
        }

        Set<String> keys = map.keySet();
        for( String key: keys ) {
            Node node = map.get(key);
            double res = ((node.spamCount)/(double)(spamCount))/(double)(((node.spamCount)/(double)(spamCount)) + (node.hamCount)/(double)(hamCount));
            node.probability = res;
        }
    }*/

    public static void main(String[] args) throws Exception {
       // SpamDetector spamDetector = new SpamDetector();
       // spamDetector.init();

    }
}
