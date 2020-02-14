package io.github.yoyama.graal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JsEvalUtils
{
    public static String readResource(String resourceName)
    {
        try (InputStream in = GraalEval.class.getResourceAsStream(resourceName)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8));
            StringBuffer sb = new StringBuffer();
            String str;
            while((str = reader.readLine())!= null){
                sb.append(str);
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int getJdkMajorVersin()
    {
        String javaSpecVer = System.getProperty("java.specification.version");
        return Integer.parseInt(javaSpecVer.split("[^0-9]")[0]);
    }

}
