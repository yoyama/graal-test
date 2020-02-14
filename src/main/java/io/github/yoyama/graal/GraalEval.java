package io.github.yoyama.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GraalEval extends JsEvalUtils
{

    private final Engine engine;
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

    public GraalEval() { this(true); }

    public GraalEval(boolean nashornCompat)
    {
        engine = Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.nashorn-compat", String.valueOf(nashornCompat))
                .option("js.console", String.valueOf(false))
                .option("js.load", "true") //default
                .option("js.syntax-extensions", "false")
                .option("js.ecmascript-version", "5")
                .build();
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

    public String eval(String code, String paramJson)
    {
        Context.Builder contextBuilder = Context.newBuilder()
                .engine(engine)
                .allowAllAccess(false);
        Context context = contextBuilder.build();
        try {
            for (Source lib : libraryJsSources) {
                context.eval(lib);
            }

            try {
                Value result = context.getBindings("js").getMember("template").execute(code, paramJson);
                context = null;
                return result.asString();
            } catch (PolyglotException ex) {
                ex.printStackTrace();
                String message;
                if (ex.getCause() != null) {
                    message = ex.getCause().getMessage();
                } else {
                    message = ex.getMessage();
                }
                System.err.println(message);
                ex.printStackTrace();
                throw new IllegalStateException("Unexpected script evaluation failure", ex);
            }
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }
}

