package nasr.huffman.helpers;

import java.util.Arrays;

import lombok.Getter;


public class ByteArrayKey {
    @Getter
    final byte[] bytes;
    final int hash;
    
    public ByteArrayKey(byte[] bytes) {
        this.bytes = bytes;
        this.hash = Arrays.hashCode(bytes);
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteArrayKey)) return false;
        return Arrays.equals(bytes, ((ByteArrayKey) o).bytes);
    }
}