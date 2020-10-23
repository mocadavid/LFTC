package ro.ubb;

public class Pair {
    public String first;
    public int second;

    public Pair(String first, int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first='" + first + '\'' +
                ", second=" + second +
                '}'+"\n";
    }
}
