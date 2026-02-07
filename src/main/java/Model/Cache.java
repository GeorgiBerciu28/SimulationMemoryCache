package Model;

public class Cache {

    protected CacheBlock[] lines;

    public Cache(int numLines) {
        lines = new CacheBlock[numLines];
        for (int i = 0; i < numLines; i++) {
            lines[i] = new CacheBlock();
        }
    }


    public int find(int tag) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isValid() && lines[i].getTag() == tag) {
                return i;
            }
        }
        return -1;
    }


    public void write(int index, int tag, int[] blockData) {
        lines[index].setBlock(tag, blockData);
    }


    public void invalidate(int index) {
        lines[index].invalidate();
    }

    public int size() {
        return lines.length;
    }

    public CacheBlock[] getLines() {
        return lines;
    }
}
