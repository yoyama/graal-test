package io.github.yoyama.graal;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.openjdk.jmh.annotations.Benchmark;

public class Main
{
    public static class TestParam
    {
        private String template;
        private String params;
        public TestParam(String template, String params)
        {
            this.template = template;
            this.params = params;
        }

        public static TestParam of(String template, String params)
        {
            return new TestParam(template, params);
        }
    }
    public static List<TestParam> TestData = Arrays.asList(
      TestParam.of("echo>: ${moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}", "{\"session_time\":1}")
    );


     public static void main(String[] args) throws IOException
     {
         org.openjdk.jmh.Main.main(args);
     }

    @Benchmark
    public void benchmarkGraalJS()
    {
        for (TestParam tp: TestData) {
            GraalEval graal = new GraalEval();
            String str = graal.eval(tp.template, tp.params);
        }
    }

    @Benchmark
    public void benchmarkNashornOriginal()
    {
        for (TestParam tp: TestData) {
            NashornEval nashorn = new NashornEval();
            String str = nashorn.eval(tp.template, tp.params);
        }
    }

    @Benchmark
    public void benchmarkNashornImproved()
    {
        for (TestParam tp: TestData) {
            String[] options = new String[] {"--no-deprecation-warning", "--optimistic-types=false"};
            NashornEval nashorn = new NashornEval(options);
            String str = nashorn.eval(tp.template, tp.params);
        }
    }
}
