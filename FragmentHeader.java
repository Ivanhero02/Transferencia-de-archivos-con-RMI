import java.io.Serializable;

public class FragmentHeader implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int index;
    private long size;
    
    public FragmentHeader(int index, long size) {
        this.index = index;
        this.size = size;
    }
    
    public int getIndex() {
        return index;
    }
    
    public long getSize() {
        return size;
    }

}
