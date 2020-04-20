package group.skids.requiem.utils;

public class Plane {
    private final double x;
    private final double y;

    private final double width;
    private final double height;

    private final boolean visible;

    public Plane(double x, double y, double width, double height, boolean visible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = visible;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public boolean isVisible() {
        return visible;
    }
}