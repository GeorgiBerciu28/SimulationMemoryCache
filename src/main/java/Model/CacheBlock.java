package Model;

public class CacheBlock {

    private int tag;
    private int[] dataBlock;
    private boolean valid;

    public CacheBlock() {
        this.valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public int getTag() {
        return tag;
    }

    public void setBlock(int tag,int[] blockData) {
        this.tag = tag;
        this.dataBlock= blockData;
        this.valid = true;
    }
    public int[] getBlock() {
        return dataBlock;
    }

    public void invalidate() {
        valid = false;
    }
}
