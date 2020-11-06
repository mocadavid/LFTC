package ro.ubb.FA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FA {

    private boolean valid = false;
    private List<String> states = new LinkedList<>();
    private List<String> alphabet = new LinkedList<>();
    private String initialState;
    private List<String> finalStates = new LinkedList<>();
    private Map<Pair,String> transitions = new HashMap<>();

    private void loadFile(String filename){
        try{
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new FileReader(filename));

            boolean statesDone = false;
            boolean alphabetDone = false;
            boolean finalStatesDone = false;
            boolean initialStateDone = false;
            int indexState = 0;

            String line = bufferedReader.readLine();

            while (line!=null){

                if (!statesDone){
                    states = Arrays.asList(line.split(","));
                    statesDone = true;
                }else if (!alphabetDone) {
                    alphabet = Arrays.asList(line.split(","));
                    alphabetDone = true;
                }else if (!initialStateDone){
                    initialState = line.strip();
                    initialStateDone = true;
                }else if (!finalStatesDone){
                    finalStates = Arrays.asList(line.split(","));
                    finalStatesDone = true;
                }else{
                    List<String> destinations = Arrays.asList(line.split(","));

                    int indexAlphabet = 0;

                    for (String dest: destinations){
                        if (!dest.equals("-")){
                            if (dest.contains(":")){
                                List<String> groupOfDestinations = Arrays.asList(dest.split(":"));
                                for (String dest2: groupOfDestinations){
                                    transitions.put(new Pair(states.get(indexState),dest2),alphabet.get(indexAlphabet));
                                }
                            }else{
                                transitions.put(new Pair(states.get(indexState),dest),alphabet.get(indexAlphabet));
                            }
                        }
                        indexAlphabet+=1;
                    }
                    indexState+=1;
                }

                line = bufferedReader.readLine();

            }
        } catch (IOException e) { e.printStackTrace(); }
        System.out.println("Reading done!");
    }

    public void start(String filename){
        loadFile(filename);
        menu();
    }

    private void menu(){
        boolean exit = false;
        Scanner scanner = new Scanner(System.in);
        while (!exit){
            displayMenu();
            int answer = Integer.parseInt(scanner.nextLine());
            switch (answer){
                case 0:
                    exit = true;
                    break;
                case 1:
                    System.out.println(states);
                    break;
                case 2:
                    System.out.println(alphabet);
                    break;
                case 3:
                    System.out.println(initialState);
                    break;
                case 4:
                    System.out.println(finalStates);
                    break;
                case 5:
                    transitions.keySet().forEach(x-> System.out.println(x+" : "+transitions.get(x)));
                    break;
                case 6:
                    break;
            }
        }
    }

    private void displayMenu(){
        System.out.println("Press 0 to exit.");
        System.out.println("Press 1 for the set of states.");
        System.out.println("Press 2 for the alphabet.");
        System.out.println("Press 3 for the initial state.");
        System.out.println("Press 4 for final states.");
        System.out.println("Press 5 for all transitions.");
        System.out.println("Press 6 to check if it is accepted.");
    }

}
