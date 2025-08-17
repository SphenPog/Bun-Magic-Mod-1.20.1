package net.sphen.magicmodbuns.screen.elements;

public class Dot {
    public int gridX;
    public int gridY;

    public Dot(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;

    }

    public int compareTo(Dot other) {
         int comparison = Integer.compare(this.gridX, other.gridX);
         return (comparison != 0) ? comparison : Integer.compare(this.gridY, other.gridY);
    }

    @Override
    public String toString() {
        return gridX + "," + gridY;
    }
}
