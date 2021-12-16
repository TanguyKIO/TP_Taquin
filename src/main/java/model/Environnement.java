package model;

import java.util.ArrayList;
import java.util.Random;

public class Environnement {
    private ArrayList<Agent> agents;
    private Agent[][] map;
    private int[][] finalMap;
    private ArrayList<Thread> runningThreads;

    public Environnement(int nbLignes, int nbColonnes, ArrayList<Agent> agents) {
        Random rand = new Random();
        this.agents = agents;
        map = new Agent[nbLignes][nbColonnes];
        finalMap = new int[nbLignes][nbColonnes];
        int x, y;

        // Conception du schéma initiale
        for (Agent agent : agents) {
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
            agent.setFinalX((agentName - 1) / nbColonnes);
            agent.setFinalY((agentName - 1) % nbColonnes);
            agent.setSleep((agents.size() < 10?2000:agents.size() < 20?3000:4000));
            agent.setMaxInterations(50);
            finalMap[(agentName - 1) / nbColonnes][(agentName - 1) % nbColonnes] = agentName;
        }
    }


    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public void start() {
        runningThreads = new ArrayList<>();
        for (Agent agent : agents) {
            Thread thread = new Thread(agent);
            try {
                Thread.sleep((agents.size() < 10?2000:agents.size() < 20?3000:4000)/agents.size());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            thread.start();
            runningThreads.add(thread);
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
        try {
            switch (d) {
                case TOP -> {
                    map[a.getCurrentX() - 1][a.getCurrentY()] = a;
                    a.setCurrentX(a.getCurrentX() - 1);
                    break;
                }
                case BOTTOM -> {
                    map[a.getCurrentX() + 1][a.getCurrentY()] = a;
                    a.setCurrentX(a.getCurrentX() + 1);
                    break;
                }
                case LEFT -> {
                    map[a.getCurrentX()][a.getCurrentY() - 1] = a;
                    a.setCurrentY(a.getCurrentY() - 1);
                    break;
                }
                case RIGHT -> {
                    map[a.getCurrentX()][a.getCurrentY() + 1] = a;
                    a.setCurrentY(a.getCurrentY() + 1);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("passe");
        }
    }


    public Agent whichAgentBlocking(int x, int y, Direction d) {
        switch (d) {
            case TOP -> {
                if (x > 0) return map[x - 1][y];
                else return null;
            }
            case BOTTOM -> {
                if (x < map.length - 1) return map[x + 1][y];
                else return null;
            }
            case LEFT -> {
                if (y > 0) return map[x][y - 1];
                else return null;
            }
            case RIGHT -> {
                if (y < map[0].length - 1) return map[x][y + 1];
                else return null;
            }
            default -> {
                return null;
            }
        }
    }

    public Agent[][] getMap() {
        return map;
    }

    public int[][] getFinalMap() {
        return finalMap;
    }

    public void reinitialiser() {
        Random rand = new Random();
        ArrayList<Integer> agentNames = new ArrayList<>();
        int nbLignes = map.length;
        int nbColonnes = map[0].length;
        int x, y, random_number;
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                map[i][j] = null;
                finalMap[i][j] = 0;
            }
        }

        // Conception du schéma initiale
        for (Agent agent : agents) {
            agent.setInterupt(true);
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
            while (true) {
                random_number = 1 + rand.nextInt(nbLignes * nbColonnes - 1);
                if (!agentNames.contains(random_number)) {
                    agentNames.add(random_number);
                    break;
                }
            }
            agent.setName(random_number);
            agent.setFinalX((random_number - 1) / nbColonnes);
            agent.setFinalY((random_number - 1) % nbColonnes);
            finalMap[(random_number - 1) / nbColonnes][(random_number - 1) % nbColonnes] = random_number;
        }
    }

    public void stopAgents(){
        for (Agent agent : agents) {
            agent.setInterupt(true);
        }
        for (Thread t :runningThreads){
            t.stop();
        }
    }

    public void restart(){
        for (Agent agent : agents) {
            agent.setInterupt(false);
            try {
                Thread.sleep((agents.size() < 10?2000:agents.size() < 20?3000:4000)/agents.size());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean movePossible(int x, int y, Direction d) {
        switch (d) {
            case TOP -> {
                if (x > 0) return true;
                else return false;
            }
            case BOTTOM -> {
                if (x < map.length - 1) return true;
                else return false;
            }
            case LEFT -> {
                if (y > 0) return true;
                else return false;
            }
            case RIGHT -> {
                if (y < map[0].length - 1) return true;
                else return false;
            }
            default -> {
                return false;
            }
        }
    }
}
