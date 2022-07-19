
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Visual extends javax.swing.JFrame {

    static int mode;
    ImageIcon originalImage;
    ImageIcon processedImage;
    static String originalFilePath;
    String processedFilePath;
    float[][] kernelMatrix;
    static BufferedImage output;
    FileFilter filter = new FileNameExtensionFilter("Image files (.jpg, .png)", "jpg", "png");
    static String ext;

    public Visual() {
        initComponents();
        mode = 1;   // By default, mode is 1
    }

    private void initComponents() {

        modesButtonGroup = new javax.swing.ButtonGroup();
        openFileChooser = new javax.swing.JFileChooser();
        saveFileChooser = new javax.swing.JFileChooser();
        Panel = new javax.swing.JPanel();
        operationsScrollPane = new javax.swing.JScrollPane();
        operationsList = new javax.swing.JList<>();
        runButton = new javax.swing.JButton();
        originalImageLabel = new javax.swing.JLabel();
        label1 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        processedImageLabel = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        modesMenu = new javax.swing.JMenu();
        sequentialMode = new javax.swing.JRadioButtonMenuItem();
        parallelMode = new javax.swing.JRadioButtonMenuItem();
        distributedMode = new javax.swing.JRadioButtonMenuItem();
        imageMenu = new javax.swing.JMenu();
        selectImageMI = new javax.swing.JMenuItem();
        samplesMenu = new javax.swing.JMenu();
        sample1MI = new javax.swing.JMenuItem();
        sample2MI = new javax.swing.JMenuItem();
        sample3MI = new javax.swing.JMenuItem();
        sample4MI = new javax.swing.JMenuItem();
        sample5MI = new javax.swing.JMenuItem();
        sample6MI = new javax.swing.JMenuItem();
        sample7MI = new javax.swing.JMenuItem();
        sample8MI = new javax.swing.JMenuItem();
        sample9MI = new javax.swing.JMenuItem();
        sample10MI = new javax.swing.JMenuItem();
        sample11MI = new javax.swing.JMenuItem();

        openFileChooser.setDialogTitle("Open some file...");
        openFileChooser.setPreferredSize(new java.awt.Dimension(600, 400));

        saveFileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFileChooser.setDialogTitle("Save as...");
        saveFileChooser.setPreferredSize(new java.awt.Dimension(600, 400));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Kernel image processing");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        operationsList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Edge Detection", "Sharpen", "Box Blur", "Gaussian Blur 3x3", "Gaussian Blur 5x5", "Unsharp Masking 5x5", "Motion Blur", "Emboss" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        operationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        operationsList.setToolTipText("");
        operationsScrollPane.setViewportView(operationsList);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        originalImageLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.darkGray));

        label1.setText("Select operation:");

        label2.setText("Your selected image:");

        processedImageLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.lightGray, java.awt.Color.darkGray));

        label3.setText("Your processed image:");

        saveButton.setText("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                    .addComponent(operationsScrollPane)
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addComponent(label1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(label2)
                    .addComponent(originalImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(processedImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3))
                .addContainerGap())
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1)
                    .addComponent(label2)
                    .addComponent(label3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addComponent(processedImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(PanelLayout.createSequentialGroup()
                        .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(PanelLayout.createSequentialGroup()
                                .addComponent(operationsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(runButton)
                                    .addComponent(saveButton))
                                .addGap(18, 18, 18)
                                .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(originalImageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(23, Short.MAX_VALUE))))
        );

        modesMenu.setText("Mode");

        modesButtonGroup.add(sequentialMode);
        sequentialMode.setSelected(true);
        sequentialMode.setText("Sequential mode");
        sequentialMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sequentialModeActionPerformed(evt);
            }
        });
        modesMenu.add(sequentialMode);

        modesButtonGroup.add(parallelMode);
        parallelMode.setText("Parallel mode");
        parallelMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parallelModeActionPerformed(evt);
            }
        });
        modesMenu.add(parallelMode);

        modesButtonGroup.add(distributedMode);
        distributedMode.setText("Distributed mode");
        distributedMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distributedModeActionPerformed(evt);
            }
        });
        modesMenu.add(distributedMode);

        MenuBar.add(modesMenu);

        imageMenu.setText("Image");

        selectImageMI.setText("Select your image");
        selectImageMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectImageMIActionPerformed(evt);
            }
        });
        imageMenu.add(selectImageMI);

        samplesMenu.setText("Select from samples");

        sample1MI.setText("Sample 1: 300 x 300 (50 KB) - Butterfly");
        sample1MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample1MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample1MI);

        sample2MI.setText("Sample 2: 689 x 689 (100 KB) - Butterfly");
        sample2MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample2MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample2MI);

        sample3MI.setText("Sample 3: 1036 x 1036 (200 KB) - Butterfly");
        sample3MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample3MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample3MI);

        sample4MI.setText("Sample 4: 1792 x 1792 (500 KB) - Butterfly");
        sample4MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample4MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample4MI);

        sample5MI.setText("Sample 5: 2192 x 2921 (1 MB) - Plane");
        sample5MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample5MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample5MI);

        sample6MI.setText("Sample 6: 3218 x 4291 (2 MB) - Plane");
        sample6MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample6MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample6MI);

        sample7MI.setText("Sample 7: 5072 x 6761 (5 MB) - Plane");
        sample7MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample7MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample7MI);

        sample8MI.setText("Sample 8: 7724 x 5148 (10 MB) - Park");
        sample8MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample8MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample8MI);

        sample9MI.setText("Sample 9: 10212 x 6806 (15 MB) - Park");
        sample9MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample9MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample9MI);

        sample10MI.setText("Sample 10: 10751 x 4287 (20 MB) - Forest");
        sample10MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample10MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample10MI);

        sample11MI.setText("Sample 11: 13583 x 5417 (30 MB) - Forest");
        sample11MI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sample11MIActionPerformed(evt);
            }
        });
        samplesMenu.add(sample11MI);

        imageMenu.add(samplesMenu);

        MenuBar.add(imageMenu);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void sequentialModeActionPerformed(java.awt.event.ActionEvent evt) {
        mode = 1;
        System.out.println("Mode is: " + mode);
    }

    private void parallelModeActionPerformed(java.awt.event.ActionEvent evt) {
        mode = 2;
        System.out.println("Mode is: " + mode);
    }

    private void distributedModeActionPerformed(java.awt.event.ActionEvent evt) {
        mode = 3;
        System.out.println("Mode is: " + mode);
    }

    private void sample1MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample1_300x300.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample2MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample2_689x689.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample3MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample3_1036x1036.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample4MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample4_1792x1792.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample5MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample5_2192x2921.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample6MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample6_3218x4291.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample7MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample7_5072x6761.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample8MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample8_7724x5148.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample9MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample9_10212x6806.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample10MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample10_10751x4287.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void sample11MIActionPerformed(java.awt.event.ActionEvent evt) {
        originalFilePath = "Samples/sample11_13583x5417.jpg";
        insertImage(originalImage, originalFilePath, originalImageLabel);
    }

    private void selectImageMIActionPerformed(java.awt.event.ActionEvent evt) {

        openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images (.jpg, .jpeg, .png)", "jpg", "jpeg", "png"));

        int returnVal = openFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = openFileChooser.getSelectedFile();
            // What to do with the file, e.g. display it in a TextArea
            //          textarea.read( new FileReader( file.getAbsolutePath() ), null );
            originalFilePath = file.getAbsolutePath();
            insertImage(originalImage, originalFilePath, originalImageLabel);
        } else {
            System.out.println("File access cancelled by user.");
        }
    }

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {
        float factor = 1;
        if (operationsList.getSelectedValue() == null) {      // If nothing is chosen
            System.out.println("Please, choose all the parameters required first!");
        } else {

            if (operationsList.getSelectedValue().equals("Edge Detection")) {
                System.out.println("Edge Detection");
                kernelMatrix = new float[][]{
                    {-1, -1, -1},
                    {-1, 8, -1},
                    {-1, -1, -1}
                };
                factor = 1;
            } else if (operationsList.getSelectedValue().equals("Sharpen")) {
                System.out.println("Sharpen");
                kernelMatrix = new float[][]{
                    {0, -1, 0},
                    {-1, 5, -1},
                    {0, -1, 0}
                };
                factor = 1;
            } else if (operationsList.getSelectedValue().equals("Box Blur")) {
                System.out.println("Box Blur");
                kernelMatrix = new float[][]{
                    {1, 1, 1},
                    {1, 1, 1},
                    {1, 1, 1}
                };
                factor = 1f / 9f;
            } else if (operationsList.getSelectedValue().equals("Gaussian Blur 3x3")) {
                System.out.println("Gaussian Blur 3x3");
                kernelMatrix = new float[][]{
                    {1, 2, 1},
                    {2, 4, 2},
                    {1, 2, 1}
                };
                factor = 1f / 16f;
            } else if (operationsList.getSelectedValue().equals("Gaussian Blur 5x5")) {
                System.out.println("Gaussian Blur 5x5");
                kernelMatrix = new float[][]{
                    {1, 4, 6, 4, 1},
                    {4, 16, 24, 16, 4},
                    {6, 24, 36, 24, 6},
                    {4, 16, 24, 16, 4},
                    {1, 4, 6, 4, 1}
                };
                factor = 1f / 256f;
            } else if (operationsList.getSelectedValue().equals("Unsharp Masking 5x5")) {
                System.out.println("Unsharp Masking 5x5");
                kernelMatrix = new float[][]{
                    {1, 4, 6, 4, 1},
                    {4, 16, 24, 16, 4},
                    {6, 24, -476, 24, 6},
                    {4, 16, 24, 16, 4},
                    {1, 4, 6, 4, 1}
                };
                factor = -(1f / 256f);
            } else if (operationsList.getSelectedValue().equals("Motion Blur")) {
                System.out.println("Motion Blur");
                kernelMatrix = new float[][]{
                    {1, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0},
                    {0, 0, 1, 0, 0},
                    {0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 1}
                };
                factor = 0.2f;
            } else if (operationsList.getSelectedValue().equals("Emboss")) {
                System.out.println("Emboss");
                kernelMatrix = new float[][]{
                    //                    {-1, -1, 0},
                    //                    {-1, 0, 1},
                    //                    {0, 1, 1}
                    //                    это было так

                    {-2, -1, 0},
                    {-1, 1, 1},
                    {0, 1, 2}
                // а это я свой добавил, он вроде получше будет

                };
                factor = 1;
            }

            try {
                proceed(kernelMatrix, factor);
            } catch (IOException ex) {
                Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {

        // Эти фильтры не работают, т.е. когда их выбираешь, он не сохраняет в них
//        saveFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG", "jpg"));
//        saveFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png"));
        int returnVal = saveFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = saveFileChooser.getSelectedFile();
            // What to do with the file, e.g. display it in a TextArea
            //          textarea.read( new FileReader( file.getAbsolutePath() ), null );
            processedFilePath = file.getAbsolutePath();
            try {
                ImageIO.write(output, ext, new File(processedFilePath));
//            insertImage(originalImage, originalFilePath, originalImageLabel);
            } catch (IOException ex) {
                Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }



    public static void main() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Visual.class.getName()).log(Level.SEVERE, null, ex);
        }


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Visual().setVisible(true);
            }
        });

    }

    private void insertImage(ImageIcon ii, String filePath, javax.swing.JLabel label) {
        ii = new ImageIcon(new ImageIcon(filePath).getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH));
        label.setIcon(ii);
    }

    private void proceed(float[][] kernelMatrix, float factor) throws IOException, InterruptedException {

        if (originalFilePath != null) {

            if (mode == 1) {
                System.out.println("Entering sequential mode...");
                Sequential.process(originalFilePath, kernelMatrix, factor);
            } else if (mode == 2) {
                System.out.println("Entering parallel mode...");
                Parallel.process(originalFilePath, kernelMatrix, factor);
            } else if (mode == 3) {
                System.out.println("Entering distributed mode...");
                Distributed.process(originalFilePath, kernelMatrix, factor);
            }

            // Insert processed image and enable disabled "Save" button
            insertImage(processedImage, "Temp/temp.jpg", processedImageLabel);
            saveButton.setEnabled(true);
            // Release heap memory
            System.gc();

        } else {
            System.out.println("Original image is not chosen!");
        }

    }

    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JPanel Panel;
    private javax.swing.JRadioButtonMenuItem distributedMode;
    private javax.swing.JMenu imageMenu;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.ButtonGroup modesButtonGroup;
    private javax.swing.JMenu modesMenu;
    private javax.swing.JFileChooser openFileChooser;
    private javax.swing.JList<String> operationsList;
    private javax.swing.JScrollPane operationsScrollPane;
    private javax.swing.JLabel originalImageLabel;
    private javax.swing.JRadioButtonMenuItem parallelMode;
    private javax.swing.JLabel processedImageLabel;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem sample10MI;
    private javax.swing.JMenuItem sample11MI;
    private javax.swing.JMenuItem sample1MI;
    private javax.swing.JMenuItem sample2MI;
    private javax.swing.JMenuItem sample3MI;
    private javax.swing.JMenuItem sample4MI;
    private javax.swing.JMenuItem sample5MI;
    private javax.swing.JMenuItem sample6MI;
    private javax.swing.JMenuItem sample7MI;
    private javax.swing.JMenuItem sample8MI;
    private javax.swing.JMenuItem sample9MI;
    private javax.swing.JMenu samplesMenu;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveFileChooser;
    private javax.swing.JMenuItem selectImageMI;
    private javax.swing.JRadioButtonMenuItem sequentialMode;
    public static javax.swing.JLabel timeLabel;
}
