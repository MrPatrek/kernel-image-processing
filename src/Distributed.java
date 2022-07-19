
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

import mpi.*;

public class Distributed {

    static int wid;
    static int hgt;
    static BufferedImage outputImg;

    // fileLocation is actually name of the file itself, but with path
    public static void process(String fileLocation, float[][] kernelMatrix, float multiplier) throws IOException, InterruptedException {
        
        
        long startPrepare = System.currentTimeMillis();

        Main.path = fileLocation.toCharArray();
        Main.charLen[0] = Main.path.length;

        MPI.COMM_WORLD.Bcast(Main.charLen, 0, 1, MPI.INT, Main.root);
        MPI.COMM_WORLD.Bcast(Main.path, 0, Main.charLen[0], MPI.CHAR, Main.root);

        
        

        Main.inputImg = ImageIO.read(new File(fileLocation));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(Main.inputImg, Common.getExtension(fileLocation), baos);
        Main.inputByted = baos.toByteArray();
        baos.close();
        
        Main.bytedLen[0] = Main.inputByted.length;
        MPI.COMM_WORLD.Bcast(Main.bytedLen, 0, 1, MPI.INT, Main.root);
        MPI.COMM_WORLD.Bcast(Main.inputByted, 0, Main.bytedLen[0], MPI.BYTE, Main.root);



        
        int kernelLen = kernelMatrix.length;
        
        wid = Main.inputImg.getWidth();
        hgt = Main.inputImg.getHeight();
        outputImg = new BufferedImage(wid, hgt, Main.inputImg.getType());

        
        MPI.COMM_WORLD.Bcast(new int[]{kernelMatrix.length}, 0, 1, MPI.INT, Main.root);


        
        Main.kernelARRAY = Common.matrixToArray(kernelMatrix);
        MPI.COMM_WORLD.Bcast(Main.kernelARRAY, 0, Main.kernelARRAY.length, MPI.FLOAT, Main.root);

        Main.commonWhatToWorkWith_int = new int[]{wid, hgt, kernelLen};
        MPI.COMM_WORLD.Bcast(Main.commonWhatToWorkWith_int, 0, Main.commonWhatToWorkWith_int.length, MPI.INT, Main.root);

        Main.commonWhatToWorkWith_float = new float[]{multiplier};
        MPI.COMM_WORLD.Bcast(Main.commonWhatToWorkWith_float, 0, Main.commonWhatToWorkWith_float.length, MPI.FLOAT, Main.root);

        
        
        int chunkLength = wid / Main.processes;

        int[] bags = new int[Main.processes];
        Arrays.fill(bags, chunkLength);                     // distribute elements
        for (int rest = wid % Main.processes; rest > 0; rest--) {
            bags[bags.length - rest] += 1;                  // distribute the rest of them
        }

        int[] rangeValues = new int[Main.processes];
        for (int i = 0; i < bags.length; i++) {
            for (int j = 0; j <= i; j++) {
                rangeValues[i] += bags[j];
            }
        }
        
        
        // Launching MPI processes with evenly distributed chunks:
        Main.allRanges = new int[2 * Main.processes];
        Main.allRanges[0] = 0;
        Main.allRanges[1] = rangeValues[0] - 1;         // setting first two parts of one range manually
        int k = 2;
        for (int i = 0; i < Main.processes - 1; i++) {
            Main.allRanges[k] = rangeValues[i];
            Main.allRanges[k+1] = rangeValues[i + 1] - 1;
            k = k + 2;
        }
        MPI.COMM_WORLD.Scatter(Main.allRanges, 0, 2, MPI.INT, Main.personalRange, 0, 2, MPI.INT, Main.root);        // each process gets its own personal range
        
        
        
        long finishPrepare = System.currentTimeMillis();
        long timeToPrepare = finishPrepare - startPrepare;

        long start = System.currentTimeMillis();
        
        
        
        
        processDistributedForRoot(Main.inputImg, kernelMatrix, Main.personalRange[0], Main.personalRange[1],
                        Main.commonWhatToWorkWith_int[0], Main.commonWhatToWorkWith_int[1], Main.commonWhatToWorkWith_int[2], Main.commonWhatToWorkWith_float[0]);

        for (int i = 1; i < Main.processes; i++) {
            
            MPI.COMM_WORLD.Recv(Main.reservedID, 0, Main.reservedID.length, MPI.INT, MPI.ANY_TAG, Main.tag);
            int currentRank = Main.reservedID[0];
            int from = Main.reservedID[1];
            int to = Main.reservedID[2];
            int range = to - from + 1;
            Main.longRgbSet = new int [5 * hgt * range];
            
            MPI.COMM_WORLD.Recv(Main.longRgbSet, 0, Main.longRgbSet.length, MPI.INT, currentRank, Main.tag);
            
            for (int j = 0; j < Main.longRgbSet.length / 5; j++) {
                outputImg.setRGB(Main.longRgbSet[j * 5 + 0], Main.longRgbSet[j * 5 + 1], new Color(Main.longRgbSet[j * 5 + 2], Main.longRgbSet[j * 5 + 3], Main.longRgbSet[j * 5 + 4]).getRGB());
            }

        }


        

        
        

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        Common.formalFinish(fileLocation, outputImg, timeToPrepare, timeElapsed);
        
     // Release heap memory
        outputImg.flush();
        outputImg = null;
        System.gc();

    }

    public static void processDistributed(BufferedImage img, float[][] kernelMatrix, int from, int to, int wid, int hgt, int kernelLen, float multiplier) {
        

        int range = to - from + 1;
        int startRangeFrom = 0;
        Main.longRgbSet = new int[5 * hgt * range];

        for (int a = from; a <= to; a++) {

            for (int b = 0; b < hgt; b++) {

                float redFloat = 0f;
                float greenFloat = 0f;
                float blueFloat = 0f;

                for (int m = 0; m < kernelLen; m++) {
                    for (int n = 0; n < kernelLen; n++) {

                        int aCoordinate = (a - kernelLen / 2 + m + wid) % wid;
                        int bCoordinate = (b - kernelLen / 2 + n + hgt) % hgt;

                        int rgbTotal = img.getRGB(aCoordinate, bCoordinate);
                        
                        int rgbRed = (rgbTotal >> 16) & 0xff;
                        int rgbGreen = (rgbTotal >> 8) & 0xff;
                        int rgbBlue = (rgbTotal) & 0xff;

                        redFloat += (rgbRed * kernelMatrix[m][n]);
                        greenFloat += (rgbGreen * kernelMatrix[m][n]);
                        blueFloat += (rgbBlue * kernelMatrix[m][n]);
                    }
                }

                int redOutput = Math.min(Math.max((int) (redFloat * multiplier), 0), 255);
                int greenOutput = Math.min(Math.max((int) (greenFloat * multiplier), 0), 255);
                int blueOutput = Math.min(Math.max((int) (blueFloat * multiplier), 0), 255);

                // Insert into data array:
                Main.longRgbSet[(startRangeFrom + b) * 5 + 0] = a;
                Main.longRgbSet[(startRangeFrom + b) * 5 + 1] = b;
                Main.longRgbSet[(startRangeFrom + b) * 5 + 2] = redOutput;
                Main.longRgbSet[(startRangeFrom + b) * 5 + 3] = greenOutput;
                Main.longRgbSet[(startRangeFrom + b) * 5 + 4] = blueOutput;
                

            }
            

            startRangeFrom += hgt;            // Важно!

        }
        
        Main.reservedID[0] = Main.rank;
        Main.reservedID[1] = from;
        Main.reservedID[2] = to;
        
        
        MPI.COMM_WORLD.Send(Main.reservedID, 0, Main.reservedID.length, MPI.INT, Main.root, Main.tag);

        // Pixels are sent to the root node:
        MPI.COMM_WORLD.Send(Main.longRgbSet, 0, Main.longRgbSet.length, MPI.INT, Main.root, Main.tag);

    }
    
    public static void processDistributedForRoot (BufferedImage img, float[][] kernelMatrix, int from, int to, int wid, int hgt, int kernelLen, float multiplier) {
        
        for (int a = from; a <= to; a++) {

            for (int b = 0; b < hgt; b++) {

                float redFloat = 0f;
                float greenFloat = 0f;
                float blueFloat = 0f;

                for (int m = 0; m < kernelLen; m++) {
                    for (int n = 0; n < kernelLen; n++) {

                        int aCoordinate = (a - kernelLen / 2 + m + wid) % wid;
                        int bCoordinate = (b - kernelLen / 2 + n + hgt) % hgt;

                        int rgbTotal = img.getRGB(aCoordinate, bCoordinate);
                        
                        int rgbRed = (rgbTotal >> 16) & 0xff;
                        int rgbGreen = (rgbTotal >> 8) & 0xff;
                        int rgbBlue = (rgbTotal) & 0xff;

                        redFloat += (rgbRed * kernelMatrix[m][n]);
                        greenFloat += (rgbGreen * kernelMatrix[m][n]);
                        blueFloat += (rgbBlue * kernelMatrix[m][n]);
                    }
                }
                
                int redOutput = Math.min(Math.max((int) (redFloat * multiplier), 0), 255);
                int greenOutput = Math.min(Math.max((int) (greenFloat * multiplier), 0), 255);
                int blueOutput = Math.min(Math.max((int) (blueFloat * multiplier), 0), 255);

                Color color = new Color(redOutput, greenOutput, blueOutput);
                outputImg.setRGB(a, b, color.getRGB());
                

            }
            


        }
        

    }

}
