package io.github.yoyama.graal;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Hello World");
        GraalTest1 graal = new GraalTest1();
        String str = graal.run1("echo>: ${moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}", "{\"session_time\":1}");
        System.out.println(str);
    }
}
