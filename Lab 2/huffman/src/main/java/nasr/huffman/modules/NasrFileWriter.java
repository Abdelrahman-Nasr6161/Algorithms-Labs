package nasr.huffman.modules;

import java.io.*;
import java.util.*;
import nasr.huffman.helpers.ByteArrayKey;

public class NasrFileWriter {
    private static final int CHUNK_SIZE = 1024 * 1024 * 1024;

    public void writeCompressed(File inputFile, File outFile, int n, long fileSize,
            HashMap<ByteArrayKey, String> codes) throws IOException {
        long t1 = System.currentTimeMillis();

        try (FileOutputStream fos = new FileOutputStream(outFile);
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos, 65536))) {

            dos.writeInt(n);
            dos.writeInt(codes.size());

            for (Map.Entry<ByteArrayKey, String> e : codes.entrySet()) {
                byte[] keyBytes = e.getKey().getBytes();
                String codeStr = e.getValue();
                int bitLen = codeStr.length();

                dos.writeInt(keyBytes.length);
                dos.write(keyBytes);

                dos.writeInt(bitLen);

                int byteLen = (bitLen + 7) / 8;
                byte[] packed = new byte[byteLen];

                for (int i = 0; i < bitLen; i++) {
                    if (codeStr.charAt(i) == '1') {
                        int byteIndex = i / 8;
                        int bitOffset = 7 - (i % 8);
                        packed[byteIndex] |= (1 << bitOffset);
                    }
                }

                dos.write(packed);
            }
            
            dos.writeLong(fileSize);

            long t2 = System.currentTimeMillis();
            System.out.println("Write header: " + (t2 - t1) + " ms");

            long t3 = System.currentTimeMillis();

            try (FileInputStream fis = new FileInputStream(inputFile);
                    java.nio.channels.FileChannel inChannel = fis.getChannel()) {

                java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(CHUNK_SIZE);
                byte[] carry = new byte[0];
                byte[] block = new byte[n];
                byte[] outBuffer = new byte[8 * 1024 * 1024];
                int outPos = 0;
                int bitBuffer = 0;
                int bitCount = 0;

                while (inChannel.read(buffer) != -1) {
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

                        String code = codes.get(new ByteArrayKey(block));

                        for (int j = 0; j < code.length(); j++) {
                            bitBuffer = (bitBuffer << 1) | (code.charAt(j) == '1' ? 1 : 0);
                            bitCount++;

                            if (bitCount == 8) {
                                outBuffer[outPos++] = (byte) bitBuffer;
                                if (outPos == outBuffer.length) {
                                    dos.write(outBuffer);
                                    outPos = 0;
                                }
                                bitBuffer = 0;
                                bitCount = 0;
                            }
                        }
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

                if (bitCount > 0) {
                    outBuffer[outPos++] = (byte) (bitBuffer << (8 - bitCount));
                }

                if (outPos > 0) {
                    dos.write(outBuffer, 0, outPos);
                }

                dos.writeInt(carry.length);
                if (carry.length > 0) {
                    dos.write(carry);
                }
            }

            long t4 = System.currentTimeMillis();
            System.out.println("Encode and write compressed data: " + (t4 - t3) + " ms");
        }
    }
}