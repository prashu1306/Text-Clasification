package com.example.hp.pd_lab;

public class Node {
    int spamCount;
    int hamCount;
    double probability;

    public Node( int spamCount, int hamCount ) {
        this.spamCount = spamCount;
        this.hamCount = hamCount;
    }

    public String toString() {
        return String.valueOf(probability);
    }
}
