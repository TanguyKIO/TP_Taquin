package model;

import java.util.ArrayList;
import java.util.Random;

public class Environnement {
    private ArrayList<Agent> agents;
    private Agent [][] map;
    private int [][] finalMap;

    public Environnement(int nbLignes, int nbColonnes, ArrayList<Agent> agents) {
        Random rand = new Random();
        this.agents = agents;
        map = new Agent[nbLignes][nbColonnes];
        finalMap = new int[nbLignes][nbColonnes];
        int x,y;

        // Conception du schéma initiale
        for(Agent agent : agents) {
            while (true) {
                x = rand.nextInt(nbLignes);
                y = rand.nextInt(nbColonnes);
                if (map[x][y] != null) {
                    map[x][y] = agent;
                    break;
                }
                int agentName = agent.getNom();
                finalMap[agentName / nbColonnes][agentName % nbColonnes] = agentName;
            }
        }
    }

    public void start(){
        for(Agent agent: agents){
            agent.start();
        }
    }

    public void verify(){
        if (isResolved()) {
            for(Agent a : agents) {
                a.interrupt();
            }
        }
    }

    public boolean isResolved() {
        for (Agent agent : agents) {
            if (agent.getCurrentX() != agent.getFinalX()) {
                return false;
            }
        }
        return true;
    }

    public void move(Agent a, Direction d) {
        map[a.getCurrentX()][a.getCurrentY()] = null;
        switch (d){
            case TOP -> map[a.getCurrentX()-1][a.getCurrentY()] = a;
            case BOTTOM -> map[a.getCurrentX()+1][a.getCurrentY()] = a;
            case LEFT -> map[a.getCurrentX()][a.getCurrentY()-1] = a;
            case RIGHT -> map[a.getCurrentX()][a.getCurrentY()+1] = a;
        }
    }


    public boolean isOccupied(Direction d) {

        return false;
    }

    public Agent[][] getMap() {
        return map;
    }

    public int[][] getFinalMap() {
        return finalMap;
    }

}
