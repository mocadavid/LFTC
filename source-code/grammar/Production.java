package ro.ubb.grammar;

import java.util.List;

/**
 * Defines a set of productions for a nonterminal.
 */
public class Production {
    //nonterminal
    private String start;
    //list of productions
    private List<List<String>> rules;

    Production(String start, List<List<String>> rules) {
        this.start = start;
        this.rules = rules;
    }

    String getStart() { return start; }
    List<List<String>> getRules() { return rules; }

    /**
     * Build all productions for a nonterminal as a string.
     * @return the result of the built: String
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(start + " -> ");

        for (List<String> rule : rules) {
            for (String element: rule)
                stringBuilder.append(element).append(" ");
            stringBuilder.append("| ");
        }

        stringBuilder.replace(stringBuilder.length() - 3, stringBuilder.length() - 1, "");
        return stringBuilder.toString();
    }
}
