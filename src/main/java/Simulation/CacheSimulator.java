package Simulation;

import Model.Cache;
import Model.CacheBlock;
import Model.MainMemory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CacheSimulator {

    private Cache cache;
    private MainMemory memory;

    private int hits = 0;
    private int misses = 0;
    private static double savedFifoHitRate = -1;
    private static double savedFifoMissRate = -1;

    private static double savedLruHitRate = -1;
    private static double savedLruMissRate = -1;

    private PrintWriter logWriter;

    private String type;

    public CacheSimulator(String cacheType, int cacheLines) {

        this.type = cacheType;

        if (cacheType.equals("FIFO")) {
            cache = new FifoCache(cacheLines);
        } else {
            cache = new LruCache(cacheLines);
        }

        memory = new MainMemory(1024);
    }


    public void startLogging() {

        String fileName;

        if (type.equals("FIFO")) {
            fileName = "rezultate_fifo.txt";
        } else {
            fileName = "rezultate_lru.txt";
        }

        try {
            logWriter = new PrintWriter(new FileWriter(fileName));
            logWriter.println("=== SIMULARE CACHE ===");
            logWriter.println("Politică: " + type);
            logWriter.println();
        } catch (IOException e) {
            System.err.println("Eroare la deschiderea fisierului: " + e.getMessage());
        }
    }


    public void stopLogging() {
        if (logWriter != null) {

            logWriter.println();
            logWriter.println("HIT-uri: " + hits);
            logWriter.println("MISS-uri: " + misses);
            logWriter.printf("Rată HIT: %.2f%%%n", getHitRate());

            logWriter.close();
            logWriter = null;
        }
        if (type.equals("FIFO")) {
            savedFifoHitRate = getHitRate();
            savedFifoMissRate = getMissRate();
        } else {
            savedLruHitRate = getHitRate();
            savedLruMissRate = getMissRate();
        }
    }

    private void log(String msg) {
        System.out.println(msg);
        if (logWriter != null) logWriter.println(msg);
    }



    public boolean writeData(int address, int value) {

        int blockSize = 4;
        int tag = address / blockSize;
        int offset = address % blockSize;


        int index = cache.find(tag);

        if (index >= 0) {
            hits++;
            cache.getLines()[index].getBlock()[offset] = value;
            memory.write(address, value);



            log("WRITE HIT (" + type + ") - linia " + index);
            printCacheState();

            printMainMemory(address, address);
            return true;
        }


        misses++;
        log("WRITE MISS (" + type + ") - adresa " + address);
        printCacheState();

        int[] block = new int[blockSize];
        int base = tag * blockSize;
        for (int i = 0; i < blockSize; i++)
            block[i] = memory.read(base + i);


        int victim;

        if (cache instanceof FifoCache fc) {
            victim = fc.selectVictim();
            fc.write(victim, tag, block);
            fc.afterWrite(victim);
        } else {
            LruCache lc = (LruCache) cache;
            victim = lc.selectVictim();
            lc.write(victim, tag, block);
        }


        cache.getLines()[victim].getBlock()[offset] = value;
        memory.write(address, value);

        log("Bloc TAG=" + tag + " -> pus în linia " + victim);
        printCacheState();

        printMainMemory(address, address);
        return false;
    }


    public boolean access(int address) {

        int blockSize = 4;
        int tag = address / blockSize;


        int index = cache.find(tag);

        if (index >= 0) {
            hits++;
            log("HIT (" + type + ") - linia " + index);
            return true;
        }


        misses++;

        int[] block = new int[blockSize];
        int base = tag * blockSize;
        for (int i = 0; i < blockSize; i++)
            block[i] = memory.read(base + i);

        int victim;

        if (cache instanceof FifoCache fc) {
            victim = fc.selectVictim();
            fc.write(victim, tag, block);
            fc.afterWrite(victim);
        } else {
            LruCache lc = (LruCache) cache;
            victim = lc.selectVictim();
            lc.write(victim, tag, block);
        }

        log("MISS (" + type + ") - pus în linia " + victim);
        printCacheState();
        return false;
    }

    public void evict(int address) {

        int blockSize = 4;
        int tag = address / blockSize;

        int index = cache.find(tag);
        if (index >= 0) {
            cache.invalidate(index);
            log("EVICT (" + type + ") linia " + index);
            printCacheState();
        }
    }

    public CacheBlock[] getCacheLines() {
        return cache.getLines();
    }

    public int getHits() { return hits; }
    public int getMisses() { return misses; }

    public double getHitRate() {
        int total = hits + misses;
        return total == 0 ? 0 : (hits * 100.0) / total;
    }

    public double getMissRate() {
        int total = hits + misses;
        return total == 0 ? 0 : (misses * 100.0) / total;
    }

    public int getMemoryValue(int address) {
        return memory.read(address);
    }
    public double getSavedFifoHitRate() { return savedFifoHitRate; }
    public double getSavedFifoMissRate() { return savedFifoMissRate; }

    public double getSavedLruHitRate() { return savedLruHitRate; }
    public double getSavedLruMissRate() { return savedLruMissRate; }
    public void printCacheState() {
        StringBuilder sb = new StringBuilder("\n=== STAREA CURENTĂ A CACHE-ULUI ===\n");
        CacheBlock[] lines = cache.getLines();

        for (int i = 0; i < lines.length; i++) {
            CacheBlock block = lines[i];

            if (block.isValid()) {
                sb.append("Linia ").append(i)
                        .append(" | TAG=").append(block.getTag())
                        .append(" | Bloc: ");

                int[] data = block.getBlock();
                for (int value : data) {
                    sb.append(value).append(" ");
                }
                sb.append("\n");
            } else {
                sb.append("Linia ").append(i).append(" | INVALIDĂ\n");
            }
        }
        log(sb.toString());
    }
    public void printMainMemory(int start, int end) {
        StringBuilder sb = new StringBuilder("\n=== FRAGMENT DIN MEMORIA PRINCIPALĂ ===\n");

        for (int i = start; i <= end; i++) {
            sb.append("Adresa ").append(i)
                    .append(" → valoare: ").append(memory.read(i))
                    .append("\n");
        }
        log(sb.toString());
    }
}
