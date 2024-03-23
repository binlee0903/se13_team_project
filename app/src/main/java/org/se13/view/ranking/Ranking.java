package org.se13.view.ranking;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ranking {
    private SimpleIntegerProperty position;
    private SimpleStringProperty name;
    private SimpleIntegerProperty score;

    public Ranking(int position, String name, int score) {
        this.position = new SimpleIntegerProperty(position);
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleIntegerProperty(score);
    }

    public int getPosition() { return position.get(); }
    public String getName() { return name.get(); }
    public int getScore() { return score.get(); }

    public SimpleIntegerProperty positionProperty() { return position; }
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleIntegerProperty scoreProperty() { return score; }
}
