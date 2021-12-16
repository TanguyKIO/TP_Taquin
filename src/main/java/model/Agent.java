package model;

import java.util.*;

public class Agent extends Observable implements Runnable {

    public void setE(Environnement env) {
        this.env = env;
    }

    private Environnement env;
    private int finalX, finalY;
    private int currentX, currentY;
    private int name;
    private boolean interupt;

    public Agent(int name) {
        this.name = name;
    }

    @Override
    public synchronized void run() {
        while (true) {
            synchronized (this) {
                if (!interupt) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    action(false);
                    setChanged();
                    notifyObservers();
                    env.verify();
                }
            }
        }
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

    public LinkedList<Direction> path() {
        LinkedList<Direction> directions = new LinkedList<>();
        int diffX = finalX - currentX;
        int diffY = finalY - currentY;
        if (Math.abs(diffX) >= Math.abs(diffY)) {
            if (diffX < 0) directions.add(Direction.TOP);
            else directions.add(Direction.BOTTOM);
            if (diffY < 0) {
                directions.add(Direction.LEFT);
                if (directions.contains(Direction.TOP)) {
                    directions.add(Direction.BOTTOM);
                } else {
                    directions.add(Direction.TOP);
                }
                directions.add(Direction.RIGHT);
            } else {
                directions.add(Direction.RIGHT);
                if (directions.contains(Direction.TOP)) {
                    directions.add(Direction.BOTTOM);
                } else {
                    directions.add(Direction.TOP);
                }
                directions.add(Direction.LEFT);
            }
        } else {
            if (diffY < 0) directions.add(Direction.LEFT);
            else directions.add(Direction.RIGHT);
            if (diffX < 0) {
                directions.add(Direction.TOP);
                if (directions.contains(Direction.LEFT)) {
                    directions.add(Direction.RIGHT);
                } else {
                    directions.add(Direction.LEFT);
                }
                directions.add(Direction.BOTTOM);
            } else {
                directions.add(Direction.BOTTOM);
                if (directions.contains(Direction.LEFT)) {
                    directions.add(Direction.RIGHT);
                } else {
                    directions.add(Direction.LEFT);
                }
                directions.add(Direction.TOP);
            }
        }
        return directions;
    }

    public void action(boolean forced) {
        if ((finalX != currentX || finalY != currentY) || forced) {
            LinkedList<Direction> availableDirections = path();
            int count = 0;
            for (Direction d : availableDirections) {
                Agent blockingAgent = env.whichAgentBlocking(currentX, currentY, d);
                if (blockingAgent == null && env.movePossible(currentX, currentY, d)) {
                    env.move(this, d);
                    break;
                } else if (count >= 1 && !forced && blockingAgent != null) {
                    System.out.println(name + "pushed agent " + blockingAgent.getNom());
                    blockingAgent.action(true);
                }
                count++;
            }
        }
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
}

enum Direction {
    TOP,
    BOTTOM,
    RIGHT,
    LEFT
}
