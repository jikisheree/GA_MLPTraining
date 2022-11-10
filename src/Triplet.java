/**
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */
public class Triplet<F, S, T> {
    public F first;
    public S second;
    public T third;

    public Triplet(F first) {
        this.first = first;
    }


    public F getFirst(){
        return first;
    }

    public S getSecond(){
        return second;
    }

    public T getThird(){
        return third;
    }

    public void setFirst(F first){
        this.first = first;
    }

    public void setSecond(S second){
        this.second = second;
    }

    public void setThird(T third){
        this.third = third;
    }
}