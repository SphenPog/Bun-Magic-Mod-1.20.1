package net.sphen.magicmodbuns.screen.elements;

public class Line {

    public Dot start;
    public Dot end;
    public final boolean curved;

    public Line(Dot start, Dot end) {
        this(start, end, false); // default to straight
    }

    public Line(Dot start, Dot end, boolean curved) {
        this.start = start;
        this.end = end;
        this.curved = curved;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Line other = (Line) obj;
        return (start.equals(other.start) && end.equals(other.end)) ||
                (start.equals(other.end) && end.equals(other.start));
    }

    @Override
    public int hashCode() {
        // Ensure the hash code is the same regardless of start/end order
        return start.hashCode() + end.hashCode();
    }

}
