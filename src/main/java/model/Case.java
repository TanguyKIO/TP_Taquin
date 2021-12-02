package model;

public class Case {
    private Agent occupation;

    public void setAgent(Agent a) {
        occupation = a;
    }

    public void removeAgent() {
        occupation = null;
    }

    public boolean isOccupied() {
        return occupation != null;
    }

    public Agent getOccupation() {
        return occupation;
    }
}
