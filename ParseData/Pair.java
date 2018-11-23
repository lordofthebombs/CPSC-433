package ParseData;

public class Pair<L,R>{

    private final L left;
    private final R right;

    public Pair(L left, R right){
        this.left = left;
        this.right = right;
    }

    public L getLeft(){return this.left;}
    public R getRight(){return this.right;}

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode();}


    //This is done because I want thew pairs (a,b) and (b,a) to be equal.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair otherPair = (Pair) o;
        return (this.left.equals(otherPair.getLeft()) &&
                this.right.equals(otherPair.getRight())) ||
                (this.left.equals(otherPair.getRight()) &&
                        this.right.equals(otherPair.getLeft()));

    }
}
