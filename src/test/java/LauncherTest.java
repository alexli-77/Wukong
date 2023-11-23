import org.junit.Test;
import spoon.Launcher;
import spoon.MavenLauncher;

import java.util.HashSet;
import java.util.Set;

public class LauncherTest {
    @Test
    public void testLauncher() {
        //测试Launcher
//        MavenLauncher launcher = new MavenLauncher("PATH/java_collect-1.0-SNAPSHOT.jar", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
//        launcher.getEnvironment().setAutoImports(true);
//        launcher.buildModel();
    }

    @Test
    public void testFilter() {
        Set<String> whitelist = new HashSet<>();
        whitelist.add("java.lang");
        String str = "java.lang.String";
        whitelist.removeIf(str::contains);
        System.out.println(whitelist.isEmpty());
    }
}
