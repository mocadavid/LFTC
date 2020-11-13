package ro.ubb.FA;

//Class used in creating transitions
public class Pair {
    //State from which we go
    public String source;
    //State we reach
    public String destination;

    //constructor
    public Pair(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    /**
     * Constructs a display string with all the arguments for the pair class.
     * @return Concatenation of source and destination: String
     */
    @Override
    public String toString() {
        return source + " -> " + destination;
    }
}
