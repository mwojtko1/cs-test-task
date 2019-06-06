import api.DbConnector;
import enums.StateEnum;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;


public class Parser {

    private static final String CHARSET = "UTF-8";
    private static final int MAXIMUM_REGULAR_DURATION_TIME_IN_MILIS = 4;
    private static final String QUERY_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS EVENT ( " +
                    "ID VARCHAR(20) NOT NULL, " +
                    "DURATION INT NOT NULL, " +
                    "TYPE VARCHAR(20) , " +
                    "HOST VARCHAR(20) , " +
                    "ALERT CHAR(1) , " +
                    "PRIMARY KEY (id));";
    private static final String QUERY_CLEAR_EVENTS = "DELETE FROM EVENT;";
    private static final String QUERY_INSERT = "INSERT INTO EVENT VALUES (?, ?, ?, ?, ?)";
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private DbConnector dbConnector = new DbConnectorImpl();

    public void parseFile(String filename) throws Exception {
        dbConnector.connect();
        createEventsTable();
        clearEventsTable();
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            Scanner scanner = new Scanner(inputStream, CHARSET);
            Map<String, Log> startLogsMap = new HashMap<>();
            Map<String, Log> finishLogsMap = new HashMap<>();

            while (scanner.hasNext()) {
                String singleLine = scanner.nextLine();
                processSingleLogLine(singleLine, startLogsMap, finishLogsMap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            dbConnector.disconnect();
        }
    }

    private void clearEventsTable() {
        dbConnector.executeSql(QUERY_CLEAR_EVENTS);
    }

    private void createEventsTable() {
        dbConnector.executeSql(QUERY_CREATE_TABLE);
    }

    private void processSingleLogLine(String line, Map<String, Log> startLogsMap, Map<String, Log> finishLogsMap) {
        Log log = parseJson(line);
        if (log == null) {
            return;
        } else if (StateEnum.STARTED == log.getState()) {
            processStartingLog(startLogsMap, finishLogsMap, log);
        } else if (StateEnum.FINISHED == log.getState()) {
            processFinishingLog(startLogsMap, finishLogsMap, log);
        }
    }

    private Log parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String id = jsonObject.getString("id");
            StateEnum state = Enum.valueOf(StateEnum.class, jsonObject.getString("state"));
            String timestamp = jsonObject.getString("timestamp");
            String host = jsonObject.has("host") ? jsonObject.getString("host") : null;
            String type = jsonObject.has("type") ? jsonObject.getString("type") : null;
            return new Log(id, state, timestamp, host, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processStartingLog(Map<String, Log> startLogsMap, Map<String, Log> finishLogsMap, Log log) {
        processLog(startLogsMap, finishLogsMap, log);
    }

    private void processFinishingLog(Map<String, Log> startLogsMap, Map<String, Log> finishLogsMap, Log log) {
        processLog(finishLogsMap, startLogsMap, log);
    }

    private void processLog(Map<String, Log> mapToAdd, Map<String, Log> mapToLookUp, Log secondLog) {
        Log firstLog = mapToLookUp.remove(secondLog.getId());
        if (firstLog == null) {
            mapToAdd.put(secondLog.getId(), secondLog);
        } else {
            Event event = createEvent(secondLog, firstLog);
            List<Object> params = prepareParametersListForEvent(event);
            dbConnector.persist(QUERY_INSERT, params);
        }
    }

    private List<Object> prepareParametersListForEvent(Event event) {
        List<Object> params = new ArrayList<>();
        params.add(event.getId());
        params.add(event.getDuration());
        params.add(event.getType());
        params.add(event.getHost());
        params.add(event.isTooLong());
        return params;
    }

    private Event createEvent(Log started, Log finished) {
        int duration = countDuration(started, finished).intValue();
        boolean isTooLong = duration > MAXIMUM_REGULAR_DURATION_TIME_IN_MILIS;
        if (isTooLong) {
            logger.warn("Long event detected. ID = {}", started.getId());
        }
        String host = started.getHost().isPresent() ? started.getHost().get() : null;
        String type = started.getType().isPresent() ? started.getType().get() : null;
        return new Event(started.getId(), duration, type, host, isTooLong);
    }

    private Long countDuration(Log started, Log finished) {
        Long startTime = Long.valueOf(started.getTimestamp());
        Long finishTime = Long.valueOf(finished.getTimestamp());
        return Math.abs(finishTime - startTime);
    }
}
