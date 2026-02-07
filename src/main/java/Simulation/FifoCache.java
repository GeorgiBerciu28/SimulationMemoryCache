package Simulation;

import Model.Cache;

import java.util.LinkedList;
import java.util.Queue;

public class FifoCache extends Cache{
    private Queue<Integer> fifoQueue;

    public FifoCache(int numLines) {
        super(numLines);
        fifoQueue = new LinkedList<>();
    }

    public int selectVictim() {
        for (int i = 0; i < size(); i++) {
            if (!lines[i].isValid()) {
                fifoQueue.offer(i);
                return i;
            }
        }
        return fifoQueue.poll();
    }

    public void afterWrite(int index) {
        fifoQueue.remove(index);
        fifoQueue.offer(index);
    }
}
