package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Environnement {
    private ArrayList<Agent> agents;
    private Agent[][] map;
    private int[][] finalMap;
    private ArrayList<Thread> runningThreads;
    private double multiplicateurVitesseAffichage = 1;
    private boolean allInterrupted;
    private ReentrantLock lock;


    public static HashMap<Agent, LinkedList< Pair<int[], Direction>>> messages;
    public static int currentLine;
    public static int orientation;
    public static int strategie;

    public Environnement(int nbLignes, int nbColonnes, ArrayList<Agent> agents, int strategie, int vitesseAffichage) {
        Random rand = new Random();
        this.messages = new HashMap<>();
        this.currentLine = 0;
        this.strategie = strategie;
        this.agents = agents;
        this.map = new Agent[nbLignes][nbColonnes];
        this.finalMap = new int[nbLignes][nbColonnes];
        this.allInterrupted = false;
        this.lock = new ReentrantLock();
        switch (vitesseAffichage){
            case 0 -> this.multiplicateurVitesseAffichage = 2;
            case 1 -> this.multiplicateurVitesseAffichage = 1;
            case 2 -> this.multiplicateurVitesseAffichage = 0.33;
            case 3 -> this.multiplicateurVitesseAffichage = 0.16;
        }

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
                    agent.setCurrentX(x);
                    agent.setCurrentY(y);
                    break;
                }
            }
            int agentName = agent.getNom();
            agent.setFinalX((agentName - 1) / nbColonnes);
            agent.setFinalY((agentName - 1) % nbColonnes);
            agent.setSleep((int)(6000/agents.size()*multiplicateurVitesseAffichage));
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
                Thread.sleep((agents.size() < 10 ? 2000 : agents.size() < 20 ? 3000 : 4000) / agents.size());
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
        if ((map[x][y] == null && finalMap[x][y] != 0) || (map[x][y] != null && finalMap[x][y] != 0
                && map[x][y].getNom() != finalMap[x][y])) {
            return false;
        }
        if (precedent_line) {
            return (finalMap[x][y] == 0 && map[x][y] == null) || (map[x][y] != null && map[x][y].getNom() == finalMap[x][y]);
        }
        return true;
    }

    public synchronized boolean partResolved(int line, int orientation){
        this.lock.lock();
        boolean isResolved = true;
        switch (strategie){
            case 0 -> {
                isResolved =  lineResolved(line);
            }
            case 1 -> {
                isResolved = contourResolved(line);
            }
            case 2 -> {
                isResolved = lineResolved(line, orientation);
            }
        }
        this.lock.unlock();
        return isResolved;
    }

    public synchronized boolean contourResolved(int line){
        for (int i=0; i<map[0].length; i++) {
            if (!testCase(line, i, false)) {
                return false;
            }
        }
        for (int i=0; i<map[0].length; i++){
            if (!testCase(getNbLignes()-1-line, i, false)) {
                return false;
            }
        }
        for (int i=0; i<map.length; i++) {
            if (!testCase(i, line, false)) {
                return false;
            }
        }
        for (int i=0; i<map.length; i++){
            if (!testCase(i, getNbColonnes()-1-line, false)) {
                return false;
            }
        }

        if (angleBlocked(line, line)) return false;
        if (angleBlocked(getNbLignes()-1-line, line)) return false;
        if (angleBlocked(getNbLignes()-1-line, getNbColonnes()-1-line)) return false;
        if (angleBlocked(line, getNbColonnes()-1-line)) return false;

        for (int x = line; x > 0; x--) {
            for (int i = 0; i < map[0].length; i++) {
                if (!testCase(x-1, i, true)) {
                    return false;
                }
            }
            for (int i=0; i<map[0].length; i++){
                if (!testCase(getNbLignes() - x, i, true)) {
                    return false;
                }
            }
            for (int i=0; i<map.length; i++) {
                if (!testCase(i, x-1, true)) {
                    return false;
                }
            }
            for (int i=0; i<map.length; i++){
                if (!testCase(i, getNbColonnes()-x, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized boolean lineResolved(int line) {
        for (int i = 0; i < map[0].length; i++) {
            if (!testCase(line, i, false)) {
                return false;
            }
        }
        for (int x = line - 1; x >= 0; x--) {
            for (int i = 0; i < map[0].length; i++) {
                if (!testCase(x, i, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized boolean lineResolved(int line, int orientation) {
        switch (orientation){
            case 0 -> {
                for (int i =0; i<map[0].length; i++) {
                    if (!testCase(line, i, i == line - 1 || i == map[0].length - line)) {
                        return false;
                    }
                }
                for (int x = line - 1; x >= 0; x--) {
                    for (int i = 0; i < map[0].length; i++) {
                        if (!testCase(x, i, true)) {
                            return false;
                        }
                    }
                }
            }
            case 1 -> {
                for (int i=0; i<map.length; i++) {
                    if (!testCase(i, line, i == line - 1 || i != map.length - line)) {
                        return false;
                    }
                }
                for (int x = line - 1; x >= 0; x--) {
                    for (int i = 0; i < map.length; i++) {
                        if (!testCase(i, x, true)) {
                            return false;
                        }
                    }
                }
                if (angleBlocked(line, line)) return false;
            }
            case 2 -> {
                for (int i = 0; i < map[0].length; i++) {
                    if (!testCase(getNbLignes() - 1 - line, i, i == line - 1 || i == map[0].length - line)) {
                        return false;
                    }
                }
                for (int x = line; x > 0; x--) {
                    for (int i = 0; i < map[0].length; i++) {
                        if (!testCase(getNbLignes()-x, i, true)) {
                            return false;
                        }
                    }
                }
                if (angleBlocked(getNbLignes()-1-line, line)) return false;
            }
            case 3 -> {
                for (int i=0; i<map.length; i++){
                    if (!testCase(i, getNbColonnes()-1-line, i == line - 1 || i == map.length - line)) {
                        return false;
                    }
                }
                for (int x = line; x > 0; x--) {
                    for (int i = 0; i < map.length; i++) {
                        if (!testCase(i, getNbColonnes()-x, true)) {
                            return false;
                        }
                    }
                }
                if (angleBlocked(getNbLignes()-1-line, getNbColonnes()-1-line)) return false;
                if (angleBlocked(line, getNbColonnes()-1-line)) return false;
            }
        }
        return true;
    }

    public synchronized boolean angleBlocked(int x, int y){
        return map[x][y] != null
                && finalMap [x][y] != map[x][y].getNom();
    }

    public synchronized void move(Agent agent, Direction d) {
        int x; int y;
        while (lock.isLocked()){
        }
        switch (d) {
            case TOP -> {
                x = agent.getCurrentX() - 1;
                y = agent.getCurrentY();
            }
            case BOTTOM -> {
                x = agent.getCurrentX() + 1;
                y = agent.getCurrentY();
            }
            case LEFT -> {
                x = agent.getCurrentX();
                y = agent.getCurrentY() - 1;
            }
            case RIGHT -> {
                x = agent.getCurrentX();
                y = agent.getCurrentY() + 1;
            }
            default -> {
                x = agent.getCurrentX();
                y = agent.getCurrentY();
            }
        }
        if (map[x][y] == null) {
            map[agent.getCurrentX()][agent.getCurrentY()] = null;
            map[x][y] = agent;
            agent.setCurrentX(x);
            agent.setCurrentY(y);
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
                    agent.setCurrentX(x);
                    agent.setCurrentY(y);
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
            agent.setLine(0);
            agent.setOrientation(0);
            agent.setNbIterations(0);
            agent.setNbIterNoMove(0);
            agent.emptyMemory();
            finalMap[(random_number - 1) / nbColonnes][(random_number - 1) % nbColonnes] = random_number;
            currentLine = 0;
            orientation = 0;
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

    public void checkAllInterupted() {
        boolean interrupt = true;
        for (Agent agent:agents){
            if (!agent.isInterupt()){
                interrupt = false;
                break;
            }
        }
        this.allInterrupted = interrupt;
    }

    public boolean isAllInterrupted() {
        return allInterrupted;
    }

    public int getNbIterationsMax(){
        int nbIter = 0;
        for (Agent agent: getAgents()){
            if (agent.getNbIterations() > nbIter){
                nbIter = agent.getNbIterations();
            }
        }
        return nbIter;
    }
}
