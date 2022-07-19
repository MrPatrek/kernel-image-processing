
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.util.Arrays;


public class Parallel {

    static float[][] kernelMatrix;
    static float multiplier;
    static int kernelLen;
    static int wid;
    static int hgt;
    static BufferedImage inputImg;
    static BufferedImage outputImg;     // SHARED VARIABLE!

    static class MyThread extends Thread {

        int start;
        int end;

        MyThread(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run() {

            processParallelOrDistributed(start, end);

        }

    }

    public static void processParallelOrDistributed(int from, int to) {

        for (int a = from; a <= to; a++) {

            for (int b = 0; b < hgt; b++) {

                float redFloat = 0f;
                float greenFloat = 0f;
                float blueFloat = 0f;

                for (int m = 0; m < kernelLen; m++) {
                    for (int n = 0; n < kernelLen; n++) {

                        int aCoordinate = (a - kernelLen / 2 + m + wid) % wid;
                        int bCoordinate = (b - kernelLen / 2 + n + hgt) % hgt;

                        int rgbTotal = inputImg.getRGB(aCoordinate, bCoordinate);
                        
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

    // fileLocation is actually name of the file itself, but with path
    public static void process(String fileLocation, float[][] kernelMatrix, float multiplier) throws IOException, InterruptedException {

        
        long startPrepare = System.currentTimeMillis();
        Parallel.kernelLen = kernelMatrix.length;

        Parallel.inputImg = ImageIO.read(new File(fileLocation));
        Parallel.wid = inputImg.getWidth();
        Parallel.hgt = inputImg.getHeight();

        Parallel.outputImg = new BufferedImage(wid, hgt, inputImg.getType());
        Parallel.kernelMatrix = kernelMatrix;
        Parallel.multiplier = multiplier;
        
        long finishPrepare = System.currentTimeMillis();
        long timeToPrepare = finishPrepare - startPrepare;

        long start = System.currentTimeMillis();


        
        
        // Parallel part begins here:
        int threads = Runtime.getRuntime().availableProcessors();
        MyThread[] thread = new MyThread[threads];
        int chunkLength = wid / threads;

        int[] bags = new int[threads];
        Arrays.fill(bags, chunkLength);                     // distribute elements
        for (int rest = wid % threads; rest > 0; rest--) {
            bags[bags.length - rest] += 1;                  // distribute the rest of them
        }

        int[] rangeValues = new int[threads];
        for (int i = 0; i < bags.length; i++) {
            for (int j = 0; j <= i; j++) {
                rangeValues[i] += bags[j];
            }
        }
        
        // Launching threads with evenly distributed chunks:
        thread[0] = new MyThread(0, rangeValues[0] - 1);
        thread[0].start();                      // Launchiung the first thread manually
        for (int i = 0; i < threads - 1; i++) {
            thread[i+1] = new MyThread(rangeValues[i], rangeValues[i + 1] - 1);
            thread[i+1].start();
        }
        
        
        // Finishing threads:
        for (int i = 0; i < threads; i++) {
            thread[i].join();
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        Common.formalFinish(fileLocation, outputImg, timeToPrepare, timeElapsed);
        
        // Release heap memory
        outputImg.flush();
        outputImg = null;
        System.gc();

    }

}
