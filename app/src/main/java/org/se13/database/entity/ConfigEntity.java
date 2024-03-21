package org.se13.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "config")
public class ConfigEntity implements Serializable {
    public int getId() {
        return id;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public String getMode() {
        return this.mode;
    }

    public int getScreenWidth() {
        return this.screenWidth;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }

    @Id
    @GeneratedValue
    private int id;

    private String mode;
    private int screenWidth;
    private int screenHeight;
}