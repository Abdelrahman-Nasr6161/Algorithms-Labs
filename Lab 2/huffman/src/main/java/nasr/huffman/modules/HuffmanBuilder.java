package nasr.huffman.modules;

import java.util.*;

import nasr.huffman.helpers.ByteArrayKey;

public class HuffmanBuilder {
    
    static class Node implements Comparable<Node> {
        byte[] bytes;
        int freq;
        Node left, right;

        Node(byte[] b, int freq) { this.bytes = b; this.freq = freq; }
        Node(int freq) { this.bytes = null; this.freq = freq; }
        
        @Override
        public int compareTo(Node o) { return Integer.compare(this.freq, o.freq); }
    }

    public HashMap<ByteArrayKey, String> buildCodes(HashMap<ByteArrayKey, Integer> freqMap) {
        long t1 = System.currentTimeMillis();
        
        PriorityQueue<Node> pq = new PriorityQueue<>(freqMap.size());
        for (Map.Entry<ByteArrayKey, Integer> entry : freqMap.entrySet()) {
            pq.add(new Node(entry.getKey().getBytes(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        long t2 = System.currentTimeMillis();
        System.out.println("Build Huffman tree: " + (t2 - t1) + " ms");

        long t3 = System.currentTimeMillis();
        Node root = pq.poll();
        HashMap<ByteArrayKey, String> codes = new HashMap<>(freqMap.size());
        dfs(root, "", codes);

        long t4 = System.currentTimeMillis();
        System.out.println("Generate Huffman codes: " + (t4 - t3) + " ms");
        
        return codes;
    }

    private void dfs(Node node, String code, HashMap<ByteArrayKey, String> codes) {
        if (node.left != null) dfs(node.left, code + "0", codes);
        if (node.right != null) dfs(node.right, code + "1", codes);
        if (node.left == null && node.right == null && node.bytes != null) {
            codes.put(new ByteArrayKey(node.bytes), code);
        }
    }
}