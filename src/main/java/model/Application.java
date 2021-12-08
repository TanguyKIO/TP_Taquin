package model;

import java.util.ArrayList;

public class Application {
    public Environnement getEnv() {
        return env;
    }

    private Environnement env;

    public Application(int nbLignes, int nbColonnes, int nbAgents){
        initialiserPartie(nbLignes, nbColonnes, nbAgents);

    }
    public void initialiserPartie(int nbLignes, int nbColonnes, int nbAgents){
        ArrayList<Agent> agents = new ArrayList<>();
        agents.add(new Agent("10"));
        agents.add(new Agent("2"));
        agents.add(new Agent("15"));
        agents.add(new Agent("7"));
        agents.add(new Agent("5"));
        env = new Environnement(5, agents);
    }


}
