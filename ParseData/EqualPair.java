package ParseData;

public class EqualPair<L,R>{

    private final L left;
    private final R right;

    public EqualPair(L left, R right){
        this.left = left;
        this.right = right;
    }

    public L getLeft(){return this.left;}
    public R getRight(){return this.right;}

    @Override
    public int hashCode() {return left.hashCode() ^ right.hashCode();}


    //This is done because I want the pairs (a,b) and (b,a) to be equal.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EqualPair)) return false;
        EqualPair otherEqualPair = (EqualPair) o;
        return (this.left.equals(otherEqualPair.getLeft()) &&
                this.right.equals(otherEqualPair.getRight())) ||
                (this.left.equals(otherEqualPair.getRight()) &&
                        this.right.equals(otherEqualPair.getLeft()));

    }

    @Override
    public String toString(){
        return left.toString() + " / " + right.toString();
    }
}
