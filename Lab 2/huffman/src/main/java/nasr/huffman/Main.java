package nasr.huffman;

import nasr.huffman.modules.Compressor;
import nasr.huffman.modules.Decompressor;
import nasr.huffman.modules.NasrFileComparator;

public class Main {
    private static final String STUDENT_ID = "22010887";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage:");
            System.out.println("  Compress: java -jar huffman_22010887.jar c <input_file> <n_bytes>");
            System.out.println("  Decompress: java -jar huffman_22010887.jar d <input_file>");
            System.out.println("  Compare: java -jar huffman_22010887.jar m <input_file1> <input_file2>");
            return;
        }

        String mode = args[0];

        switch (mode.toLowerCase()) {
            case "c": {
                if (args.length < 3) {
                    System.out.println("Missing parameters for compression.");
                    return;
                }
                String inputPath = args[1];
                int n = Integer.parseInt(args[2]);
                new Compressor(STUDENT_ID).compress(inputPath, n);
                break;
            }

            case "d": {
                String inputPath = args[1];
                new Decompressor().decompress(inputPath);
                break;
            }

            case "m": {  // Compare mode
                if (args.length < 3) {
                    System.out.println("Missing parameters for compare.");
                    return;
                }
                String file1 = args[1];
                String file2 = args[2];

                boolean identical = NasrFileComparator.areFilesIdentical(file1, file2);

                System.out.println(identical ? "Files are IDENTICAL" : "Files are DIFFERENT");
                break;
            }

            default:
                System.out.println("Unknown mode: " + mode);
        }
    }
}
