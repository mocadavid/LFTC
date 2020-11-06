package ro.ubb.FA;

public class Pair {
    public String source;
    public String destination;

    public Pair(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return source + " -> " + destination;
    }
}
