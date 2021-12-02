package model;

import javafx.geometry.Point2D;

import java.util.*;

public class Agent extends Thread {

    private HashMap<Agent, LinkedList<String>> sharedQueue;
    private Environnement e;
    private int finalX, finalY;
    private Direction d;
    private String name;

    public enum Direction{
        TOP,
        BOTTOM,
        RIGHT,
        LEFT
    }

    public Agent(String name){
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

    public Direction chemin(){
        Point2D position = e.getXY(this);
        int x = (int) position.getX();
        int y = (int) position.getY();
        int diffX = finalX - x;
        int diffY = finalY - y;
        if(Math.abs(diffX) >= Math.abs(diffY)) {
            if(diffX < 0) return Direction.TOP;
            else return Direction.BOTTOM;
        } else {
            if(diffY<0) return Direction.LEFT;
            else return Direction.RIGHT;
        }
    }

    public void decide(){
        d = chemin();
        if(e.isOccupied(d)){
            // communicate pour demander à l'agent de bouger
        }
    }
}
