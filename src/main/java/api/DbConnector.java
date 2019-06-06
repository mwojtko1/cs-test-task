package api;

import java.util.List;

public interface DbConnector {
    void connect();

    void executeSql(String sql);

    void disconnect();

    void persist(String query, List<Object> params);
}
