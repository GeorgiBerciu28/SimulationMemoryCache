package Model;

public class MainMemory {
    private int[] mem;

    public MainMemory(int size) {
        mem = new int[size];

        for (int i = 0; i < size; i++) {
            mem[i] = i * 10;
        }
    }

    public int read(int address) {
        return mem[address % mem.length];
    }

    public void write(int address, int value) {
        mem[address % mem.length] = value;
    }
}
