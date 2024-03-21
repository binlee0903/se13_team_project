package org.se13.database.config;

import org.se13.database.entity.ConfigEntity;

import java.util.Map;

public interface IConfigRepository {
    void insertDefaultConfig();
    void updateConfig(ConfigEntity configEntity);
    ConfigEntity getConfig();
}
