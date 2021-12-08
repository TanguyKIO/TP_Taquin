package model;

import java.util.ArrayList;
import java.util.Random;

public class Environnement {
    private ArrayList<Agent> agents;
    private Agent[][] map;
    private int[][] finalMap;

    public Environnement(int nbLignes, int nbColonnes, ArrayList<Agent> agents) {
        Random rand = new Random();
        this.agents = agents;
        map = new Agent[nbLignes][nbColonnes];
        finalMap = new int[nbLignes][nbColonnes];
        int x,y;

        // Conception du schéma initiale
        for(Agent agent : agents) {
            agent.setE(this);
            agent.setInterupt(false);
            while (true) {
                x = rand.nextInt(nbLignes);
                y = rand.nextInt(nbColonnes);
                if (map[x][y] == null) {
                    map[x][y] = agent;
                    map[x][y].setCurrentX(x);
                    map[x][y].setCurrentY(y);
                    break;
                }
            }
            int agentName = agent.getNom();
            finalMap[agentName / nbColonnes][agentName % nbColonnes] = agentName;
        }
    }


    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public void start() {
        for (Agent agent : agents) {
            Thread thread = new Thread(agent);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            thread.start();
        }
    }

    public void verify() {
        if (isResolved()) {
            for (Agent a : agents) {
                a.setInterupt(true);
            }
        }
    }

    public boolean isResolved() {
        for (Agent agent : agents) {
            if (agent.getCurrentX() != agent.getFinalX() || agent.getCurrentY() != agent.getFinalY()) {
                return false;
            }
        }
        return true;
    }

    public void move(Agent a, Direction d) {
        map[a.getCurrentX()][a.getCurrentY()] = null;
        switch (d) {
            case TOP -> {
                map[a.getCurrentX() - 1][a.getCurrentY()] = a;
                a.setCurrentX(a.getCurrentX() - 1);
            }
            case BOTTOM -> {
                map[a.getCurrentX() + 1][a.getCurrentY()] = a;
                a.setCurrentX(a.getCurrentX() + 1);
            }
            case LEFT -> {
                map[a.getCurrentX()][a.getCurrentY() - 1] = a;
                a.setCurrentY(a.getCurrentY() - 1);
            }
            case RIGHT -> {
                map[a.getCurrentX()][a.getCurrentY() + 1] = a;
                a.setCurrentY(a.getCurrentY() + 1);
            }
        }
    }


    public boolean isOccupied(int x, int y, Direction d) {
        switch (d) {
            case TOP -> {
                return (x-1 >= 0 && map[x- 1][y] != null);
            }
            case BOTTOM -> {
                return (x+1 < map.length && map[x+ 1][y]!= null);
            }
            case LEFT -> {
                return (y-1 >=0 && map[x][y - 1] != null);
            }
            case RIGHT -> {
                return (y+1 < map[0].length && map[x][y + 1] != null);
            }
        }
        return false;
    }

    public Agent[][] getMap() {
        return map;
    }

    public int[][] getFinalMap() {
        return finalMap;
    }

}
