package model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Random;

public class Environnement {
    private ArrayList<Agent> agents;
    private Case[][] map;
    private Case[][] finalMap;

    public Environnement(int n, ArrayList<Agent> agents) {
        this.agents = agents;
        map = new Case[n][n];
        finalMap = new Case[n][n];
        int x,y;
        boolean found;

        for (int i = 0; i<n;i++){
            for (int j=0; j<n; j++){
                map[i][j] = new Case();
                finalMap[i][j] = new Case();
            }
        }

        // Conception du schéma initiale
        for(Agent a : agents){
            found = false;
            while(true){
                x = new Random().nextInt(map.length);
                y = new Random().nextInt(map[0].length);
                if(!map[x][y].isOccupied()){
                    map[x][y].setAgent(a);
                    break;
                }
            }
        }

        // Conception du schéma finale
        for(Agent a : agents){
            found = false;
            while(true){
                x = new Random().nextInt(finalMap.length);
                y = new Random().nextInt(finalMap[0].length);
                if(!finalMap[x][y].isOccupied()){
                    finalMap[x][y].setAgent(a);
                    break;
                }
            }
        }
    }

    public void start(){
        for(Agent agent: agents){
            agent.start();
        }
    }

    public void verify(){
        if (map == finalMap) {
            for(Agent a : agents) {
                a.interrupt();
            }
        }
    }

    public Point2D getXY(Agent a){
        for(int i = 0; i<map.length;i++){
            for(int j = 0; j<map[i].length;j++){
                if(a.equals(map[i][j].getOccupation())) return new Point2D(i,j);
            }
        }
        return null;
    }

    public boolean isOccupied(Agent.Direction d) {
        // TODO()
        return false;
    }

    public Case[][] getMap() {
        return map;
    }

    public Case[][] getFinalMap() {
        return finalMap;
    }

}
