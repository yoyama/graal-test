package io.github.yoyama.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class GraalEval extends JsEvalUtils
{
    private static Logger logger = LoggerFactory.getLogger(GraalEval.class);

    private final Engine engine;
    private final boolean nashornCompat;
    private final boolean separatedEngine;
    public final Source[] libraryJsSources;

    private static final String[][] LIBRARY_JS_RESOURCES = {
            new String[] { "digdag.js", "/io/digdag/core/agent/digdag.js" },
            new String[] { "moment.min.js", "/io/digdag/core/agent/moment.min.js" },
    };

    private static final HostAccess hostAccess = HostAccess.newBuilder()
            .allowPublicAccess(true)
            .build();


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

    public GraalEval() { this(true, false); }

    public GraalEval(boolean nashornCompat, boolean separatedEngine)
    {
        this.nashornCompat = nashornCompat;
        this.separatedEngine = separatedEngine;
        engine = createEngine(this.nashornCompat);
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

    private static Engine createEngine(boolean nashornCompat)
    {
        return Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.nashorn-compat", String.valueOf(nashornCompat))
                .option("js.console", String.valueOf(false))
                .option("js.load", "true") //default
                .option("js.load-from-url", "false") //default
                .option("js.syntax-extensions", "false")
                .option("js.ecmascript-version", "5")
                .build();
    }

    public String eval(String code, String paramJson)
    {
        Engine jsEngine = separatedEngine? createEngine(nashornCompat): engine;
        //engine.close();
        Context.Builder contextBuilder = Context.newBuilder()
                .engine(jsEngine)
                .allowAllAccess(false)
                .allowHostAccess(hostAccess)
                .allowHostClassLookup(className -> {
                    if (className.matches("java\\.lang\\.String")) {
                        return true;
                    }
                    else {
                        return false;
                    }
                })
                //.allowIO(true) //required for load from url
                ;
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

