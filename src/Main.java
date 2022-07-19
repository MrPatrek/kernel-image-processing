
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import mpi.*;

public class Main {

    // For MPI only:
    static int rank;
    static int processes;
    static int root = 0;		// root process
    static int charLen[] = new int[1];
    static char[] path;
    static String originalFilePath;
    static BufferedImage inputImg;
    static byte[] inputByted;
    static int[] bytedLen = new int[1];
    static float[] kernelARRAY;
    static int[] commonWhatToWorkWith_int = new int[3];
    static float[] commonWhatToWorkWith_float = new float[1];
    static int[] personalRange = new int[2];
    static int[] allRanges;
    static int tag = 777;
    static int[] reservedID = new int[]{-345, -456, -567};
    static int[] longRgbSet;

    public static void main(String[] args) throws InterruptedException, IOException {

        MPI.Init(args);
        rank = MPI.COMM_WORLD.Rank();
        processes = MPI.COMM_WORLD.Size();

        // code for root only
        if (rank == root) {
            Visual.main();
        }

        // code for non-root processes only:
        if (rank != root) {

            while (true) {

                MPI.COMM_WORLD.Bcast(charLen, 0, 1, MPI.INT, root);
                path = new char[charLen[0]];
                MPI.COMM_WORLD.Bcast(path, 0, path.length, MPI.CHAR, root);

                originalFilePath = new String(path);





                
                MPI.COMM_WORLD.Bcast(Main.bytedLen, 0, 1, MPI.INT, Main.root);
                Main.inputByted = new byte[Main.bytedLen[0]];           // НЕ ЗАБЫВАЙ ЕГО СРАЗУ ИНИЦИАЛИЗИРОВАТЬ С ПОЛУЧЕННЫМ РАЗМЕРОМ ! ! !
                MPI.COMM_WORLD.Bcast(Main.inputByted, 0, Main.bytedLen[0], MPI.BYTE, Main.root);
                InputStream is = new ByteArrayInputStream(Main.inputByted);
                inputImg = ImageIO.read(is);
                is.close();

                

                
                
                int[] kernelLen = new int[1];
                MPI.COMM_WORLD.Bcast(kernelLen, 0, 1, MPI.INT, root);

                kernelARRAY = new float[kernelLen[0] * kernelLen[0]];
                MPI.COMM_WORLD.Bcast(kernelARRAY, 0, kernelARRAY.length, MPI.FLOAT, root);

                MPI.COMM_WORLD.Bcast(commonWhatToWorkWith_int, 0, commonWhatToWorkWith_int.length, MPI.INT, root);
                MPI.COMM_WORLD.Bcast(commonWhatToWorkWith_float, 0, commonWhatToWorkWith_float.length, MPI.FLOAT, root);

                float[][] matr = Common.arrayToMatrix(kernelARRAY);
                
                allRanges = new int[2 * processes];
                
                MPI.COMM_WORLD.Scatter(Main.allRanges, 0, 2, MPI.INT, Main.personalRange, 0, 2, MPI.INT, Main.root);        // each process gets its own perosnal range

                Distributed.processDistributed(inputImg, matr, personalRange[0], personalRange[1],
                        commonWhatToWorkWith_int[0], commonWhatToWorkWith_int[1], commonWhatToWorkWith_int[2], commonWhatToWorkWith_float[0]);

                // Release heap memory
                inputImg.flush();
                inputImg = null;
                System.gc();
                
            }

        }

    }

}
