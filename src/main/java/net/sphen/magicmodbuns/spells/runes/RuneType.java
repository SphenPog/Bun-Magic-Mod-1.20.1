package net.sphen.magicmodbuns.spells.runes;

public enum RuneType {
    LIGHT(1), WATER(2), EARTH(3), FIRE(4), AIR(5), DARKNESS(6), UNKNOWN(7);

    private RuneType(int id) {
        this.id = id;
    }

    private int id;

    public int getId() {
        return id;
    }

    public static RuneType getById(int id){
        switch (id){
            case 1:
                return RuneType.LIGHT;
            case 2:
                return RuneType.WATER;
            case 3:
                return RuneType.EARTH;
            case 4:
                return RuneType.FIRE;
            case 5:
                return RuneType.AIR;
            case 6:
                return RuneType.DARKNESS;
            case 7:
                return RuneType.UNKNOWN;
        }
        return null;
    }
}
