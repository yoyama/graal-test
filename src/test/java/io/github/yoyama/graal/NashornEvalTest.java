package io.github.yoyama.graal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NashornEvalTest {
    NashornEval eval;

    @Before
    public void setUp()
    {
        eval = new NashornEval();
    }

    @Test
    public void evalTest()
    {
        String ret = eval.eval("echo>: ${moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}",
            "{\"session_time\":1581241139000}");
        Assert.assertEquals("echo>: 2020-02-09 18:38:59 +09:00", ret);
    }
    @Test
    public void evalWithImprovedTest()
    {
        eval = new NashornEval(new String[] {"--no-deprecation-warning", "--optimistic-types=false"});
        String ret = eval.eval("echo>: ${moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}",
                "{\"session_time\":1581241139000}");
        Assert.assertEquals("echo>: 2020-02-09 18:38:59 +09:00", ret);
    }
}
