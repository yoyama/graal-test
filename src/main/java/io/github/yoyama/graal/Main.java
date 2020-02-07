package io.github.yoyama.graal;


import java.io.IOException;
import java.util.List;
import org.openjdk.jmh.annotations.Benchmark;

public class Main
{
    public static List<TestParam> TestData = TestParam.TestData;
    private static final int TEST_LOOP = 10;

     public static void main(String[] args) throws IOException
     {
         org.openjdk.jmh.Main.main(args);
     }

    @Benchmark
    public void benchmarkGraalJS()
    {
        GraalEval graal = new GraalEval();
        for ( int i = 0; i < TEST_LOOP; i++) {
            for (TestParam tp : TestData) {
                String str = graal.eval(tp.getTemplate(), tp.getParams());
                //System.out.println(str);
            }
        }
    }

    @Benchmark
    public void benchmarkGraalJSNoCompat()
    {
        GraalEval graal = new GraalEval(false); //nashorn compatible mode is false
        for ( int i = 0; i < TEST_LOOP; i++) {
            for (TestParam tp : TestData) {
                String str = graal.eval(tp.getTemplate(), tp.getParams());
            }
        }
    }

    @Benchmark
    public void benchmarkNashornOriginal()
    {
        NashornEval nashorn = new NashornEval();
        for ( int i = 0; i < TEST_LOOP; i++) {
            for (TestParam tp : TestData) {
                String str = nashorn.eval(tp.getTemplate(), tp.getParams());
            }
        }
    }

    @Benchmark
    public void benchmarkNashornImproved()
    {
        String[] options = getJdkMajorVersin() < 11 ?
                        new String[] {} :
                        new String[] {"--no-deprecation-warning", "--optimistic-types=false"};
        NashornEval nashorn = new NashornEval(options);
        for ( int i = 0; i < TEST_LOOP; i++) {
            for (TestParam tp : TestData) {
                String str = nashorn.eval(tp.getTemplate(), tp.getParams());
            }
        }
    }

    public int getJdkMajorVersin()
    {
        String javaSpecVer = System.getProperty("java.specification.version");
        return Integer.parseInt(javaSpecVer.split("[^0-9]")[0]);
    }
}
