package ud.susel.tool;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LauncherTest {
    @Test
    public void validLaunchTest() throws Exception {
        Launcher.main("--module-name", "ud.susel.mock.svc.impl", "--meta-inf-root-path", "build");

        var expected = new Properties();
        expected.setProperty(
                "ud.susel.mock.svc.impl.MyAnotherServiceImpl_Refs",
                "ud.susel.mock.svc:ud.susel.mock.svc.MyService;setMyService;false;false,ud.susel.mock.svc:ud.susel.mock.svc.MyService;setMyMoreServices;true;false");
        expected.setProperty("ud.susel.mock.svc.impl.MyAnotherServiceImpl_Activate", "activateCustom");

        var actual = new Properties();
        actual.load(new FileInputStream(new File("build/META-INF/susel.metadata")));

        assertEquals(expected, actual);
    }
}
