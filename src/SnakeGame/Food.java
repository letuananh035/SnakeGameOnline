package SnakeGame;

import javafx.geometry.Point2D;

import java.awt.*;

public class Food {

    private Point position;

    public Food(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
