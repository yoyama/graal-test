package io.github.yoyama.graal;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NashornEval
{
    private final NashornScriptEngineFactory jsEngineFactory;

    private final Source[] libraryJsSources;

    private static final String[][] LIBRARY_JS_RESOURCES = {
            new String[] { "digdag.js", "/io/digdag/core/agent/digdag.js" },
            new String[] { "moment.min.js", "/io/digdag/core/agent/moment.min.js" },
    };

    static final String[][] LIBRARY_JS_CONTENTS;

    static {
        LIBRARY_JS_CONTENTS = new String[LIBRARY_JS_RESOURCES.length][];
        for (int i = 0; i < LIBRARY_JS_RESOURCES.length; i++) {
            String contents = readResource(LIBRARY_JS_RESOURCES[i][1]);
            LIBRARY_JS_CONTENTS[i] = new String[] {
                    LIBRARY_JS_RESOURCES[i][0],
                    contents
            };
        }
    }

    public NashornEval()
    {
        this.jsEngineFactory = new NashornScriptEngineFactory();
        try {
            this.libraryJsSources = new Source[LIBRARY_JS_CONTENTS.length];
            for (int i = 0; i < LIBRARY_JS_CONTENTS.length; i++) {
                libraryJsSources[i] = Source.newBuilder("js", LIBRARY_JS_CONTENTS[i][1], LIBRARY_JS_CONTENTS[i][0]).build();
            }
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private static String readResource(String resourceName)
    {
        try (InputStream in = NashornEval.class.getResourceAsStream(resourceName)) {
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

    public String eval(String code, String paramJson)
    {
        ScriptEngine scriptEngine = jsEngineFactory.getScriptEngine(new String[] {
                //"--language=es6",  // this is not even accepted with jdk1.8.0_20 and has a bug with jdk1.8.0_51
                "--no-java",
                "--no-syntax-extensions"
        });
        try {
            for (int i = 0; i < LIBRARY_JS_CONTENTS.length; i++) {
                scriptEngine.eval(LIBRARY_JS_CONTENTS[i][1]);
            }
            final Invocable invocable = (Invocable) scriptEngine;
            return (String) invocable.invokeFunction("template", code, paramJson);
        }
        catch (ScriptException | ClassCastException ex) {
            throw new IllegalStateException("Unexpected script evaluation failure", ex);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException("Failed to evaluate JavaScript code: " + code, ex);
        }
    }
}

