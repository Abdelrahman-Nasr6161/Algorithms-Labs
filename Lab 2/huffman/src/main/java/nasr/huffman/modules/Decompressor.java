package nasr.huffman.modules;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class Decompressor {

    private static final int CHUNK_SIZE = 1024 * 1024 * 1024;

    public void decompress(String inputPath) throws Exception {

        long start = System.currentTimeMillis();

        File inputFile = new File(inputPath);
        File parent = inputFile.getParentFile();

        long t1 = System.currentTimeMillis();

        int n;
        long originalLength;
        HashMap<String, byte[]> reverseMap = new HashMap<>();
        int maxCodeLen = 0;
        long compressedDataStart = 0;

        try (FileChannel headerChannel = FileChannel.open(inputFile.toPath(), java.nio.file.StandardOpenOption.READ)) {

            ByteBuffer intBuf = ByteBuffer.allocate(4);
            ByteBuffer longBuf = ByteBuffer.allocate(8);

            headerChannel.read(intBuf);
            intBuf.flip();
            n = intBuf.getInt();
            intBuf.clear();

            headerChannel.read(intBuf);
            intBuf.flip();
            int codeCount = intBuf.getInt();
            intBuf.clear();

            for (int i = 0; i < codeCount; i++) {
                headerChannel.read(intBuf);
                intBuf.flip();
                int keyLen = intBuf.getInt();
                intBuf.clear();

                byte[] key = new byte[keyLen];
                ByteBuffer kbuf = ByteBuffer.wrap(key);
                headerChannel.read(kbuf);

                headerChannel.read(intBuf);
                intBuf.flip();
                int bitLen = intBuf.getInt();
                intBuf.clear();

                int byteLen = (bitLen + 7) >>> 3;
                byte[] packed = new byte[byteLen];
                ByteBuffer pbuf = ByteBuffer.wrap(packed);
                headerChannel.read(pbuf);

                StringBuilder sb = new StringBuilder(bitLen);
                int bitsRead = 0;
                for (byte b : packed) {
                    for (int bit = 7; bit >= 0 && bitsRead < bitLen; bit--) {
                        sb.append(((b >>> bit) & 1) == 1 ? '1' : '0');
                        bitsRead++;
                    }
                }

                String code = sb.toString();

                reverseMap.put(code, key);
                maxCodeLen = Math.max(maxCodeLen, bitLen);

                compressedDataStart += 4 + keyLen + 4 + byteLen;
            }

            headerChannel.read(longBuf);
            longBuf.flip();
            originalLength = longBuf.getLong();
            longBuf.clear();

            compressedDataStart += 4 + 4 + 8;
        }

        long t2 = System.currentTimeMillis();
        System.out.println("Header read time: " + (t2 - t1) + " ms");

        String outputName = "extracted." + inputFile.getName().replace(".hc", "");

        try (FileChannel in = FileChannel.open(inputFile.toPath(), java.nio.file.StandardOpenOption.READ);
             FileChannel out = FileChannel.open(
                     new File(parent, outputName).toPath(),
                     java.nio.file.StandardOpenOption.CREATE,
                     java.nio.file.StandardOpenOption.WRITE,
                     java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {

            in.position(compressedDataStart);

            long completeBlocks = originalLength / n;
            long expectedDecoded = completeBlocks * n;
            int remainderBytes = (int) (originalLength % n);

            ByteBuffer readBuf = ByteBuffer.allocateDirect(CHUNK_SIZE);
            byte[] outputBuf = new byte[CHUNK_SIZE];
            int outPos = 0;

            long decoded = 0;
            StringBuilder temp = new StringBuilder(maxCodeLen);

            long t3 = System.currentTimeMillis();

            while (decoded < expectedDecoded) {
                readBuf.clear();
                if (in.read(readBuf) == -1)
                    break;
                readBuf.flip();

                while (readBuf.hasRemaining() && decoded < expectedDecoded) {
                    byte b = readBuf.get();

                    for (int bit = 7; bit >= 0 && decoded < expectedDecoded; bit--) {
                        temp.append(((b >> bit) & 1) == 1 ? '1' : '0');

                        byte[] block = reverseMap.get(temp.toString());
                        if (block != null) {
                            int writeCount = (int) Math.min(block.length, expectedDecoded - decoded);

                            for (int i = 0; i < writeCount; i++) {
                                outputBuf[outPos++] = block[i];
                                decoded++;

                                if (outPos == outputBuf.length) {
                                    out.write(ByteBuffer.wrap(outputBuf));
                                    outPos = 0;
                                }
                            }
                            temp.setLength(0);
                        }
                    }
                }
            }

            if (outPos > 0) {
                out.write(ByteBuffer.wrap(outputBuf, 0, outPos));
            }

            long t4 = System.currentTimeMillis();
            System.out.println("Decoded main section: " + (t4 - t3) + " ms");

            long t5 = System.currentTimeMillis();

            long footerPos = inputFile.length() - 4 - remainderBytes;
            in.position(footerPos);

            ByteBuffer remCountBuf = ByteBuffer.allocate(4);
            in.read(remCountBuf);
            remCountBuf.flip();
            int remCount = remCountBuf.getInt();

            if (remCount == remainderBytes && remCount > 0) {
                ByteBuffer remBuf = ByteBuffer.allocate(remCount);
                in.read(remBuf);
                remBuf.flip();
                out.write(remBuf);
            }

            long t6 = System.currentTimeMillis();
            System.out.println("Write remainder bytes: " + (t6 - t5) + " ms");
        }

        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + " ms");
        System.out.println("Done -> " + outputName);
    }
}