package model;

import java.util.ArrayList;
import java.util.Random;

public class Application {
    public Environnement getEnv() {
        return env;
    }

    private Environnement env;

    public Application(int nbLignes, int nbColonnes, int nbAgents){
        initialiserPartie(nbLignes, nbColonnes, nbAgents);
    }

    public void initialiserPartie(int nbLignes, int nbColonnes, int nbAgents) {
        ArrayList<Agent> agents = new ArrayList<>();
        ArrayList<Integer> agentNames = new ArrayList<>();
        Random rand = new Random();
        int random_number;
        for (int i = 0; i < nbAgents; i++) {
            while (true) {
                random_number = 1 + rand.nextInt(nbAgents);
                if (!agentNames.contains(random_number)) {
                    break;
                }
            }
            agents.add(new Agent(random_number));
        }
        env = new Environnement(nbLignes, nbColonnes, agents);
    }
}
