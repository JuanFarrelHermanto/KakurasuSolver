/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.KakurasuSolver;

import java.util.Random;

/**
 *
 * @author Juan Farrel Hermanto
 */
public class WaOASolver {
    private final KakurasuPuzzle puzzle;
    private final int N; //Ukuran populasi
    private final int T; //Maks iterasi
    private final int size; //Ukuran papan
    private final int totalCells;
    private int lockedCellsCount = 0;
    private final boolean usePreprocess;

    //Representasi walrus
    private final int[][] popBinary;
    private final double[][] popContinuous;
    private final double[] popFitness;

    //Solusi terbaik
    private final int[] bestBinary;
    private final double[] bestContinuous;
    private double bestFitness = Double.MAX_VALUE;

    //Hasil preprocessing
    private final int[] lockedBoard; // -1: Blm Pasti, 0: Putih, 1: Hitam
    private final int[] reducedRowTargets;
    private final int[] reducedColTargets;

    //Batas atas & bawah WaOA
    private final double lb;
    private final double ub;
    private final Random rand = new Random();

    public WaOASolver(KakurasuPuzzle puzzle, int populationSize, int maxIterations, double lb, double ub, boolean usePreprocess) {
        this.puzzle = puzzle;
        this.N = populationSize;
        this.T = maxIterations;
        this.lb = lb;
        this.ub = ub;
        this.size = puzzle.size;
        this.totalCells = size * size;
        this.usePreprocess = usePreprocess;

        this.popBinary = new int[N][totalCells];
        this.popContinuous = new double[N][totalCells];
        this.popFitness = new double[N];

        this.bestBinary = new int[totalCells];
        this.bestContinuous = new double[totalCells];

        this.lockedBoard = new int[totalCells];
        this.reducedRowTargets = new int[size];
        this.reducedColTargets = new int[size];

        //Set semua cell sebagai -1 (blm pasti) di awal
        for (int i = 0; i < totalCells; i++) {
            lockedBoard[i] = -1;
        }
    }

    //TAHAP PREPROCESSING
    private void preprocessBoard() {
        boolean changed;
        do {
            changed = false;
            //Evaluasi Baris
            for (int r = 0; r < size; r++) {
                int currentSum = 0;
                int possibleSum = 0;
                for (int c = 0; c < size; c++) {
                    int idx = r * size + c;
                    int weight = c + 1;
                    if (lockedBoard[idx] == 1) currentSum += weight;
                    else if (lockedBoard[idx] == -1) possibleSum += weight;
                }
                int remTarget = puzzle.rowTargets[r] - currentSum;
                
                for (int c = 0; c < size; c++) {
                    int idx = r * size + c;
                    int weight = c + 1;
                    if (lockedBoard[idx] == -1) {
                        if (remTarget == 0 || weight > remTarget) { //Sisa target nol & nilai mustahil
                            lockedBoard[idx] = 0; changed = true;
                        } else if (remTarget == possibleSum || possibleSum - weight < remTarget) { //Sisa target Penuh & pemilihan wajib
                            lockedBoard[idx] = 1; changed = true;
                        }
                    }
                }
            }

            //Evaluasi Kolom
            for (int c = 0; c < size; c++) {
                int currentSum = 0;
                int possibleSum = 0;
                for (int r = 0; r < size; r++) {
                    int idx = r * size + c;
                    int weight = r + 1;
                    if (lockedBoard[idx] == 1) currentSum += weight;
                    else if (lockedBoard[idx] == -1) possibleSum += weight;
                }
                int remTarget = puzzle.colTargets[c] - currentSum;
                
                for (int r = 0; r < size; r++) {
                    int idx = r * size + c;
                    int weight = r + 1;
                    if (lockedBoard[idx] == -1) {
                        if (remTarget == 0 || weight > remTarget) {
                            lockedBoard[idx] = 0; changed = true;
                        } else if (remTarget == possibleSum || possibleSum - weight < remTarget) {
                            lockedBoard[idx] = 1; changed = true;
                        }
                    }
                }
            }
        } while (changed);

        //Menghitung Target Reduksi (Sisa angka yang harus dicari WaOA)
        System.arraycopy(puzzle.rowTargets, 0, reducedRowTargets, 0, size);
        System.arraycopy(puzzle.colTargets, 0, reducedColTargets, 0, size);

        int lockedCount = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (lockedBoard[r * size + c] == 1) {
                    reducedRowTargets[r] -= (c + 1);
                    reducedColTargets[c] -= (r + 1);
                    lockedCount++;
                } else if (lockedBoard[r * size + c] == 0) {
                    lockedCount++;
                }
            }
        }
        this.lockedCellsCount = lockedCount;
        System.out.println("Preprocessing selesai! " + lockedCount + " dari " + totalCells + " sel berhasil dikunci.");
    }

    //Method untuk menampilkan hasil dari Preprocessing
    private void printPreprocessedBoard() {
        System.out.println("\n--- Kondisi Papan Setelah Preprocessing ---");
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int status = lockedBoard[i * size + j];
                if (status == -1) {
                    System.out.print("? "); //Sel yang masih harus dicari lewat WaOA
                } else {
                    System.out.print(status + " "); //Sel yang sudah dikunci mutlak
                }
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------");
    }

    //HITUNG FITNESS
    private double calculateFitness(int[] binarySolution) {
        double totalError = 0;

        for (int i = 0; i < size; i++) {
            int rowSum = 0; 
            for (int j = 0; j < size; j++) {
                int idx = i * size + j;
                //Hanya hitung jika sel masih blm pasti (-1) dan diisi hitam (1)
                if (lockedBoard[idx] == -1 && binarySolution[idx] == 1) {
                    rowSum += puzzle.colValues[j];
                }
            }
            totalError += Math.pow(rowSum - reducedRowTargets[i], 2);
        }

        for (int j = 0; j < size; j++) {
            int colSum = 0;
            for (int i = 0; i < size; i++) {
                int idx = i * size + j;
                if (lockedBoard[idx] == -1 && binarySolution[idx] == 1) {
                    colSum += puzzle.rowValues[i];
                }
            }
            totalError += Math.pow(colSum - reducedColTargets[j], 2);
        }
        return totalError;
    }

    private double vShapeTransfer(double velocity) {
        return Math.abs(Math.tanh(velocity));
    }

    public int[] solve() {
        if (usePreprocess){
            preprocessBoard(); 
            //printPreprocessedBoard();
        } else {
            System.arraycopy(puzzle.rowTargets, 0, reducedRowTargets, 0, size);
            System.arraycopy(puzzle.colTargets, 0, reducedColTargets, 0, size);
            this.lockedCellsCount = 0;
        }
        
        //INISIALISASI POPULASI WALRUS MENGIKUTI HASIL PREPROCESSING
        for (int p = 0; p < N; p++) {
            for (int i = 0; i < totalCells; i++) {
                if (lockedBoard[i] != -1) {
                    //Paksa ikuti hasil preprocessing
                    popBinary[p][i] = lockedBoard[i];
                    popContinuous[p][i] = (lockedBoard[i] == 1) ? ub : lb; 
                } else {
                    //Acak sisa kotak yang blm pasti
                    popBinary[p][i] = rand.nextBoolean() ? 1 : 0;
                    popContinuous[p][i] = lb + (ub - lb) * rand.nextDouble();
                }
            }
            popFitness[p] = calculateFitness(popBinary[p]);

            if (popFitness[p] < bestFitness) {
                bestFitness = popFitness[p];
                System.arraycopy(popBinary[p], 0, bestBinary, 0, totalCells);
                System.arraycopy(popContinuous[p], 0, bestContinuous, 0, totalCells);
            }
        }

        //Loop utama WaOA
        int[] tempBinary = new int[totalCells];
        double[] tempContinuous = new double[totalCells];

        for (int t = 1; t <= T; t++) {
            if (bestFitness == 0) {
                System.out.println("Solusi optimal ditemukan pada iterasi " + (t - 1));
                break;
            }

            for (int p = 0; p < N; p++) {
                System.arraycopy(popBinary[p], 0, tempBinary, 0, totalCells);
                System.arraycopy(popContinuous[p], 0, tempContinuous, 0, totalCells);

                //FASE 1: Feeding Strategy
                for (int j = 0; j < totalCells; j++) {
                    if (lockedBoard[j] != -1) continue; //MASKING

                    double r = rand.nextDouble();
                    int I = rand.nextInt(2) + 1;
                    double newPos = tempContinuous[j] + r * (bestContinuous[j] - I * tempContinuous[j]);
                    
                    if (rand.nextDouble() < vShapeTransfer(newPos)) tempBinary[j] = 1 - tempBinary[j];
                    tempContinuous[j] = newPos;
                }
                updateIfBetter(p, tempBinary, tempContinuous);

                //FASE 2: Migration
                int k = rand.nextInt(N);
                for (int j = 0; j < totalCells; j++) {
                    if (lockedBoard[j] != -1) continue;

                    double r = rand.nextDouble();
                    double newPos;
                    if (popFitness[k] < popFitness[p]) {
                        int I = rand.nextInt(2) + 1;
                        newPos = tempContinuous[j] + r * (popContinuous[k][j] - I * tempContinuous[j]);
                    } else {
                        newPos = tempContinuous[j] + r * (tempContinuous[j] - popContinuous[k][j]);
                    }

                    if (rand.nextDouble() < vShapeTransfer(newPos)) tempBinary[j] = 1 - tempBinary[j];
                    tempContinuous[j] = newPos;
                }
                updateIfBetter(p, tempBinary, tempContinuous);

                //FASE 3: Escaping/Fighting
                double lb_local = lb / t;
                double ub_local = ub / t;
                for (int j = 0; j < totalCells; j++) {
                    if (lockedBoard[j] != -1) continue;

                    double r = rand.nextDouble();
                    double step = lb_local + (ub_local - r * lb_local);
                    double newPos = tempContinuous[j] + step;

                    if (rand.nextDouble() < vShapeTransfer(newPos)) tempBinary[j] = 1 - tempBinary[j];
                    tempContinuous[j] = newPos;
                }
                updateIfBetter(p, tempBinary, tempContinuous);
            }
            if (t % 100 == 0 || bestFitness == 0) {
                System.out.println("Iterasi " + t + ": Fitness Terbaik = " + bestFitness);
            }
        }

        return bestBinary;
    }

    private void updateIfBetter(int p, int[] tempBinary, double[] tempContinuous) {
        double newFitness = calculateFitness(tempBinary);
        if (newFitness < popFitness[p]) {
            popFitness[p] = newFitness;
            System.arraycopy(tempBinary, 0, popBinary[p], 0, totalCells);
            System.arraycopy(tempContinuous, 0, popContinuous[p], 0, totalCells);

            if (newFitness < bestFitness) {
                bestFitness = newFitness;
                System.arraycopy(tempBinary, 0, bestBinary, 0, totalCells);
                System.arraycopy(tempContinuous, 0, bestContinuous, 0, totalCells);
            }
        } else {
            //Kembalikan array ke posisi semula jika tidak lebih baik
            System.arraycopy(popBinary[p], 0, tempBinary, 0, totalCells);
            System.arraycopy(popContinuous[p], 0, tempContinuous, 0, totalCells);
        }
    }

    private void printBoard(int[] solusi) {
        System.out.println("\n--- Hasil Akhir Papan ---");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(solusi[i * size + j] + " ");
            }
            System.out.println();
        }
    }
    
    public double getBestFitness(){
        return bestFitness;
    }
    public int getLockedCellsCount(){
        return lockedCellsCount;
    }
}
