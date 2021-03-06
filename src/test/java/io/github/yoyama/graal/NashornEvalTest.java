package io.github.yoyama.graal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


import javax.script.ScriptException;

public class NashornEvalTest extends JsEvalUtils{
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
        String[] options = (getJdkMajorVersin() < 11)?
                new String[] {}:
                new String[] {"--no-deprecation-warning", "--optimistic-types=false"};
        eval = new NashornEval(options);
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

    /** Sould't ban this call?
    @Test(expected = IllegalStateException.class)
    public void mustErrorLoad()
    {
        String ret = eval.eval("echo>: ${load('classpath:test1.js')}","{}");
        System.out.println(ret);
    }
     */

    /** Sould't ban this call?
    @Test(expected = IllegalStateException.class)
    public void mustErrorPrint()
    {
        String ret = eval.eval("echo>: ${print('test test')}","{}");
        System.out.println(ret);
    }
    */

    /** Sould't ban this call?
    @Test(expected = IllegalStateException.class)
    public void mustErrorLoadFromUrl()
    {
        String ret = eval.eval("echo>: ${load('https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js')}","{}");
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

    @Test(expected = IllegalStateException.class)
    public void mustNotShared()
    {
        String ret1 = eval.eval("echo>: ${i=999}","{}");
        System.out.println(ret1);
        String ret2 = eval.eval("echo>: ${i*2}","{}");
        System.out.println(ret2);
    }

    @Test
    public void mustShared2()
    {
        String ret1 = eval.eval("echo>: ${i=999} ${i*2}","{}");
        System.out.println(ret1);
        assertEquals("echo>: 999 1998", ret1);
    }

    @Test
    public void JavaClass()
    {
        String ret1 = eval.eval("echo>: ${'aaa'.replaceAll('a', 'b')}", "{}");
        System.out.println(ret1);
        assertEquals("echo>: bbb", ret1);
    }

    @Test(expected = IllegalStateException.class)
    public void JavaClass2()
    {
        String ret2 = eval.eval("echo>: ${new Integer(10)}", "{}");
        System.out.println(ret2);
        assertEquals("echo>: 10", ret2);
    }

}
