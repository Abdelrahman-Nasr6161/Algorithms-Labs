package nasr.huffman.modules;

import java.io.*;
import java.util.*;

import nasr.huffman.helpers.ByteArrayKey;

public class Compressor {
    private final String studentId;

    public Compressor(String studentId) {
        this.studentId = studentId;
    }

    public void compress(String inputPath, int n) throws Exception {
        long startTime = System.currentTimeMillis();
        File inputFile = new File(inputPath);
        long fileSize = inputFile.length();
        
        long t1 = System.currentTimeMillis();
        NasrFileReader reader = new NasrFileReader();
        HashMap<ByteArrayKey, Integer> freqMap = reader.buildFrequencyMap(inputFile, n);
        
        long t2 = System.currentTimeMillis();
        System.out.println("Read file and build frequency map: " + (t2 - t1) + " ms");

        long t3 = System.currentTimeMillis();
        HuffmanBuilder huffman = new HuffmanBuilder();
        HashMap<ByteArrayKey, String> codes = huffman.buildCodes(freqMap);
        
        long t4 = System.currentTimeMillis();
        System.out.println("Build Huffman tree and codes: " + (t4 - t3) + " ms");

        long t5 = System.currentTimeMillis();
        String compressedName = studentId + "." + n + "." + inputFile.getName() + ".hc";
        File outFile = new File(inputFile.getParentFile(), compressedName);
        
        NasrFileWriter writer = new NasrFileWriter();
        writer.writeCompressed(inputFile, outFile, n, fileSize, codes);
        
        long t6 = System.currentTimeMillis();
        System.out.println("Write compressed file: " + (t6 - t5) + " ms");

        long endTime = System.currentTimeMillis();
        System.out.println("Total compression time: " + (endTime - startTime) + " ms");
        System.out.println("Compression complete: " + outFile.getAbsolutePath());
        System.out.println("Original size: " + fileSize + " bytes");
        System.out.println("Compressed size: " + outFile.length() + " bytes");
        System.out.printf("Compression ratio: %.2f%%\n", 100.0 * outFile.length() / fileSize);
    }
}