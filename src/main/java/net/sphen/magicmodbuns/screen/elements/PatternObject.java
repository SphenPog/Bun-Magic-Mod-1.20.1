package net.sphen.magicmodbuns.screen.elements;

import java.util.ArrayList;
import java.util.List;

public class PatternObject {

    private final List<Line> lines = new ArrayList<>();

    public void addLine(Dot start, Dot end) {
        lines.add(new Line(start, end));
    }

    //store pattern in text format
    public String storeData() {
        StringBuilder data = new StringBuilder();
        for (Line line : lines) {
            data.append(line.start.gridX).append(",").append(line.start.gridY)
                    .append("->")
                    .append(line.end.gridX).append(",").append(line.end.gridY)
                    .append(";");
        }
        System.out.println("Storing Pattern Data: " + data.toString());
        return data.toString();
    }

    public static PatternObject loadData(String data) {
        System.out.println("Loading Pattern Data: " + data);

        PatternObject pattern = new PatternObject();
        if (data == null || data.isEmpty()){
            return pattern;
        }
        String[] lineData = data.split(";");
        for (String entry : lineData) {
            if (!entry.isEmpty()) {
                String[] points = entry.split("->");
                String[] startCoords = points[0].split(",");
                String[] endCoords = points[1].split(",");

                Dot start = new Dot(Integer.parseInt(startCoords[0]), Integer.parseInt(startCoords[1]));
                Dot end = new Dot(Integer.parseInt(endCoords[0]), Integer.parseInt(endCoords[1]));
                pattern.addLine(start, end);
            }
        }
        return pattern;
    }

    public List<Line> getLines(){
        return lines;
    }
}
