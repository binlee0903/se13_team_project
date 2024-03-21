package org.se13.database.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.se13.database.entity.ConfigEntity;

public class ConfigRepositoryImpl implements IConfigRepository {
    ConfigRepositoryImpl() {
        setupHibernate();
    }

    @Override
    public void insertDefaultConfig() {
        ConfigEntity entity = new ConfigEntity();
        entity.setMode("default");
        entity.setScreenWidth(800);
        entity.setScreenHeight(600);

        sessionFactory.inTransaction(session -> {
            session.persist(entity);
        });
    }

    @Override
    public void updateConfig(ConfigEntity configEntity) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        ConfigEntity entity = session.get(ConfigEntity.class, 1);
        entity.setMode(configEntity.getMode());
        entity.setScreenWidth(configEntity.getScreenWidth());
        entity.setScreenHeight(configEntity.getScreenHeight());
        session.getTransaction().commit();
    }

    @Override
    public ConfigEntity getConfig() {
        sessionFactory.inTransaction(session -> {
            String query = "FROM config";
            config = session.createSelectionQuery(query, ConfigEntity.class).getResultList().getFirst();
        });

        return null;
    }

    private void setupHibernate() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();
        this.sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(ConfigEntity.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    private SessionFactory sessionFactory;
    private ConfigEntity config;
}
