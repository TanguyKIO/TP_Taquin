package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Environnement {
    private ArrayList<Agent> agents;
    private Agent[][] map;
    private int[][] finalMap;
    private ArrayList<Thread> runningThreads;

    public static HashMap<Agent, LinkedList< Pair<int[], Direction>>> messages;
    public static int currentLine;
    public static int strategie;

    public Environnement(int nbLignes, int nbColonnes, ArrayList<Agent> agents, int strategie) {
        Random rand = new Random();
        messages = new HashMap<>();
        currentLine = 0;
        this.strategie = strategie;
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
            agent.setSleep(6000/agents.size());
            agent.setMaxInterations(10000);
            finalMap[(agentName - 1) / nbColonnes][(agentName - 1) % nbColonnes] = agentName;
            messages.put(agent, new LinkedList<>());
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

    public synchronized boolean verify() {
        if (isResolved()) {
            for (Agent a : agents) {
                a.setInterupt(true);
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isResolved() {
        for (Agent agent : agents) {
            if (agent.getCurrentX() != agent.getFinalX() || agent.getCurrentY() != agent.getFinalY()) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean testCase(int x, int y, boolean precedent_line){
        if (map[x][y] == null) {
            if (finalMap[x][y] != 0) {
                return false;
            }
        }
        if (!precedent_line) {
            if (finalMap[x][y] != 0 && map[x][y] != null && map[x][y].getNom() != finalMap[x][y]){
                return false;
            }
            else return map[x][y] == null || map[x][y].getNom() == finalMap[x][y];
        }
        return true;
    }

    public synchronized boolean partResolved(int line){
        switch (strategie){
            case 0 -> {
                return lineResolved(line);
            }
            case 1 -> {
                return contourResolved(line);
            }
            case 2 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public synchronized boolean contourResolved(int line){
        for (int i=0; i<map[0].length; i++) {
            if (!testCase(line, i, false)) {
                return false;
            }
        }
        for (int i=0; i<map[0].length; i++){
            if (!testCase(getNbColonnes()-1-line, i, false)) {
                return false;
            }
        }
        for (int i=0; i<map.length; i++) {
            if (!testCase(i, line, false)) {
                return false;
            }
        }
        for (int i=0; i<map.length; i++){
            if (!testCase(i, getNbLignes()-1-line, false)) {
                return false;
            }
        }
        if (line > 0){
            for (int i=0; i<map[0].length; i++) {
                if (!testCase(line-1, i, true)) {
                    return false;
                }
            }
            for (int i=0; i<map[0].length; i++){
                if (!testCase(getNbColonnes() - line, i, true)) {
                    return false;
                }
            }
            for (int i=0; i<map.length; i++) {
                if (!testCase(i, line-1, true)) {
                    return false;
                }
            }
            for (int i=0; i<map.length; i++){
                if (!testCase(getNbLignes()-line, i, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized boolean lineResolved(int line) {
        for (int i =0; i<map[0].length; i++) {
            if (!testCase(line, i, false)) {
                return false;
            }
        }
        if (line > 0){
            for (int i =0; i<map[0].length; i++) {
                if (!testCase(line-1, i, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized void move(Agent a, Direction d) {
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


    public synchronized Agent whichAgentBlocking(int x, int y, Direction d) {
        switch (d) {
            case TOP -> {
                return (x > 0)? map[x - 1][y]:null;
            }
            case BOTTOM -> {
                return (x < map.length - 1)? map[x + 1][y]:null;
            }
            case LEFT -> {
                return (y > 0) ?map[x][y - 1]:null;
            }
            case RIGHT -> {
                return (y < map[0].length - 1) ? map[x][y + 1]:null;
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
        messages.clear();
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
            currentLine = 0;
            messages.put(agent, new LinkedList<>());
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
                return (x > 0);
            }
            case BOTTOM -> {
                return (x < map.length - 1);
            }
            case LEFT -> {
                return  (y > 0);
            }
            case RIGHT -> {
                return (y < map[0].length - 1);
            }
            default -> {
                return false;
            }
        }
    }

    public int getNbLignes(){
        return map.length;
    }

    public int getNbColonnes(){
        return map[0].length;
    }

    public int getNbAgents(){
        return agents.size();
    }

    public synchronized void updateMap(Agent agent, int x, int y){
        if (map[x][y] != agent){
            if (map[x][y] == null) {
                map[x][y] = agent;
            }
            else{
                Random rand = new Random();
                while (true) {
                    x = rand.nextInt(map.length);
                    y = rand.nextInt(map[0].length);
                    if (x >= currentLine && map[x][y] == null) {
                        map[x][y] = agent;
                        map[x][y].setCurrentX(x);
                        map[x][y].setCurrentY(y);
                        break;
                    }
                }
            }
        }
    }
}
