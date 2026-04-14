/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.KakurasuSolver;

/**
 *
 * @author Juan Farrel Hermanto
 */
public class KakurasuPuzzle {
    final int size;
    final int[] rowTargets;
    final int[] colTargets;
    final int[] rowValues;
    final int[] colValues;

    public KakurasuPuzzle(int size, int[] rowTargets, int[] colTargets) {
        this.size = size;
        this.rowTargets = rowTargets;
        this.colTargets = colTargets;

        this.rowValues = new int[size];
        this.colValues = new int[size];
        for (int i = 0; i < size; i++) {
            this.rowValues[i] = i + 1;
            this.colValues[i] = i + 1;
        }
    }
}
