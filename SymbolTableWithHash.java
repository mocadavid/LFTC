package ro.ubb;

import java.util.HashSet;
import java.util.Set;

class SymbolTableWithHash<Key, Value> {

    private static class Node {
        private final Object key;
        private Object value;
        private Node next;

        public Node(Object key, Object value, Node next)  {
            this.key  = key; this.value = value; this.next = next;
        }
    }

    private Node remove(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            numberOfEntries--;
            return x.next;
        }
        x.next = remove(x.next, key);
        return x;
    }

    private static final int CAPACITY = 8;

    private int numberOfEntries;
    private int numberOfChains;
    private Node[] nodes;

    public SymbolTableWithHash() {
        this(CAPACITY);
    }

    public SymbolTableWithHash(int numberOfChains) {
        this.numberOfChains = numberOfChains;
        nodes = new Node[numberOfChains];
    }

    private void resize(int chains) {
        SymbolTableWithHash<Key, Value> temporary = new SymbolTableWithHash<>(chains);
        for (int i = 0; i < numberOfChains; i++) {
            for (Node x = nodes[i]; x != null; x = x.next) {
                temporary.put((Key) x.key, (Value) x.value);
            }
        }

        this.numberOfChains = temporary.numberOfChains;
        this.numberOfEntries = temporary.numberOfEntries;
        this.nodes = temporary.nodes;
    }

    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % numberOfChains;
    }

    public void put(Key key, Value value) {
        if (key == null){ throw new RuntimeException("Key is null in put!"); }
        if (value == null){ throw new RuntimeException("Value is null in put!"); }

        if (numberOfEntries >= 16* numberOfChains) resize(2* numberOfChains);

        int index = hash(key);

        for (Node x = nodes[index]; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.value = value;
                return;
            }
        }

        numberOfEntries++;
        nodes[index] = new Node(key, value, nodes[index]);
    }

    public void remove(Key key) {
        if (key == null){ throw new RuntimeException("Key is null for remove!"); }

        int i = hash(key);

        nodes[i] = remove(nodes[i], key);

        if (numberOfChains > CAPACITY && numberOfEntries <= 2* numberOfChains) resize(numberOfChains /2);
    }

    public Value get(Key key) {
        if (key == null){ throw new RuntimeException("Key from get is null!"); }
        int i = hash(key);
        for (Node x = nodes[i]; x != null; x = x.next) {
            if (key.equals(x.key)) return (Value) x.value;
        }
        return null;
    }

    public Iterable<Key> keys()  {
        Set<Key> set = new HashSet<>();
        for (int i = 0; i < numberOfChains; i++) {
            for (Node x = nodes[i]; x != null; x = x.next) {
                set.add((Key) x.key);
            }
        }
        return set;
    }

    public boolean contains(Key key) {
        if (key == null){ throw new RuntimeException("Key from contains is null!"); }
        return get(key) != null;
    }

    public int size() { return numberOfEntries; }
    public boolean isEmpty() { return numberOfEntries == 0; }
    public int index(Key key){ return hash(key); }
}
