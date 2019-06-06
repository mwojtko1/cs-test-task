public class Event {
    private String id;
    private int duration;
    private String type;
    private String host;
    private boolean tooLong;

    public Event(String id, int duration, String type, String host, boolean alert) {
        this.id = id;
        this.duration = duration;
        this.type = type;
        this.host = host;
        this.tooLong = alert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isTooLong() {
        return tooLong;
    }

    public void setTooLong(boolean tooLong) {
        this.tooLong = tooLong;
    }
}
