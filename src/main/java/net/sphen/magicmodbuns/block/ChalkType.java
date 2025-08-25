package net.sphen.magicmodbuns.block;

import java.awt.*;

public enum ChalkType {
    LIGHT(Color.YELLOW, 1), WATER(Color.BLUE, 2), EARTH(Color.GREEN, 3), FIRE(Color.RED, 4), AIR(Color.GRAY, 5), DARKNESS(Color.BLACK, 6), UNKNOWN(Color.WHITE, 7);

    private Color color;
    private int id;

    private ChalkType(Color color, int id){
        this.color = color;
        this.id = id;
    }

    public Color getValue(){
       return this.color;
    }

    public int getId() {
        return id;
    }

    public static ChalkType getById(int id){
        switch (id){
            case 1:
                return ChalkType.LIGHT;
            case 2:
                return ChalkType.WATER;
            case 3:
                return ChalkType.EARTH;
            case 4:
                return ChalkType.FIRE;
            case 5:
                return ChalkType.AIR;
            case 6:
                return ChalkType.DARKNESS;
            case 7:
                return ChalkType.UNKNOWN;
        }
        return null;
    }


}
