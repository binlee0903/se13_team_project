package org.se13.database.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "config")
public class ConfigEntity implements java.io.Serializable {
    @Id
    @GeneratedValue
    private int id;

    private String mode;
    private int screenWidth;
    private int screenHeight;

}