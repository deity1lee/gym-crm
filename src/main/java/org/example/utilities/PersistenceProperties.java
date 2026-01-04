package org.example.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PersistenceProperties {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.driver}")
    private String driver;

    @Value("${hibernate.hbm2ddl.auto}")
    private String ddlAuto;

    @Value("${hibernate.show_sql}")
    private boolean showSql;

    @Value("${hibernate.dialect}")
    private String dialect;

    public String getUrl() {
        return url;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getDriver() {
        return driver;
    }
    public String getDdlAuto() {
        return ddlAuto;
    }
    public boolean isShowSql() {
        return showSql;
    }
    public String getDialect() {
        return dialect;
    }
}
