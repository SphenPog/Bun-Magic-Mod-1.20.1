package net.sphen.magicmodbuns.screen.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatternObject {

    private final List<Line> lines = new ArrayList<>();
    private boolean withered = false;

    public void addLine(Dot start, Dot end, boolean curved) {
        Dot first = (start.compareTo(end) <= 0) ? start : end;
        Dot second = (start.compareTo(end) <= 0) ? end : start;

        lines.add(new Line(start, end, curved));
    }

    //store pattern in text format
    public String storeData() {
        StringBuilder data = new StringBuilder();
        for (Line line : lines) {
            data.append(line.start.gridX).append(",").append(line.start.gridY)
                    .append("->")
                    .append(line.end.gridX).append(",").append(line.end.gridY)
                    .append(line.curved ? ":c" : "")
                    .append(";");
        }
        System.out.println("Storing Pattern Data: " + data.toString());
        return data.toString();
    }



    public static PatternObject loadData(String data) {

        PatternObject pattern = new PatternObject();
        if (data == null || data.isEmpty()){
            return pattern;
        }
        String[] lineData = data.split(";");
        for (String entry : lineData) {
            if (!entry.isEmpty()) {
                String[] points = entry.split("->");
                String[] startCoords = points[0].split(",");
                boolean curved = points[1].contains(":c");
                String[] endCoords = points[1].replace(":c", "").split(",");

                Dot start = new Dot(Integer.parseInt(startCoords[0]), Integer.parseInt(startCoords[1]));
                Dot end = new Dot(Integer.parseInt(endCoords[0]), Integer.parseInt(endCoords[1]));
                pattern.addLine(start, end, curved);
            }
        }
        return pattern;
    }

    public List<Line> getLines() {
        return lines;
    }

    public String getSortedLines(){
        return lines.stream()
                .sorted((a, b) -> {
                    int comparison = Integer.compare(a.start.gridX, b.start.gridX);
                    if (comparison != 0) return comparison;

                    comparison = Integer.compare(a.start.gridY, b.start.gridY);
                    if (comparison != 0) return comparison;

                    comparison = Integer.compare(a.end.gridX, b.end.gridX);
                    if (comparison != 0) return comparison;

                    comparison = Integer.compare(a.end.gridY, b.end.gridY);
                    if (comparison != 0) return comparison;

                    return Boolean.compare(a.curved, b.curved);
                })
                .map(Line::toString)
                .collect(Collectors.joining(";"));
    }

    public void removeLine(Line line) {
        lines.remove(line);
    }
}
