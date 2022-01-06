package model;

import javafx.util.Pair;

import java.util.*;

import static model.Environnement.*;


enum Direction {
    TOP,
    BOTTOM,
    RIGHT,
    LEFT
}

public class Agent extends Observable implements Runnable {

    private int sleepTime;

    public void setE(Environnement env) {
        this.env = env;
    }

    private Environnement env;
    private int finalX, finalY;
    private int currentX, currentY;
    private int name;
    private boolean interupt;
    private int maxInterations;
    private int nbIterations;
    private int nbIterNoMove;
    private int line;
    private int orientation;
    private final LinkedList<Direction> memoire;

    public Agent(int name) {
        this.name = name;
        this.memoire = new LinkedList<>();
        nbIterations = 0;
        nbIterNoMove = 0;
        this.line = 0;
    }

    @Override
    public synchronized void run() {
        while (true) {
            synchronized (this) {
                if (!interupt) {
                    nbIterations += 1;
                    line = currentLine;
                    orientation = Environnement.orientation;

                    if (messages.get(this).size() > 5 || nbIterNoMove >= 5) {
                        if (!moveAvailableDirection()) {
                            pushAvailableDirection();
                        } else {
                            nbIterNoMove = 0;
                            messages.get(this).clear();
                        }
                    } else {
                        Pair<int[], Direction> message = getMessage();
                        if (message == null && conditionsToMove()) {
                            moveBestDirection();
                        } else {
                            if (message != null && conditionsToRelease()) {
                                moveToRelease(message);
                            }
                            if (!bienPositionne()) {
                                nbIterNoMove += 1;
                            }
                        }
                        switch (strategie) {
                            case 0 -> {
                                if (line < env.getNbLignes() - 2 && env.partResolved(line, orientation)) {
                                    line++;
                                }
                            }
                            case 1 -> {
                                //if ((2*(line+1) <= env.getNbLignes() - 3 && 2*(line+1) <= env.getNbColonnes()-3) && env.partResolved(line, orientation)) {
                                if (line < env.getNbLignes() -2 && line < env.getNbColonnes() -2 && env.partResolved(line, orientation)) {
                                    line++;
                                }
                            }
                            case 2 -> {
                                /*if (((2*(line+1) <= env.getNbLignes() - 3 && 2*(line+1) <= env.getNbColonnes()-3)
                                        || (2*line+orientation+1 <= env.getNbLignes() - 3 && 2*line+orientation+1 <= env.getNbColonnes()-3))
                                        && env.partResolved(line, orientation)) {*/
                                if (line < env.getNbLignes() -2 && line < env.getNbColonnes() -2 && env.partResolved(line, orientation)){
                                    if (orientation == 3) {
                                        line++;
                                        orientation = 0;
                                    } else {
                                        orientation++;
                                    }
                                }
                            }
                        }
                    }
                    currentLine = line;
                    Environnement.orientation = orientation;
                    if (nbIterations == maxInterations) {
                        this.setInterupt(true);
                        this.env.checkAllInterupted();
                    }
                    setChanged();
                    notifyObservers();
                    if (env.verify()) {
                        setChanged();
                        notifyObservers();
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private Pair<int[], Direction> getMessage() {
        return messages.get(this).pollFirst();
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentX(int x) {
        currentX = x;
    }

    public void setCurrentY(int y) {
        currentY = y;
    }

    public boolean conditionsToMove(){
        if (bienPositionne()) return false;
        switch (strategie) {
            case 0 -> {
                return this.getFinalX() <= currentLine || this.getCurrentX() <= currentLine || currentLine == env.getNbLignes()-2;
            }
            case 1 -> {
                return this.getFinalX() <= currentLine || this.getCurrentX() <= currentLine
                        || this.getFinalY() <= currentLine || this.getCurrentY() <= currentLine
                        || this.getFinalX() >= env.getNbLignes() - 1 - currentLine || this.getCurrentX() >= env.getNbLignes() - 1 - currentLine
                        || this.getFinalY() >= env.getNbColonnes() - 1 - currentLine || this.getCurrentY() >= env.getNbColonnes() - 1 - currentLine
                        || currentLine == env.getNbLignes()-2 || currentLine == env.getNbColonnes()-2;
            }
            case 2 -> {
                if (currentLine == env.getNbLignes()-2) return true;
                else{
                    switch (Environnement.orientation){
                        case 0 -> {
                            return this.getFinalX() <= currentLine || this.getCurrentX() <= currentLine;
                        }
                        case 1 -> {
                            return (this.getFinalX() <= currentLine || this.getCurrentX() <= currentLine)
                                    || (this.getFinalY() <= currentLine || this.getCurrentY() <= currentLine);
                        }
                        case 2 -> {
                            return (this.getFinalX() <= currentLine || this.getCurrentX() <= currentLine)
                                    || (this.getFinalY() <= currentLine || this.getCurrentY() <= currentLine)
                                    || (this.getFinalX() >= env.getNbColonnes()-1-line || this.getCurrentX() >= env.getNbColonnes()-1-line);
                        }
                        case 3 -> {
                            return (this.getFinalX() <= currentLine || this.getCurrentX() <= currentLine)
                                    || (this.getFinalY() <= currentLine || this.getCurrentY() <= currentLine)
                                    || (this.getFinalX() >= env.getNbColonnes()-1-line || this.getCurrentX() >= env.getNbColonnes()-1-line)
                                    || (this.getFinalY() >= env.getNbLignes()-1-line || this.getCurrentY() >= env.getNbLignes()-1-line);
                        }
                        default -> {
                            return false;
                        }
                    }
                }
            }
            case 3 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean conditionsToRelease(){
        int nbLignes = env.getNbLignes();
        int nbColonnes = env.getNbColonnes();
        switch (strategie){
            case 0 -> {
                return this.getCurrentX() >= line;
            }
            case 1 -> {
                return (this.getCurrentX() >= line && this.getCurrentX() <= nbLignes-1-line
                        && this.getCurrentY() >= line && this.getCurrentY() <= nbColonnes-1-line);
            }
            case 2 -> {
                switch (Environnement.orientation){
                    case 0 -> {
                        return this.getCurrentX() >= line && this.getCurrentX() <= nbLignes - 1 - line
                                && this.getCurrentY() >= line && this.getCurrentY() <= nbColonnes - 1 - line;
                    }
                    case 1 -> {
                        return this.getCurrentX() > line && this.getCurrentY() >= line
                                && this.getCurrentX() <= nbLignes - 1 - line && this.getCurrentY() <= nbColonnes - 1 - line;
                    }
                    case 2 -> {
                        return this.getCurrentX() > line && this.getCurrentY() > line && this.getCurrentX() <= nbLignes-1-line
                                && this.getCurrentY() <= nbColonnes - 1 - line;
                    }
                    case 3 -> {
                        return this.getCurrentX() > line && this.getCurrentY() > line && this.getCurrentX() < nbLignes - 1 -line
                                && this.getCurrentY() <= nbColonnes - 1 -line;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case 3 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean conditionsRandomMove(){
        switch (strategie){
            case 0 -> {
                return this.getFinalX() >= line;
            }
            case 1 -> {
                return (this.getFinalX() >= line && this.getFinalX() <= env.getNbLignes()-1-line
                        && this.getFinalY() >= line && this.getFinalY() <= env.getNbColonnes()-1-line);
            }
            case 2 -> {
                switch (Environnement.orientation){
                    case 0 -> {
                        return this.getFinalX() >= line;
                    }
                    case 1 -> {
                        return this.getFinalY() >= line;
                    }
                    case 2 -> {
                        return this.getFinalX() <= env.getNbLignes()-1-line;
                    }
                    case 3 -> {
                        return this.getFinalY() <= env.getNbColonnes()-1-line;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case 3 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public synchronized LinkedList<Direction> bestPath() {
        LinkedList<Direction> directions = new LinkedList<>();
        int diffX = finalX - currentX;
        int diffY = finalY - currentY;
        if (diffX == 0 && diffY == 0){
            directions.add(Direction.LEFT);
            directions.add(Direction.TOP);
            directions.add(Direction.BOTTOM);
            directions.add(Direction.RIGHT);
            Collections.shuffle(directions);
        }
        else {
            if (Math.abs(diffX) >= Math.abs(diffY)) {
                if (diffX < 0) directions.add(Direction.TOP);
                else if (diffX > 0) directions.add(Direction.BOTTOM);

                if (diffY < 0) directions.add(Direction.LEFT);
                else if (diffY > 0) directions.add(Direction.RIGHT);
                else {
                    directions.add(Direction.LEFT);
                    directions.add(Direction.RIGHT);
                }
            } else {
                if (diffY < 0) directions.add(Direction.LEFT);
                else if (diffY > 0) directions.add(Direction.RIGHT);

                if (diffX < 0) directions.add(Direction.TOP);
                else if (diffX > 0) directions.add(Direction.BOTTOM);
                else {
                    directions.add(Direction.TOP);
                    directions.add(Direction.BOTTOM);
                }
            }
        }
        directions = removeDirections(directions);
        return directions;
    }

    private synchronized LinkedList<Direction> removeDirections(LinkedList<Direction> directions) {
        switch (strategie){
            case 0-> {
                while(currentX < line && directions.remove(Direction.TOP)){
                }
            }
            case 1, 2-> {
                while(currentX < line && directions.remove(Direction.TOP)){
                }
                while(currentX > env.getNbLignes() - 1 - line && directions.remove(Direction.BOTTOM)){
                }
                while(currentY < line && directions.remove(Direction.LEFT)){
                }
                while(currentY > env.getNbColonnes() - 1 - line && directions.remove(Direction.RIGHT)){
                }
            }
        }
        return directions;
    }

    public synchronized LinkedList<Direction> bestPath(Direction d) {
        LinkedList<Direction> directions = bestPath();
        switch (d){
            case LEFT -> {
                directions.remove(Direction.RIGHT);
                directions.add(Direction.LEFT);
                directions.add(Direction.TOP);
                directions.add(Direction.BOTTOM);
                directions.add(Direction.RIGHT);
                break;
            }
            case RIGHT -> {
                directions.remove(Direction.LEFT);
                directions.add(Direction.RIGHT);
                directions.add(Direction.TOP);
                directions.add(Direction.BOTTOM);
                directions.add(Direction.LEFT);
                break;
            }
            case TOP -> {
                directions.remove(Direction.BOTTOM);
                directions.add(Direction.TOP);
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
                directions.add(Direction.BOTTOM);
                break;
            }
            case BOTTOM -> {
                directions.remove(Direction.TOP);
                directions.add(Direction.BOTTOM);
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
                directions.add(Direction.TOP);
                break;
            }
        }
        directions = removeDirections(directions);
        Direction direction = findRepetition();
        while (directions.remove(direction)){
            continue;
        }
        return directions;
    }

    public synchronized void moveBestDirection(){
        LinkedList<Direction> directions = bestPath();
        Direction direction = findRepetition();
        while (directions.size() > 1 && directions.remove(direction)){
            continue;
        }
        /*Direction direction = findRepetition();
        if (direction != null){
            directions =  findFirstPossible(directions);
        }*/
        move(directions, false);
    }

    private boolean bienPositionne() {
        return finalX == currentX && finalY == currentY;
    }

    public synchronized boolean moveAvailableDirection(){
        if (conditionsRandomMove()){
            List<Direction> directions = Arrays.asList(Direction.values());
            Collections.shuffle(directions);
            for (Direction d : directions) {
                if (env.movePossible(this.getCurrentX(), this.getCurrentY(), d) && env.whichAgentBlocking(currentX, currentY, d) == null) {
                    env.move(this, d);
                    updateMemoire(d);
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized void pushAvailableDirection(){
        if (conditionsRandomMove()){
            List<Direction> directions = Arrays.asList(Direction.values());
            Collections.shuffle(directions);
            Agent blockingAgent;
            for (Direction d : directions) {
                blockingAgent = env.whichAgentBlocking(currentX, currentY, d);
                if (env.movePossible(this.getCurrentX(), this.getCurrentY(), d) && blockingAgent != null
                        && blockingAgent.conditionsToRelease()) {
                    addMessage(blockingAgent, d, new int[]{finalX, finalY});
                    memoire.clear();
                    break;
                }
            }
        }
    }

    public synchronized void move(LinkedList<Direction> directions, boolean mustRelease) {
        boolean moved = false;
        boolean pushed = false;
        Agent blockingAgent;
        LinkedList<Pair<Agent, Direction>> wellPositionned = null;
        if (nbIterNoMove == 3){
            Collections.shuffle(directions);
        }
        for (Direction d : directions) {
            blockingAgent = env.whichAgentBlocking(currentX, currentY, d);
            if (blockingAgent == null && env.movePossible(currentX, currentY, d)) {
                env.move(this, d);
                updateMemoire(d);
                moved = true;
                break;
            }
        }
        if (!moved) {
            nbIterNoMove += 1;
            wellPositionned = new LinkedList<>();
            for (int i=0; i<directions.size(); i++) {
                Direction d = directions.get(i);
                blockingAgent = env.whichAgentBlocking(currentX, currentY, d);
                if (env.movePossible(currentX, currentY, d) && (!mustRelease || i!= directions.size()-1)
                && blockingAgent != null && blockingAgent.conditionsToRelease()) {
                    //System.out.println(this.name + " pushed " + env.whichAgentBlocking(currentX, currentY, d).getNom());
                    if (blockingAgent.bienPositionne()){
                        wellPositionned.add(new Pair<>(blockingAgent, d));
                    }
                    else {
                        addMessage(blockingAgent, d, new int[]{finalX, finalY});
                        pushed = true;
                        break;
                    }
                }
            }
        }
        if (!moved && !pushed){
            for (Pair<Agent, Direction> pair:wellPositionned){
                addMessage(pair.getKey(), pair.getValue(), new int[]{finalX, finalY});
            }
        }
    }

    public synchronized LinkedList<Direction> findFirstPossible(LinkedList<Direction> directions){
        for (Direction d : directions) {
            if (env.movePossible(currentX, currentY, d)) {
                LinkedList<Direction> newDirection = new LinkedList<>();
                newDirection.add(d);
                return newDirection;
            }
        }
        return directions;
    }

    public synchronized void moveToRelease(Pair<int[], Direction> message) {
        move(bestPath(message.getValue()), true);
    }

    public synchronized void addMessage(Agent agent, Direction d, int[] message){
        if (agent != null)
        messages.get(agent).add(new Pair<>(message, d));
    }

    public int getFinalX() {
        return finalX;
    }

    public void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    public int getFinalY() {
        return finalY;
    }

    public void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    public int getNom() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public boolean isInterupt() {
        return interupt;
    }

    public void setInterupt(boolean interupt) {
        this.interupt = interupt;
    }

    public void setSleep(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setMaxInterations(int maxInterations) {
        this.maxInterations = maxInterations;
    }

    public void updateMemoire(Direction d) {
        if (memoire.size() == 6) {
            memoire.remove();
        }
        memoire.add(d);
    }

    public Direction findRepetition(){
        boolean repetition = true;
        if (memoire.size() == 6) {
            for (int j = memoire.size()-3; j>= memoire.size()-5; j -= 2) {
                if (Arrays.equals(new Direction[]{memoire.get(memoire.size() - 1), memoire.get(memoire.size() - 2)}, new Direction[]{memoire.get(j), memoire.get(j - 1)})) {
                    continue;
                }
                else {
                    repetition = false;
                    break;
                }
            }
            if (repetition){
                return memoire.get(memoire.size() - 2);
            }
            if (Arrays.equals(new Direction[]{memoire.get(memoire.size() - 1), memoire.get(memoire.size() - 2), memoire.get(memoire.size() - 3)}, new Direction[]{memoire.get(memoire.size() - 4), memoire.get(memoire.size() - 5), memoire.get(memoire.size() - 6)})) {
                return memoire.get(memoire.size() - 3);
            }
        }
        return null;
    }

    public int getNbIterations() {
        return nbIterations;
    }

    public void setNbIterations(int nbIterations) {
        this.nbIterations = nbIterations;
    }

    public void setNbIterNoMove(int nbIterNoMove) {
        this.nbIterNoMove = nbIterNoMove;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void emptyMemory(){
        this.memoire.clear();
    }
}


