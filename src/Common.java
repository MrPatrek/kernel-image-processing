
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Common {

    public static void formalFinish(String fileLocation, BufferedImage outputImg, long timeToPrepare, long timeElapsed) throws IOException {

        String fileOutputPath = "Temp/temp";

        String extOutput = getExtension(fileLocation);

        ImageIO.write(outputImg, extOutput, new File(fileOutputPath + ".jpg"));
        Visual.ext = extOutput;

        Visual.output = outputImg;

        String mode = null;
        if (Visual.mode == 1) {
            mode = "SEQUENTIAL mode:";
        } else if (Visual.mode == 2) {
            mode = "PARALLEL mode:";
        } else if (Visual.mode == 3) {
            mode = "DISTRIBUTED mode:";
        }

        Visual.timeLabel.setText("<html>" + mode + "<br>Prepar.: " + timeToPrepare + " ms<br>Image proc.: " + timeElapsed + " ms<br>" + "Total: " + (timeToPrepare + timeElapsed) + " ms<br></html>");

    }

    public static String getExtension(String fileLocation) {

        String lastChars = "";
        String extOutput;
        if (fileLocation.length() > 4) {
            lastChars = fileLocation.substring(fileLocation.length() - 4);
        }

        if (lastChars.equals(".jpg")) {
            extOutput = "JPG";
        } else if (lastChars.equals(".png")) {
            extOutput = "PNG";
        } else {

            if (fileLocation.length() > 5) {
                lastChars = fileLocation.substring(fileLocation.length() - 5);
            }

            if (lastChars.equals(".jpeg")) {
                extOutput = "JPG";
            } else {              // if nothing from above
                extOutput = "PNG";
            }
        }

        return extOutput;

    }

    // For MPI:
    public static float[] matrixToArray(float[][] matrix) {

        int newLen = matrix.length * matrix[0].length;
        float[] newArray = new float[newLen];

        int k = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                newArray[k] = matrix[i][j];
                k++;
            }
        }

        return newArray;

    }

    // For MPI:
    public static float[][] arrayToMatrix(float[] array) {

        int newLen = (int) Math.sqrt(array.length);
        float[][] newMatrix = new float[newLen][newLen];

        int k = 0;
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[0].length; j++) {
                newMatrix[i][j] = array[k];
                k++;
            }
        }

        return newMatrix;

    }

}
