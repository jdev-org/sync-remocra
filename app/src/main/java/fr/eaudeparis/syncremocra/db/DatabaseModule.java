package fr.eaudeparis.syncremocra.db;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.postgresql.ds.PGSimpleDataSource;

public class DatabaseModule extends AbstractModule {

  public static DatabaseModule create(Config config, Config flywayConfig) {
    return new DatabaseModule(toProperties(config), toProperties(flywayConfig));
  }

  private final Properties datasourceProperties = new Properties();
  private final Properties flywayProperties = new Properties();

  public DatabaseModule(Properties datasourceProperties, Properties flywayProperties) {
    this.datasourceProperties.putAll(datasourceProperties);
    this.flywayProperties.putAll(flywayProperties);
  }

  @Provides
  @Singleton
  DSLContext provideDSLContext(DataSource dataSource) {
    return DSL.using(dataSource, SQLDialect.POSTGRES);
  }

  @Override
  protected void configure() {
    // Pas besoin d'un pool de connexions
    bind(DataSource.class).to(PGSimpleDataSource.class);
    bind(SQLDialect.class).toInstance(SQLDialect.POSTGRES);
  }

  @Provides
  @Singleton
  PGSimpleDataSource providePGSimpleDataSourceDataSource() {
    final PGSimpleDataSource datasource = new PGSimpleDataSource();
    datasource.setServerNames(new String[] {datasourceProperties.getProperty("serverName")});
    datasource.setDatabaseName(datasourceProperties.getProperty("databaseName"));
    datasource.setPortNumbers(
        new int[] {Integer.parseInt(datasourceProperties.getProperty("portNumber"))});
    datasource.setUser(datasourceProperties.getProperty("user"));
    datasource.setPassword(datasourceProperties.getProperty("password"));
    datasource.setSsl(Boolean.parseBoolean(datasourceProperties.getProperty("ssl")));
    datasource.setSslfactory(datasourceProperties.getProperty("sslfactory"));
    return datasource;
  }

  @Provides
  @Singleton
  Flyway provideFlyway(DataSource dataSource) {
    return Flyway.configure().configuration(flywayProperties).dataSource(dataSource).load();
  }

  private static Properties toProperties(Config config) {
    Properties properties = new Properties();
    for (Map.Entry<String, ConfigValue> entry : config.entrySet()) {
      properties.put(entry.getKey(), entry.getValue().unwrapped().toString());
    }
    return properties;
  }
}
