package nasr.huffman.modules;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class NasrFileComparator {

    private static final int CHUNK_SIZE = 1024 * 1024 * 1024; 

    public static boolean areFilesIdentical(String path1, String path2) throws IOException {
        File file1 = new File(path1);
        File file2 = new File(path2);

        if (file1.length() != file2.length()) {
            return false;
        }

        try {
            MessageDigest md1 = MessageDigest.getInstance("SHA-256");
            MessageDigest md2 = MessageDigest.getInstance("SHA-256");

            try (FileInputStream fis1 = new FileInputStream(file1);
                 FileInputStream fis2 = new FileInputStream(file2);
                 FileChannel ch1 = fis1.getChannel();
                 FileChannel ch2 = fis2.getChannel()) {

                ByteBuffer buffer1 = ByteBuffer.allocateDirect(CHUNK_SIZE);
                ByteBuffer buffer2 = ByteBuffer.allocateDirect(CHUNK_SIZE);

                while (true) {
                    buffer1.clear();
                    buffer2.clear();

                    int read1 = ch1.read(buffer1);
                    int read2 = ch2.read(buffer2);

                    if (read1 == -1 && read2 == -1) break;
                    if (read1 != read2) return false;

                    buffer1.flip();
                    buffer2.flip();

                    byte[] chunk1 = new byte[read1];
                    byte[] chunk2 = new byte[read2];

                    buffer1.get(chunk1);
                    buffer2.get(chunk2);

                    md1.update(chunk1);
                    md2.update(chunk2);
                }
            }

            return Arrays.equals(md1.digest(), md2.digest());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }
}
