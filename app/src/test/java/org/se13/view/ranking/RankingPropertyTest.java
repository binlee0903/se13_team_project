package org.se13.view.ranking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Test;

public class RankingPropertyTest {
  
    // Testing RankingProperty getId method
    @Test
    public void testGetId() {
        RankingProperty property = new RankingProperty(1, 2, "name", 3, true, "diff");
        assertEquals(1, property.getId());

        SimpleIntegerProperty id = new SimpleIntegerProperty(1);
        assertEquals(id.get(), property.idProperty().get());
    }

    // Testing RankingProperty getPosition method
    @Test
    public void testGetPosition() {
        RankingProperty property = new RankingProperty(1, 2, "name", 3, true, "diff");
        assertEquals(2, property.getPosition());

        SimpleIntegerProperty position = new SimpleIntegerProperty(2);
        assertEquals(position.get(), property.positionProperty().get());
    }

    // Testing RankingProperty getName method
    @Test
    public void testGetName() {
        RankingProperty property = new RankingProperty(1, 2, "name", 3, true, "diff");
        assertEquals("name", property.getName());

        SimpleStringProperty name = new SimpleStringProperty("name");
        assertEquals(name.get(), property.nameProperty().get());
    }

    // Testing RankingProperty getScore method
    @Test
    public void testGetScore() {
        RankingProperty property = new RankingProperty(1, 2, "name", 3, true, "diff");
        assertEquals(3, property.getScore());

        SimpleIntegerProperty score = new SimpleIntegerProperty(3);
        assertEquals(score.get(), property.scoreProperty().get());
    }

    // Testing RankingProperty getIsItem method
    @Test
    public void testGetIsItem() {
        RankingProperty property = new RankingProperty(1, 2, "name", 3, true, "diff");
        assertTrue(property.getIsItem());

        SimpleBooleanProperty isItem = new SimpleBooleanProperty(true);
        assertEquals(isItem.get(), property.isItemProperty().get());
    }

    // Testing RankingProperty getDiff method
    @Test
    public void testGetDiff() {
        RankingProperty property = new RankingProperty(1, 2, "name", 3, true, "diff");
        assertEquals("diff", property.getDiff());

        SimpleStringProperty diff = new SimpleStringProperty("diff");
        assertEquals(diff.get(), property.diffProperty().get());
    }
}