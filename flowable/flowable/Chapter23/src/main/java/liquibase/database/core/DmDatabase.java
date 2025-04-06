package liquibase.database.core;

import liquibase.GlobalConfiguration;
import liquibase.Scope;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.database.OfflineConnection;
import liquibase.database.core.OracleDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawCallStatement;
import liquibase.util.JdbcUtil;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DmDatabase extends OracleDatabase {

    public static final String PRODUCT_NAME = "DM DBMS";

    public String getShortName() {
        return "dm";
    }

    protected String getDefaultDatabaseProductName() {
        return PRODUCT_NAME;
    }

    public Integer getDefaultPort() {
        return 5236;
    }

    public String getDefaultDriver(String url) {
        return url.startsWith("jdbc:dm") ? "dm.jdbc.driver.DmDriver" : null;
    }

    public boolean isCorrectDatabaseImplementation(DatabaseConnection var1) throws DatabaseException {
        return PRODUCT_NAME.equalsIgnoreCase(var1.getDatabaseProductName());
    }

    public boolean supportsSchemas() {
        return false;
    }

    protected SqlStatement getConnectionSchemaNameCallStatement() {
        return new RawCallStatement("select SYS_CONTEXT('USERENV','CURRENT_SCHEMA') as schema_name from dual");
    }

}
