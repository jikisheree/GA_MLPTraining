/**
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */
public class Pair<F, S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


    public F getFirst(){
        return first;
    }

    public S getSecond(){
        return second;
    }

    public void setFirst(F first){
        this.first = first;
    }

    public void setSecond(S second){
        this.second = second;
    }

}