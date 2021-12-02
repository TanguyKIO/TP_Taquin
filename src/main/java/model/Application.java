package model;

import java.util.ArrayList;

public class Application {
    public Environnement getEnv() {
        return env;
    }

    private Environnement env;

    public Application(){
        ArrayList<Agent> agents = new ArrayList<Agent>();
        agents.add(new Agent("10"));
        agents.add(new Agent("2"));
        agents.add(new Agent("3"));
        agents.add(new Agent("8"));
        agents.add(new Agent("5"));
        env = new Environnement(5, agents);

    }
    public void initialiserPartie(){
        ArrayList<Agent> agents = new ArrayList<Agent>();
        agents.add(new Agent("1"));
        agents.add(new Agent("2"));
        agents.add(new Agent("3"));
        agents.add(new Agent("4"));
        agents.add(new Agent("5"));
        env = new Environnement(5, agents);
    }


}
