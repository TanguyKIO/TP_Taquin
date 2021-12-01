package com.example.tp_taquin.model;

public class Case {
    private Agent occupation;

    public void setAgent(Agent a) {
        occupation = a;
    }

    public void removeAgent() {
        occupation = null;
    }

    public boolean isOccupied() {
        if (occupation == null) return false;
        return true;
    }

    public Agent getOccupation() {
        return occupation;
    }
}
