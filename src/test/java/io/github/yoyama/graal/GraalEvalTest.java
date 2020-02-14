package io.github.yoyama.graal;

import org.graalvm.polyglot.PolyglotException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GraalEvalTest {
    GraalEval eval;

    @Before
    public void setUp()
    {
        eval = new GraalEval();
    }

    @Test
    public void evalTest()
    {
        String ret = eval.eval("echo>: ${moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}",
            "{\"session_time\":1581241139000}");
        Assert.assertEquals("echo>: 2020-02-09 18:38:59 +09:00", ret);
    }

    @Test(expected = IllegalStateException.class)
    public void mustErrorConsole()
    {
        String ret = eval.eval("echo>: ${console.log('aaaa')&&moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}",
                "{\"session_time\":1581241139000}");
    }

    /**
    @Test(expected = IllegalStateException.class)
    public void mustErrorLoad()
    {
        String ret = eval.eval("echo>: ${load('classpath:test1.js')}","{}");
        System.out.println(ret);
    }
     */

    /**
    @Test(expected = IllegalStateException.class)
    public void mustErrorPrint()
    {
        String ret = eval.eval("echo>: ${print('test test')}","{}");
        System.out.println(ret);
    }
     */


    @Test(expected = IllegalStateException.class)
    public void mustErrorNashronExt()
    {
        String ret = eval.eval("echo>: ${function sqr(x) x*x&&sqr(3)}","{}");
        System.out.println(ret);
    }

    @Test(expected = IllegalStateException.class)
    public void mustErrorECMA6()
    {
        String ret = eval.eval("echo>: ${0b111110111 === 503}","{}");
        System.out.println(ret);
    }

}
