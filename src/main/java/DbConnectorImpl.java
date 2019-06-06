import api.DbConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class DbConnectorImpl implements DbConnector {

    private static final String JDBC_STRING = "jdbc:hsqldb:file:testdb";
    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(DbConnectorImpl.class);

    @Override
    public void connect() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            String user = "SA";
            char[] password = "".toCharArray();
            this.connection = DriverManager.getConnection(JDBC_STRING, user, new String(password)); //Storing password as a char array doesn't make a difference in this case, as in the end DriverManager requires a password as a String.
            logger.info("Successfully connected to the database");
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Could not connect to the database", e);
        }
    }

    @Override
    public void executeSql(String sql) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            logger.error("Could not execute SQL: {}", sql, e);
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.close();
            logger.info("Succesfully disconnected from the database.");
        } catch (SQLException e) {
            logger.error("Could not disconnect from DB. ", e);
        }
    }

    @Override
    public void persist(String query, List<Object> params) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int i = 1;
            for (Object o : params) {
                preparedStatement.setObject(i++, o);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("Could not persist. Failing SQL: {}, parameters: {}", query, params, e);
        }
    }
}
