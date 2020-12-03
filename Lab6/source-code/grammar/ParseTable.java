package ro.ubb.grammar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the parsing table.
 * Key is formed from the pair row head and column head
 * Value is formed from the pair of production result symbols and its number.
 */
public class ParseTable {
    private Map<Pair<String, String>, Pair<List<String>, Integer>> table = new HashMap<>();

    /**
     * Adds a new entry in the parsing table
     * @param key given row and column: Pair<String, String>
     * @param value formed by the production result symbols and its numbering: Pair<List<String>, Integer>
     */
    public void put(Pair<String, String> key, Pair<List<String>, Integer> value) {
        table.put(key, value);
    }

    /**
     * Gets the entry from the parsing table by its key.
     * @param key given row and column: Pair<String, String>
     * @return formed by the production result symbols and its numbering: Pair<List<String>, Integer>
     */
    public Pair<List<String>, Integer> get(Pair<String, String> key) {
        for (Map.Entry<Pair<String, String>, Pair<List<String>, Integer>> entry : table.entrySet()) {
            if (entry.getValue() != null) {
                Pair<String, String> currentKey = entry.getKey();
                Pair<List<String>, Integer> currentValue = entry.getValue();

                if (currentKey.getKey().equals(key.getKey()) && currentKey.getValue().equals(key.getValue())) {
                    return currentValue;
                }
            }
        }

        return null;
    }

    /**
     * Checks if a given key is already present in the parsing table.
     * @param key given row and column: Pair<String, String>
     * @return true if it is contained, false otherwise
     */
    public boolean containsKey(Pair<String, String> key) {
        boolean isContained = false;
        for (Pair<String, String> currentKey : table.keySet()) {
            if (currentKey.getKey().equals(key.getKey()) && currentKey.getValue().equals(key.getValue())) {
                isContained = true;
                break;
            }
        }

        return isContained;
    }

    /**
     * Transforms the parsing table into a readable string form of key value pairs.
     * @return the content of the parsing table :String
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<Pair<String, String>, Pair<List<String>, Integer>> entry : table.entrySet()) {
            if (entry.getValue() != null) {
                Pair<String, String> key = entry.getKey();
                Pair<List<String>, Integer> value = entry.getValue();

                stringBuilder.append("Pair[").append(key.getKey()).append(",").append(key.getValue()).append("] = [")
                        .append(value.getKey()).append(",").append(value.getValue()).append("]\n");
            }
        }

        return stringBuilder.toString();
    }
}