package org.se13.view.ranking;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Ranking {
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty position;
    private SimpleStringProperty name;
    private SimpleIntegerProperty score;
    private SimpleBooleanProperty isItem;
    private SimpleStringProperty diff;

    public Ranking(int id, int position, String name, int score, boolean isItem, String diff) {
        this.id = new SimpleIntegerProperty(id);
        this.position = new SimpleIntegerProperty(position);
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleIntegerProperty(score);
        this.isItem = new SimpleBooleanProperty(isItem);
        this.diff = new SimpleStringProperty(diff);
    }

    public int getId() { return id.get(); }
    public int getPosition() { return position.get(); }
    public String getName() { return name.get(); }
    public int getScore() { return score.get(); }
    public boolean getIsItem() { return isItem.get(); }
    public String getDiff() { return diff.get(); }

    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleIntegerProperty positionProperty() { return position; }
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleIntegerProperty scoreProperty() { return score; }
    public SimpleBooleanProperty isItemProperty() { return isItem; }
    public SimpleStringProperty diffProperty() { return diff; }
}
