package nasr.huffman.modules;
import java.io.*;
import java.util.*;

public class Decompressor {
    private static final int CHUNK_SIZE = 1024 * 1024 * 1024;

    public void decompress(String inputPath) throws Exception {
        long startTime = System.currentTimeMillis();
        File inputFile = new File(inputPath);
        File parentDir = inputFile.getParentFile();

        long t1 = System.currentTimeMillis();
        int n;
        long originalLength;
        HashMap<String, byte[]> reverseMap = new HashMap<>();
        int maxCodeLen = 0;
        long compressedDataStart = 0;
        
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile), 65536))) {
            n = dis.readInt();
            int codeCount = dis.readInt();
            
            for (int i = 0; i < codeCount; i++) {
                int len = dis.readInt();
                byte[] keyBytes = new byte[len];
                dis.readFully(keyBytes);
                String code = dis.readUTF();
                reverseMap.put(code, keyBytes);
                maxCodeLen = Math.max(maxCodeLen, code.length());
            }

            originalLength = dis.readLong();
            compressedDataStart = 4 + 4 + 8;
            for (String code : reverseMap.keySet()) {
                compressedDataStart += 4 + n + 2 + code.length();
            }
        }
        
        long t2 = System.currentTimeMillis();
        System.out.println("Read header and code table: " + (t2 - t1) + " ms");

        String outputName = "extracted." + inputFile.getName().replace(".hc", "");
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(new File(parentDir, outputName));
             java.nio.channels.FileChannel inChannel = fis.getChannel();
             java.nio.channels.FileChannel outChannel = fos.getChannel()) {
            
            inChannel.position(compressedDataStart);
            
            // Calculate how many complete blocks we have
            long completeBlocks = originalLength / n;
            long expectedDecoded = completeBlocks * n;
            int remainderBytes = (int)(originalLength % n);
            
            java.nio.ByteBuffer readBuffer = java.nio.ByteBuffer.allocateDirect(CHUNK_SIZE);
            byte[] outputBuffer = new byte[CHUNK_SIZE];
            int outputPos = 0;
            long totalDecoded = 0;
            
            StringBuilder temp = new StringBuilder(maxCodeLen);
            
            long t3 = System.currentTimeMillis();
            
            // Decode complete blocks only
            while (inChannel.read(readBuffer) != -1 && totalDecoded < expectedDecoded) {
                readBuffer.flip();
                
                while (readBuffer.hasRemaining() && totalDecoded < expectedDecoded) {
                    byte b = readBuffer.get();
                    
                    for (int bit = 7; bit >= 0 && totalDecoded < expectedDecoded; bit--) {
                        temp.append((b & (1 << bit)) != 0 ? '1' : '0');
                        
                        String code = temp.toString();
                        byte[] block = reverseMap.get(code);
                        
                        if (block != null) {
                            int bytesToWrite = (int) Math.min(block.length, expectedDecoded - totalDecoded);
                            
                            for (int i = 0; i < bytesToWrite; i++) {
                                outputBuffer[outputPos++] = block[i];
                                totalDecoded++;
                                
                                if (outputPos == outputBuffer.length) {
                                    outChannel.write(java.nio.ByteBuffer.wrap(outputBuffer));
                                    outputPos = 0;
                                }
                            }
                            temp.setLength(0);
                            
                            if (totalDecoded >= expectedDecoded) {
                                break;
                            }
                        }
                    }
                }
                
                readBuffer.clear();
            }
            
            // Flush output buffer
            if (outputPos > 0) {
                outChannel.write(java.nio.ByteBuffer.wrap(outputBuffer, 0, outputPos));
                outputPos = 0;
            }
            
            long t4 = System.currentTimeMillis();
            System.out.println("Decode and write in chunks: " + (t4 - t3) + " ms");
            
            // Now read the remainder bytes at the end of the file
            long t5 = System.currentTimeMillis();
            try (RandomAccessFile raf = new RandomAccessFile(inputFile, "r")) {
                raf.seek(inputFile.length() - 4 - remainderBytes);
                int remainderCount = raf.readInt();
                
                if (remainderCount > 0 && remainderCount == remainderBytes) {
                    byte[] remainder = new byte[remainderCount];
                    raf.readFully(remainder);
                    outChannel.write(java.nio.ByteBuffer.wrap(remainder));
                }
            }
            long t6 = System.currentTimeMillis();
            System.out.println("Write remainder bytes: " + (t6 - t5) + " ms");
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total decompression time: " + (endTime - startTime) + " ms");
        System.out.println("Decompression complete: " + outputName);
    }
}
