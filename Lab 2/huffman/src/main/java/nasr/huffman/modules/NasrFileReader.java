package nasr.huffman.modules;
import java.io.*;
import java.util.*;
import nasr.huffman.helpers.ByteArrayKey;

public class NasrFileReader {
    private static final int CHUNK_SIZE = 1024 * 1024 * 1024;
    
    public HashMap<ByteArrayKey, Integer> buildFrequencyMap(File inputFile, int n) throws IOException {
        HashMap<ByteArrayKey, Integer> freqMap = new HashMap<>(1024 * 1024);
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             java.nio.channels.FileChannel channel = fis.getChannel()) {
            
            java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(CHUNK_SIZE);
            byte[] carry = new byte[0];
            byte[] block = new byte[n];
            
            while (channel.read(buffer) != -1) {
                buffer.flip();
                int read = buffer.remaining();
                byte[] chunk = new byte[read];
                buffer.get(chunk);
                buffer.clear();
                
                int totalLen = carry.length + read;
                int processable = (totalLen / n) * n;
                
                int carryIdx = 0;
                int chunkIdx = 0;
                
                for (int pos = 0; pos < processable; pos += n) {
                    for (int i = 0; i < n; i++) {
                        if (carryIdx < carry.length) {
                            block[i] = carry[carryIdx++];
                        } else {
                            block[i] = chunk[chunkIdx++];
                        }
                    }
                    
                    ByteArrayKey key = new ByteArrayKey(block.clone());
                    freqMap.merge(key, 1, Integer::sum);
                }
                
                int remaining = totalLen - processable;
                if (remaining > 0) {
                    byte[] newCarry = new byte[remaining];
                    int idx = 0;
                    while (carryIdx < carry.length && idx < remaining) {
                        newCarry[idx++] = carry[carryIdx++];
                    }
                    while (chunkIdx < chunk.length && idx < remaining) {
                        newCarry[idx++] = chunk[chunkIdx++];
                    }
                    carry = newCarry;
                } else {
                    carry = new byte[0];
                }
            }

        }
        
        return freqMap;
    }
}