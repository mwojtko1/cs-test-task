import enums.StateEnum;

import java.util.Optional;

public class Log {
    private String id;
    private StateEnum state;
    private String timestamp;
    private String type;
    private String host;

    Log(String id, StateEnum state, String timestamp, String host, String type) {
        this.id = id;
        this.state = state;
        this.timestamp = timestamp;
        this.host = host;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }

    public void setType(String type) {
        this.type = type;
    }
}
