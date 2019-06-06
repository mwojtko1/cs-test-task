import enums.StateEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.reflect.Whitebox;

@RunWith(JUnit4.class)
public class ParserTest {

    private Parser parser;

    @Before
    public void setUp() {
        parser = new Parser();
    }

    @Test
    public void testParseJsonWithExtendedLog() throws Exception {
        String id = "1000";
        String state = "STARTED";
        String type = "APPLICATION_LOG";
        String host = "12345";
        String timestamp = "1491377495212";
        String logString = "{\"id\":\"" + id + "\", \"state\":\"" + state + "\", \"type\":\"" + type + "\", \"host\":\"" + host + "\", \"timestamp\":" + timestamp + "}";
        Log log = Whitebox.invokeMethod(parser, "parseJson", logString);
        Assert.assertEquals(id, log.getId());
        Assert.assertEquals(StateEnum.valueOf(state), log.getState());
        Assert.assertEquals(type, log.getType().get());
        Assert.assertEquals(host, log.getHost().get());
        Assert.assertEquals(timestamp, log.getTimestamp());
    }

    @Test
    public void testParseJsonWithSimpleLog() throws Exception {
        String id = "2000";
        String state = "FINISHED";
        String timestamp = "1491377495233";
        String logString = "{\"id\":\"" + id + "\", \"state\":\"" + state + "\", \"timestamp\":" + timestamp + "}";
        Log log = Whitebox.invokeMethod(parser, "parseJson", logString);
        Assert.assertEquals(id, log.getId());
        Assert.assertEquals(StateEnum.valueOf(state), log.getState());
        Assert.assertEquals(timestamp, log.getTimestamp());
        Assert.assertFalse(log.getHost().isPresent());
        Assert.assertFalse(log.getType().isPresent());
    }

    @Test
    public void testCreateEventExtended() throws Exception {
        String id = "3000";
        long startTimestamp = 1491377495233L;
        long difference = 5L;
        String host = "testHost";
        String type = "testType";

        Event event = getEvent(id, startTimestamp, difference, host, type);

        Assert.assertEquals(id, event.getId());
        Assert.assertEquals(host, event.getHost());
        Assert.assertEquals(type, event.getType());
        Assert.assertEquals(difference, event.getDuration());
        Assert.assertTrue(event.isTooLong());
    }

    @Test
    public void testCreateEventSimple() throws Exception {
        String id = "4000";
        long startTimestamp = 1491377495233L;
        long difference = 5L;

        Event event = getEvent(id, startTimestamp, difference, null, null);

        Assert.assertEquals(id, event.getId());
        Assert.assertEquals(difference, event.getDuration());
        Assert.assertTrue(event.isTooLong());
        Assert.assertNull(event.getHost());
        Assert.assertNull(event.getType());
    }

    private Event getEvent(String id, long startTimestamp, long difference, String host, String type) throws Exception {
        Log firstLog = new Log(id, StateEnum.STARTED, String.valueOf(startTimestamp), host, type);
        Log secondLog = new Log(id, StateEnum.FINISHED, String.valueOf(startTimestamp + difference), host, type);
        return Whitebox.invokeMethod(parser, "createEvent", firstLog, secondLog);
    }
}