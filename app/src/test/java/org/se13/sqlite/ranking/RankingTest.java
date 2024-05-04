package org.se13.sqlite.ranking;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankingTest {
    @Test
    @Order(1)
    void testRanking() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        assertEquals(1, ranking.getId());
        assertEquals("test", ranking.getName());
        assertEquals(100, ranking.getScore());
        assertFalse(ranking.isItem());
        assertEquals("test", ranking.getDiff());
    }

    @Test
    void testGetId() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        assertEquals(1, ranking.getId());
    }

    @Test
    void testGetName() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        assertEquals("test", ranking.getName());
    }

    @Test
    void testGetScore() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        assertEquals(100, ranking.getScore());
    }

    @Test
    void testIsItem() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        assertFalse(ranking.isItem());
    }

    @Test
    void testGetDiff() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        assertEquals("test", ranking.getDiff());
    }

    @Test
    void testSetId() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        ranking.setId(2);
        assertEquals(2, ranking.getId());
    }

    @Test
    void testSetName() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        ranking.setName("test2");
        assertEquals("test2", ranking.getName());
    }

    @Test
    void testSetScore() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        ranking.setScore(200);
        assertEquals(200, ranking.getScore());
    }

    @Test
    void setItem() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        ranking.setItem(true);
        assertTrue(ranking.isItem());
    }

    @Test
    void testSetDiff() {
        Ranking ranking = new Ranking(1, "test", 100, false, "test");
        ranking.setDiff("test2");
        assertEquals("test2", ranking.getDiff());
    }
}