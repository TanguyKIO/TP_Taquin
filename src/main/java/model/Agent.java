package model;

import javafx.geometry.Point2D;

import java.util.*;

public class Agent extends Thread {

    private HashMap<Agent, LinkedList<String>> sharedQueue;
    private Environnement e;
    private int finalX, finalY;
    private int currentX, currentY;
    private Direction d;
    private int name;

    public Agent(int name) {
        this.name = name;
    }

    @Override
    public void run() {

        // Communiquer
        // Décider
        // Appliquer
        // Raisonner
        LinkedList<String> messages = sharedQueue.get(this);
        decide();

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

    public LinkedList<Direction> chemin() {
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

    public void decide() {
        LinkedList<Direction> availableDirections = chemin();
        for(Direction d : availableDirections){
            if(!e.isOccupied(d));
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
}


enum Direction {
    TOP,
    BOTTOM,
    RIGHT,
    LEFT
}
