package ro.ubb.grammar;

import java.util.*;

/**
 * Class which can generate the first and follow sets.
 */
public class Parser {
    private Map<String, Set<String>> firstSet;
    private Map<String, Set<String>> followSet;
    private Grammar grammar;
    private static Stack<List<String>> rules = new Stack<>();

    /**
     *Constructor
     */
    public Parser() {
        this.grammar = new Grammar();
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        generateSets();
    }

    /**
     * Initializing the first and follow sets
     */
    private void generateSets() {
        generateFirstSet();
        generateFollowSet();
        System.out.println("First sets:");
        System.out.println(firstSet);
        System.out.println("Follow sets:");
        System.out.println(followSet);
    }

    /**
     * Generating first for every nonTerminal.
     */
    private void generateFirstSet() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            firstSet.put(nonTerminal, this.firstOf(nonTerminal));
        }
    }

    /**
     *Generates the first set for the given nonTerminal.
     * @param nonTerminal: nonTerminal: String
     * @return The set of terminals for the given nonTerminal.
     */
    private Set<String> firstOf(String nonTerminal) {
        if (firstSet.containsKey(nonTerminal)){ return firstSet.get(nonTerminal); }

        Set<String> terminalsAccess = new HashSet<>();
        Set<String> terminals = grammar.getTerminals();

        for (Production production : grammar.getProductionsForNonTerminal(nonTerminal)){
            for (List<String> rule : production.getRules()) {
                String firstSymbol = rule.get(0);
                if (firstSymbol.equals("ε")){ terminalsAccess.add("ε"); }
                else if (terminals.contains(firstSymbol)){ terminalsAccess.add(firstSymbol); }
                else{ terminalsAccess.addAll(firstOf(firstSymbol)); }
            }
        }
        return terminalsAccess;
    }

    /**
     *Generating the follow set for all the nonTerminals.
     */
    private void generateFollowSet() {
        for (String nonTerminal : grammar.getNonTerminals()) {
            followSet.put(nonTerminal, this.followOf(nonTerminal, nonTerminal));
        }
    }

    /*
     * Analyses the productions in which the given nonTerminal is present and calls accordingly the follow operations with the needed values.
     * @param nonTerminal the given nonTerminal which we examine: String
     * @param initialNonTerminal the starting point for which we search for the follow set: String
     * @return the finalResult of the follow set: Set<String>
     */
    private Set<String> followOf(String nonTerminal, String initialNonTerminal) {
        if (followSet.containsKey(nonTerminal)) { return followSet.get(nonTerminal); }

        Set<String> finalResult = new HashSet<>();
        Set<String> terminals = grammar.getTerminals();

        if (nonTerminal.equals(grammar.getStartingSymbol())){ finalResult.add("$"); }

        for (Production production : grammar.getProductionsContainingNonterminal(nonTerminal)) {

            String productionStart = production.getStart();

            for (List<String> rule : production.getRules()){

                List<String> ruleConflict = new ArrayList<>();
                ruleConflict.add(nonTerminal);
                ruleConflict.addAll(rule);

                if (rule.contains(nonTerminal) && !rules.contains(ruleConflict)) {
                    rules.push(ruleConflict);
                    int indexNonTerminal = rule.indexOf(nonTerminal);
                    finalResult.addAll(followOperation(nonTerminal, finalResult, terminals, productionStart, rule, indexNonTerminal, initialNonTerminal));

                    List<String> sublist = rule.subList(indexNonTerminal + 1, rule.size());

                    if (sublist.contains(nonTerminal)){
                        finalResult.addAll(followOperation(nonTerminal, finalResult, terminals, productionStart, rule, indexNonTerminal + 1 + sublist.indexOf(nonTerminal), initialNonTerminal));
                    }
                    rules.pop();
                }
            }
        }

        return finalResult;
    }

    /**
     * Decides upon the case of the follow in which we are.
     * @param nonTerminal the nonTerminal for which we search follow: String
     * @param intermediaryResult the list in we save the found elements so far: Set<String>
     * @param terminals the terminals from the grammar: Set<String>
     * @param productionStart the starting nonTerminal of the production
     * @param rule the current production we analyse: String
     * @param indexNonTerminal the index of the nonTerminal: int
     * @param initialNonTerminal the given nonTerminal: String
     * @return the result of the follow operation for the given nonTerminal staring from the initialNonTerminal: Set<String>
     */
    private Set<String> followOperation(String nonTerminal, Set<String> intermediaryResult, Set<String> terminals, String productionStart, List<String> rule, int indexNonTerminal, String initialNonTerminal) {
        if (indexNonTerminal == rule.size() - 1) {
            if (productionStart.equals(nonTerminal)) { return intermediaryResult;  }
            if (!initialNonTerminal.equals(productionStart)){ intermediaryResult.addAll(followOf(productionStart, initialNonTerminal)); }
        }
        else{
            String nextSymbol = rule.get(indexNonTerminal + 1);
            if (terminals.contains(nextSymbol)){ intermediaryResult.add(nextSymbol); }
            else{
                if (!initialNonTerminal.equals(nextSymbol)) {
                    Set<String> fists = new HashSet<>(firstSet.get(nextSymbol));

                    if (fists.contains("ε")) {
                        intermediaryResult.addAll(followOf(nextSymbol, initialNonTerminal));
                        fists.remove("ε");
                    }
                    intermediaryResult.addAll(fists);
                }
            }
        }
        return intermediaryResult;
    }
}