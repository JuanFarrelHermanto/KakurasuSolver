/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.example.KakurasuSolver;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Juan Farrel Hermanto
 */
public class KakurasuSolverGui extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(KakurasuSolverGui.class.getName());

    /**
     * Creates new form KakurasuSolverGui
     */
    
    private KakurasuPuzzle currentPuzzle;
    private int popSize = 100;
    private int maxIter = 1000;
    private double lb = -5.0;
    private double ub = 5.0;
    
    public KakurasuSolverGui() {
        initComponents();
    }
    
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelPuzzleProblem = new javax.swing.JPanel();
        panelSolution = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelStatus = new javax.swing.JTextField();
        btnPuzzle = new javax.swing.JButton();
        btnParameter = new javax.swing.JButton();
        btnSolve = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(850, 600)); //Ukuran minimal GUI

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel1.setLayout(new java.awt.BorderLayout(20, 20)); //Ubah Layout Utama menjadi responsif

       
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Kakurasu Puzzle Solver");
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        //PENGATURAN 2 PANEL UTAMA
        javax.swing.JPanel centerWrapper = new javax.swing.JPanel(new java.awt.GridLayout(1, 2, 30, 0));
        centerWrapper.setOpaque(false);

        //PANEL SOAL
        javax.swing.JPanel leftPanel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Puzzle Problem");
        leftPanel.add(jLabel2, java.awt.BorderLayout.NORTH);
        panelPuzzleProblem.setBackground(new java.awt.Color(255, 255, 255));
        leftPanel.add(panelPuzzleProblem, java.awt.BorderLayout.CENTER);

        //PANEL SOLUSI
        javax.swing.JPanel rightPanel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Solution");
        rightPanel.add(jLabel3, java.awt.BorderLayout.NORTH);
        panelSolution.setBackground(new java.awt.Color(255, 255, 255));
        rightPanel.add(panelSolution, java.awt.BorderLayout.CENTER);

        centerWrapper.add(leftPanel);
        centerWrapper.add(rightPanel);
        jPanel1.add(centerWrapper, java.awt.BorderLayout.CENTER);

        //PENGATURAN STATUS & BUTTON
        javax.swing.JPanel bottomWrapper = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
        bottomWrapper.setOpaque(false);

        labelStatus.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        labelStatus.setText("Status Information");
        labelStatus.setEditable(false);
        labelStatus.addActionListener(this::labelStatusActionPerformed);
        bottomWrapper.add(labelStatus, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel buttonGroup = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 0));
        buttonGroup.setOpaque(false);
        btnPuzzle.setText("Puzzle");
        btnPuzzle.addActionListener(this::btnPuzzleActionPerformed);
        buttonGroup.add(btnPuzzle);

        btnParameter.setText("Parameter");
        btnParameter.addActionListener(this::btnParameterActionPerformed);
        buttonGroup.add(btnParameter);

        btnSolve.setText("Solve");
        btnSolve.addActionListener(this::btnSolveActionPerformed);
        buttonGroup.add(btnSolve);
        
        btnExperiment = new javax.swing.JButton();
        btnExperiment.setText("Experiment");
        btnExperiment.addActionListener(this::btnExperimentActionPerformed);
        buttonGroup.add(btnExperiment);

        bottomWrapper.add(buttonGroup, java.awt.BorderLayout.CENTER);
        jPanel1.add(bottomWrapper, java.awt.BorderLayout.SOUTH);

        //MEMASUKKAN KE FRAME UTAMA
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null); //Membuat window otomatis ke tengah layar
    }
    
    //Method untuk menggambar papan secara dinamis berdasarkan ukuran papan
    private void drawPuzzleBoard(KakurasuPuzzle puzzle, JPanel targetPanel, int[] solution) {
        targetPanel.removeAll();
        
        int n = puzzle.size;
        
        //Set Layout menjadi n+2 baris dan n+2 kolom (ruang ekstra untuk bobot & target)
        targetPanel.setLayout(new java.awt.GridLayout(n + 2, n + 2, 2, 2));
        
        //Looping untuk membuat kotak-kotak grid
        for (int i = 0; i <= n + 1; i++) {
            for (int j = 0; j <= n + 1; j++) {
                javax.swing.JLabel cellLabel = new javax.swing.JLabel();
                cellLabel.setOpaque(true);
                cellLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                cellLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                
                if (i == 0 && j > 0 && j <= n) {
                    //AREA BOBOT KOLOM (SISI ATAS)
                    cellLabel.setText(String.valueOf(j));
                    cellLabel.setForeground(java.awt.Color.DARK_GRAY);
                } else if (j == 0 && i > 0 && i <= n) {
                    //AREA BOBOT BARIS (SISI KIRI)
                    cellLabel.setText(String.valueOf(i));
                    cellLabel.setForeground(java.awt.Color.DARK_GRAY);
                } else if (i > 0 && i <= n && j > 0 && j <= n) {
                    //AREA KOTAK PUZZLE
                    cellLabel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
                    
                    if (solution != null) {
                        //Ini untuk menggambar panel jawaban
                        if (solution[(i - 1) * n + (j - 1)] == 1) {
                            cellLabel.setBackground(java.awt.Color.BLACK);
                        } else {
                            cellLabel.setBackground(java.awt.Color.WHITE);
                        }
                    } else {
                        //Ini untuk panel soal awal
                        cellLabel.setBackground(java.awt.Color.WHITE);
                    }
                } else if (i > 0 && i <= n && j == n + 1) {
                    //AREA TARGET BARIS (SISI KANAN)
                    
                    int target = puzzle.rowTargets[i - 1];
                    cellLabel.setText(String.valueOf(target));
                    
                    if (solution != null){
                        int currentSum = 0;
                        for (int c = 0; c < n; c++){
                            if (solution[(i - 1) * n + c] == 1){
                                currentSum += puzzle.colValues[c];
                            }
                        }
                        if (currentSum != target){
                            cellLabel.setForeground(java.awt.Color.RED);
                        } else {
                            cellLabel.setForeground(new java.awt.Color(0, 0, 200));
                        }
                    } else {
                        cellLabel.setForeground(new java.awt.Color(0, 0, 200));
                    }
                } else if (i == n + 1 && j > 0 && j <= n) {
                    //AREA TARGET KOLOM (SISI BAWAH)
                    int target = puzzle.colTargets[j - 1];
                    cellLabel.setText(String.valueOf(target));
                    
                    if (solution != null) {
                        int currentSum = 0;
                        for (int r = 0; r < n; r++) {
                            if (solution[r * n + (j - 1)] == 1) {
                                currentSum += puzzle.rowValues[r];
                            }
                        }
                        
                        if (currentSum != target) {
                            cellLabel.setForeground(java.awt.Color.RED);
                        } else {
                            cellLabel.setForeground(new java.awt.Color(0, 0, 200)); 
                        }
                    } else {
                        cellLabel.setForeground(new java.awt.Color(0, 0, 200));
                    }
                } else {
                    //AREA POJOK (KOSONG)
                    cellLabel.setBackground(targetPanel.getBackground());
                }
                
                //Tambahin label yang sudah diatur ke dalam panel
                targetPanel.add(cellLabel);
            }
        }
        
        //Refresh panel agar tampilan baru muncul
        targetPanel.revalidate();
        targetPanel.repaint();
    }

          

    private void labelStatusActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void btnPuzzleActionPerformed(java.awt.event.ActionEvent evt) {                                          
        //Panel utama pop-up
        JPanel popUpPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        //Bagian A: Pilih File
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        JTextField txtFilePath = new JTextField(20);
        txtFilePath.setEditable(false);
        JButton btnBrowse = new JButton("Browse...");

        filePanel.add(new JLabel("1. Pilih File Soal (.txt):"), BorderLayout.NORTH);
        filePanel.add(txtFilePath, BorderLayout.CENTER);
        filePanel.add(btnBrowse, BorderLayout.EAST);

        //Bagian B: Pilih Nomor Soal (Dropdown)
        JPanel comboPanel = new JPanel(new BorderLayout(5, 5));
        String[] puzzleNumbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        JComboBox<String> comboPuzzleIndex = new JComboBox<>(puzzleNumbers);

        comboPanel.add(new JLabel("2. Pilih Soal Ke-:"), BorderLayout.NORTH);
        comboPanel.add(comboPuzzleIndex, BorderLayout.CENTER);

        //Masukin Bagian A dan B ke Panel Utama Pop-up
        popUpPanel.add(filePanel);
        popUpPanel.add(comboPanel);

        //Variabel array untuk menyimpan file dari dalam ActionListener
        final File[] selectedFileHolder = new File[1];

        //Ketika button browse di klik
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                //Hanya sementara untuk testing
                File defaultDir = new File("E:/Materi Kuliah/Semester 8/Tugas Akhir/Soal Kakurasu");
                fileChooser.setCurrentDirectory(defaultDir);
                
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
                fileChooser.setFileFilter(filter);

                //Menampilkan dialog file manager
                if (fileChooser.showOpenDialog(popUpPanel) == JFileChooser.APPROVE_OPTION) {
                    selectedFileHolder[0] = fileChooser.getSelectedFile();
                    txtFilePath.setText(selectedFileHolder[0].getName());
                }
            }
        });

        //Trigger pop-up
        int result = JOptionPane.showConfirmDialog(
                this, 
                popUpPanel, 
                "Pengaturan Input Puzzle", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
        );

        //Ketika button OK di klik,
        if (result == JOptionPane.OK_OPTION) {
            File fileToOpen = selectedFileHolder[0];

            //Validasi apakah user sudah memilih file
            if (fileToOpen == null) {
                JOptionPane.showMessageDialog(this, "Anda belum memilih file!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int targetIndex = comboPuzzleIndex.getSelectedIndex(); 

            //PROSES BACA FILE
            try {
                Scanner sc = new Scanner(fileToOpen);
                List<KakurasuPuzzle> tempList = new ArrayList<>(); //Penampung soal sementara

                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.isEmpty()) continue;

                    //Baca 1 blok soal
                    int size = Integer.parseInt(line);

                    String[] rowStrings = sc.nextLine().trim().split("\\s+");
                    int[] rowTargets = new int[size];
                    for (int i = 0; i < size; i++) rowTargets[i] = Integer.parseInt(rowStrings[i]);

                    String[] colStrings = sc.nextLine().trim().split("\\s+");
                    int[] colTargets = new int[size];
                    for (int i = 0; i < size; i++) colTargets[i] = Integer.parseInt(colStrings[i]);

                    //Masukin ke penampung soal
                    tempList.add(new KakurasuPuzzle(size, rowTargets, colTargets));
                }
                sc.close();

                //VALIDASI MANA SOAL YANG AKTIF
                //Cek nomor yang dipilih ada di soal atau tidak
                if (targetIndex < tempList.size()) {
                    currentPuzzle = tempList.get(targetIndex);

                    labelStatus.setText("Berhasil memuat Soal Ke-" + (targetIndex + 1) + 
                                        " (Ukuran " + currentPuzzle.size + "x" + currentPuzzle.size + 
                                        ") dari " + fileToOpen.getName());

                    drawPuzzleBoard(currentPuzzle, panelPuzzleProblem, null);
                    
                    //Kosongkan panel jawaban jika user memuat puzzle baru
                    panelSolution.removeAll();
                    panelSolution.revalidate();
                    panelSolution.repaint();
                    // -------------------------------------

                } else {
                    //Kalo user milih nomor 10, tapi di file cuma ada 3 soal, kasih error message
                    JOptionPane.showMessageDialog(this, 
                        "File " + fileToOpen.getName() + " hanya berisi " + tempList.size() + " soal.\nSoal Ke-" + (targetIndex + 1) + " tidak ditemukan!", 
                        "Kesalahan Indeks", 
                        JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Gagal membaca file! Pastikan format blok soal benar.\nDetail: " + ex.getMessage(), 
                    "Error Membaca File", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                         

    private void btnSolveActionPerformed(java.awt.event.ActionEvent evt) {                                         
        //Validasi apakah soal sudah dimuat
        if (currentPuzzle == null) {
            JOptionPane.showMessageDialog(this, "Silakan muat soal puzzle terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Ganti UI sebelum algoritma jalan
        labelStatus.setText("WaOA sedang mencari solusi... Mohon tunggu.");
        btnSolve.setEnabled(false); //Disable tombol solve sementara agar tidak di-spam user

        //Jalankan algoritma di Thread terpisah (Background Process)
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            
            //Panggil solver yang sudah dioptimasi
            WaOASolver solver = new WaOASolver(currentPuzzle, popSize, maxIter, lb, ub, true);
            int[] bestSolution = solver.solve(); //Mengambil array solusi biner
            double bestFitness = solver.getBestFitness();
            
            long endTime = System.currentTimeMillis();
            double timeInSeconds = (endTime - startTime) / 1000.0;
            
            //Kembalikan pembaruan visual ke UI utama
            javax.swing.SwingUtilities.invokeLater(() -> {
                //Gambar solusi ke panel jawaban
                drawPuzzleBoard(currentPuzzle, panelSolution, bestSolution);
                
                if(bestFitness == 0){
                    labelStatus.setText(String.format("Solusi TEPAT ditemukan! Waktu komputasi: %.3f detik.", timeInSeconds));
                } else {
                    labelStatus.setText(String.format("Solusi TERDEKAT (Fitness: %.0f). Waktu komputasi: %.3f detik.", bestFitness, timeInSeconds));
                }
                btnSolve.setEnabled(true); //Nyalakan kembali tombol solve
            });
            
        }).start();
    }                                        

    private void btnParameterActionPerformed(java.awt.event.ActionEvent evt) {                                             
        //Buat Panel dengan GridLayout (4 baris, 2 kolom, jarak 10px)
        JPanel paramPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        //Buat TextFields dan isi dengan nilai default/saat ini
        JTextField txtPopSize = new JTextField(String.valueOf(popSize));
        JTextField txtMaxIter = new JTextField(String.valueOf(maxIter));
        JTextField txtLb = new JTextField(String.valueOf(lb));
        JTextField txtUb = new JTextField(String.valueOf(ub));

        //Buat dan masukkan Label dan TextFields ke dalam panel
        paramPanel.add(new JLabel("Population Size (N):"));
        paramPanel.add(txtPopSize);
        
        paramPanel.add(new JLabel("Max Iterations (T):"));
        paramPanel.add(txtMaxIter);
        
        paramPanel.add(new JLabel("Lower Bound (lb):"));
        paramPanel.add(txtLb);
        
        paramPanel.add(new JLabel("Upper Bound (ub):"));
        paramPanel.add(txtUb);

        //Tampilkan pop-up
        int result = JOptionPane.showConfirmDialog(
                this, 
                paramPanel, 
                "Pengaturan Parameter Algoritma", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
        );

        //Kalo pengguna menekan button OK, validasi nilainya
        if (result == JOptionPane.OK_OPTION) {
            try {
                int inputPopSize = Integer.parseInt(txtPopSize.getText().trim());
                int inputMaxIter = Integer.parseInt(txtMaxIter.getText().trim());
                double inputLb = Double.parseDouble(txtLb.getText().trim());
                double inputUb = Double.parseDouble(txtUb.getText().trim());

                //Validasi logika dasar
                if (inputPopSize <= 0 || inputMaxIter <= 0) {
                    JOptionPane.showMessageDialog(this, "Populasi dan Iterasi harus lebih besar dari 0!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                    return; //Ga disimpan kalo salah
                }
                if (inputLb >= inputUb) {
                    JOptionPane.showMessageDialog(this, "Lower Bound harus lebih kecil dari Upper Bound!", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Kalo semua aman, simpan ke variabel global
                popSize = inputPopSize;
                maxIter = inputMaxIter;
                lb = inputLb;
                ub = inputUb;

                //Update status agar user tau perubahan berhasil
                labelStatus.setText("Parameter di-update: N=" + popSize + ", T=" + maxIter + ", Bounds=[" + lb + ", " + ub + "]");

            } catch (NumberFormatException ex) {
                //Error message kalo user memasukkan selain angka
                JOptionPane.showMessageDialog(this, 
                        "Input tidak valid! Pastikan Anda hanya memasukkan angka.\n(Gunakan titik untuk angka desimal)", 
                        "Error Format Angka", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void btnExperimentActionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        
        File defaultDir = new File("E:/Materi Kuliah/Semester 8/Tugas Akhir/Soal Kakurasu");
        fileChooser.setCurrentDirectory(defaultDir);
        
        // Filter agar hanya bisa memilih file .txt
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        fileChooser.setFileFilter(filter);

        // Tampilkan jendela pencarian file
        int result = fileChooser.showOpenDialog(this);

        // Jika Anda memilih file dan menekan "Open" atau "OK"
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Opsional (Praktik UX yang baik): Konfirmasi ganda sebelum program berjalan lama
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Anda yakin ingin menjalankan eksperimen pada file:\n" + selectedFile.getName() + "?\n\n(Proses ini akan membaca seluruh soal dan menyimpannya ke CSV)", 
                "Konfirmasi Eksperimen", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // PANGGIL METHOD EXPERIMENT YANG SUDAH ANDA BUAT SEBELUMNYA
                runExperiment(selectedFile); 
            }
        }
    }
    
    private void runExperiment(File file){
        btnPuzzle.setEnabled(false);
        btnSolve.setEnabled(false);
        btnParameter.setEnabled(false);
        btnExperiment.setEnabled(false);
        labelStatus.setText("Eksperimen sedang dilakukan...");
        
        new Thread(() -> {
            try {
                String outputFileName = "E:/Materi Kuliah/Semester 8/Tugas Akhir/Soal Kakurasu/Hasil Eksperimen/Hasil_Eksperimen_" + file.getName() + "_" + String.valueOf(System.currentTimeMillis());
                PrintWriter writerNoPrep = new PrintWriter(new FileWriter(outputFileName + "_NoPrep.csv"));
                PrintWriter writerWithPrep = new PrintWriter(new FileWriter(outputFileName + "_WithPrep.csv"));
                
                String header = "No,Size,Fitness,LockedCells_Preprocessing,Total_Cells,Reduction_Persentage";
                writerNoPrep.println(header);
                writerWithPrep.println(header);
                
                Scanner sc = new Scanner(file);
                List<KakurasuPuzzle> expPuzzles = new ArrayList<>();
                
                while(sc.hasNextLine()){
                    String line = sc.nextLine().trim();
                    if(line.isEmpty()) continue;
                    
                    int size = Integer.parseInt(line);
                    
                    String[] rowStrings = sc.nextLine().trim().split("\\s+");
                    int[] rowTargets = new int[size];
                    for(int i = 0; i < size; i++) rowTargets[i] = Integer.parseInt(rowStrings[i]);
                    
                    String[] colStrings = sc.nextLine().trim().split("\\s+");
                    int[] colTargets = new int[size];
                    for(int i = 0; i < size; i++) colTargets[i] = Integer.parseInt(colStrings[i]);
                    
                    expPuzzles.add(new KakurasuPuzzle(size, rowTargets, colTargets));
                }
                
                sc.close();
                
                int totalPuzzles = expPuzzles.size();
                int totalCellsOverall = 0;
                int totalLockedOverall = 0;
                int solutionsFound = 0;
                
                System.out.println("=== MEMULAI EKSPERIMEN: " + file.getName() + " ===");
                System.out.println("Total Soal: " + totalPuzzles);
                System.out.println("Parameter: N=" + popSize + ", MaxIter=" + maxIter + ", lb=" + lb + ", ub=" + ub + "\n");
                
                System.out.println(">>> FASE 1: WaOA Tanpa Preprocessing...");
                int totalCellsOverall_1 = 0;
                int solutionsFound_1 = 0;
                
                for(int i = 0; i < totalPuzzles; i++){
                    KakurasuPuzzle puzzle = expPuzzles.get(i);
                    int currTotalCells = puzzle.size * puzzle.size;
                    
                    WaOASolver solver = new WaOASolver(puzzle, popSize, maxIter, lb, ub, false);
                    solver.solve();
                    
                    double fitness = solver.getBestFitness();
                    int lockedCells = solver.getLockedCellsCount();
                    double percent = ((double) lockedCells / currTotalCells) * 100.0;
                    
                    writerNoPrep.printf("%d,%dx%d,%.1f,%d,%d,%.2f\n", (i + 1), puzzle.size, puzzle.size, fitness, 0, currTotalCells, 0.0);
                    
                    totalCellsOverall_1 += currTotalCells;
                    
                    if (fitness == 0.0) solutionsFound_1++;
                    
                    System.out.printf("Soal %03d | Ukuran: %dx%d | Fitness: %6.1f\n", (i + 1), puzzle.size, puzzle.size, fitness);
                }
                writerNoPrep.close();
                System.out.println("Fase 1 Selesai. (Akurasi: " + solutionsFound_1 + "/" + totalPuzzles + ")\n");
                
                
                System.out.println(">>> FASE 2: WaOA Dengan Preprocessing...");
                int totalCellsOverall_2 = 0;
                int totalLockedOverall_2 = 0;
                int solutionsFound_2 = 0;
                
                for(int i = 0; i < totalPuzzles; i++){
                    KakurasuPuzzle puzzle = expPuzzles.get(i);
                    int currTotalCells = puzzle.size * puzzle.size;
                    
                    WaOASolver solver = new WaOASolver(puzzle, popSize, maxIter, lb, ub, true); 
                    solver.solve();
                    
                    double fitness = solver.getBestFitness();
                    int lockedCells = solver.getLockedCellsCount();
                    double percent = ((double) lockedCells / currTotalCells) * 100.0;
                    
                    writerWithPrep.printf("%d,%dx%d,%.1f,%d,%d,%.2f\n", (i + 1), puzzle.size, puzzle.size, fitness, lockedCells, currTotalCells, percent);
                    
                    totalCellsOverall_2 += currTotalCells;
                    totalLockedOverall_2 += lockedCells;
                    if (fitness == 0.0) solutionsFound_2++;
                }
                writerWithPrep.close();
                System.out.println("Fase 2 Selesai. (Akurasi: " + solutionsFound_2 + "/" + totalPuzzles + ")\n");
            
                double acc1 = ((double) solutionsFound_1 / totalPuzzles) * 100.0;
                double acc2 = ((double) solutionsFound_2 / totalPuzzles) * 100.0;
                double prepPercent = ((double) totalLockedOverall_2 / totalCellsOverall_2) * 100.0;

                
                String popUpMessage = "Eksperimen Selesai!\n\n" +
                        "[Fase 1: Murni WaOA]\n" +
                        "Akurasi: " + solutionsFound_1 + "/" + totalPuzzles + " (" + String.format(java.util.Locale.US, "%.1f", acc1) + "%)\n\n" +
                        "[Fase 2: WaOA + Preprocessing]\n" +
                        "Total Sel Terkunci: " + totalLockedOverall_2 + " dari " + totalCellsOverall_2 + " (" + String.format(java.util.Locale.US, "%.1f", prepPercent) + "%)\n" +
                        "Akurasi: " + solutionsFound_2 + "/" + totalPuzzles + " (" + String.format(java.util.Locale.US, "%.1f", acc2) + "%)\n\n" +
                        "2 File CSV telah disimpan di folder Dataset!";
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    labelStatus.setText("Eksperimen selesai!");
                    
                    // 3. Masukkan variabel String tadi ke dalam JOptionPane
                    JOptionPane.showMessageDialog(this, 
                        popUpMessage, 
                        "Laporan Eksperimen", JOptionPane.INFORMATION_MESSAGE);
                    
                    btnPuzzle.setEnabled(true);
                    btnSolve.setEnabled(true);
                    btnParameter.setEnabled(true);
                    btnExperiment.setEnabled(true);
                });
                
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                   JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat eksperimen: " + e.getMessage());
                   btnPuzzle.setEnabled(true);
                   btnSolve.setEnabled(true);
                   btnParameter.setEnabled(true);
                   btnExperiment.setEnabled(true);
                });
            }
        }).start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new KakurasuSolverGui().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnParameter;
    private javax.swing.JButton btnPuzzle;
    private javax.swing.JButton btnSolve;
    private javax.swing.JButton btnExperiment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField labelStatus;
    private javax.swing.JPanel panelPuzzleProblem;
    private javax.swing.JPanel panelSolution;
    // End of variables declaration                   
}
