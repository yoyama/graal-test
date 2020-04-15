package io.github.yoyama.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Re-produce GraalJS issue 'engine is already closed' while shutdown.
 * Run this app and kill the jvm process or ctrl-c. This error will be happened.
 * The cause may be GraalJS has its own shutdown hook and engine will be closed before app shutdown gracefully
 *
 * How to test:
 *  - ./gradlew shadowJar
 *  - java -cp build/libs/graal-test.jar io.github.yoyama.graal.GraalShutdownHookTest
 *  - wait for around 10 secs
 *  - Ctrl-c
 *  - Show "java.lang.IllegalStateException: Engine is already closed"
 */
public class GraalShutdownHookTest {
    private static final String[][] LIBRARY_JS_RESOURCES = {
            new String[]{"digdag.js", "/io/digdag/core/agent/digdag.js"},
            new String[]{"moment.min.js", "/io/digdag/core/agent/moment.min.js"},
    };

    private static Engine engine = createEngine();

    private static Engine createEngine() {
        return Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("js.nashorn-compat", "true")
                .option("js.console", "false")
                .option("js.load", "true") //default
                .option("js.load-from-url", "false") //default
                .option("js.syntax-extensions", "false")
                .option("js.ecmascript-version", "5")
                .build();
    }

    private static final HostAccess hostAccess = HostAccess.newBuilder()
            .allowPublicAccess(true)
            .build();

    private static Context createContext(Engine jsEngine) throws IOException {
        Context.Builder contextBuilder = Context.newBuilder()
                .engine(jsEngine)
                .allowAllAccess(false)
                .allowHostAccess(hostAccess)
                .allowHostClassLookup(className -> {
                    if (className.matches("java\\.lang\\.String")) {
                        return true;
                    } else {
                        return false;
                    }
                });
        Context context = contextBuilder.build();
        for (int i = 0; i < LIBRARY_JS_RESOURCES.length; i++) {
            Source source = Source.newBuilder("js", JsEvalUtils.readResource(LIBRARY_JS_RESOURCES[i][1]), LIBRARY_JS_RESOURCES[i][0]).build();
            context.eval(source);
        }
        return context;
    }

    private static Callable<String> createCallable() {
        return () -> {
            System.out.println("Start eval");
            try (Context context = createContext(engine)) {
                Thread.sleep(1000);
                Value result = context.getBindings("js").getMember("template").execute("echo>: ${i=999} ${i*2}", "{}");
                System.out.println("End eval. result:" + result.asString());
                System.out.flush();
                Thread.sleep(1000);
                return result.asString();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
            return "";

        };
    }

    private static final AtomicBoolean doShutdown = new AtomicBoolean(false);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("Shutdown hook started");
                    Thread.sleep(30000);
                    doShutdown.set(true);
                    System.out.println("Shutdown hook finished");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        ExecutorService executor = Executors.newFixedThreadPool(10);
        while (!doShutdown.get()) {
            try {
                executor.submit(createCallable());
                System.out.println("submit");
                Thread.sleep(100);
            } catch (InterruptedException ie) {

            }
        }
    }
}
