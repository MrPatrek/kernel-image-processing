
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;

public class Sequential {

    // fileLocation is actually name of the file itself, but with path
    public static void process(String fileLocation, float[][] kernelMatrix, float multiplier) throws IOException {

        
        long startPrepare = System.currentTimeMillis();
        int kernelLen = kernelMatrix.length;

        BufferedImage inputImg = ImageIO.read(new File(fileLocation));              // the most expensive operation here
        int wid = inputImg.getWidth();
        int hgt = inputImg.getHeight();

        BufferedImage outputImg = new BufferedImage(wid, hgt, inputImg.getType());
        
        long finishPrepare = System.currentTimeMillis();
        long timeToPrepare = finishPrepare - startPrepare;

        long start = System.currentTimeMillis();            // start the image processing itself

        for (int a = 0; a < wid; a++) {
            for (int b = 0; b < hgt; b++) {
                
                float redFloat = 0f;
                float greenFloat = 0f;
                float blueFloat = 0f;
                
                for (int m = 0; m < kernelLen; m++) {
                    for (int n = 0; n < kernelLen; n++) {
                        
                        // here when calculating coordinates, if this is the edge of image, then choose the pixel from the opposite side
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
                
                // do not allow it to be lower than 0 or greater than 255
                int redOutput = Math.min(Math.max((int) (redFloat * multiplier), 0), 255);
                int greenOutput = Math.min(Math.max((int) (greenFloat * multiplier), 0), 255);
                int blueOutput = Math.min(Math.max((int) (blueFloat * multiplier), 0), 255);
                
                // Set the pixel to the image
                Color color = new Color(redOutput, greenOutput, blueOutput);
                outputImg.setRGB(a, b, color.getRGB());
            }
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        Common.formalFinish(fileLocation, outputImg,timeToPrepare, timeElapsed);
        
        // Release heap memory
        outputImg.flush();
        outputImg = null;
        System.gc();
        
    }

}
