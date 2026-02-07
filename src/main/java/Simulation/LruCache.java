package Simulation;

import Model.Cache;
import java.util.LinkedList;
import java.util.List;

public class LruCache extends Cache {

    private List<Integer> usageList;

    public LruCache(int numLines) {
        super(numLines);
        usageList = new LinkedList<>();
    }


    public void updateUsage(int index) {
        usageList.remove(Integer.valueOf(index));
        usageList.add(index);
    }


    @Override
    public int find(int tag) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isValid() && lines[i].getTag() == tag) {
                updateUsage(i);
                return i;
            }
        }
        return -1;
    }


    @Override
    public void write(int index, int tag, int[] blockData) {
        super.write(index, tag, blockData);
        updateUsage(index);
    }
    @Override
    public void invalidate(int index) {
        super.invalidate(index);
        usageList.remove(Integer.valueOf(index));
    }


    public int selectVictim() {
        if (usageList.size() < size()) {

            for (int i = 0; i < size(); i++) {
                if (!usageList.contains(i)) {
                    usageList.add(i);
                    return i;
                }
            }
        }


        return usageList.remove(0);
    }
}
