package ud.susel.tool;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ud.susel.mock.svc.MyService;
import ud.susel.mock.svc.impl.MyServiceImpl2;
import ud.susel.tool.impl.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ScannerTest {
    private static Module testModule;

    @BeforeAll
    public static void setup() {
        testModule = MyServiceImpl2.class.getModule();
    }

    @Test
    public void scanTest() throws Exception {
        Scanner scanner = new Scanner();
        var metadata = scanner.scan(testModule);

        assertEquals(3, metadata.items().size(),
                "Expected providers not found in the 'ud.susel.mock.svc.impl' module");

        var svcMetadata = metadata.items().stream()
                .filter(item -> "ud.susel.mock.svc.impl.MyAnotherServiceImpl".equals(item.providerName()))
                .findFirst();

        if (svcMetadata.isPresent()) {
            assertEquals("activateCustom", svcMetadata.get().activateMethodName());
            assertEquals(1, svcMetadata.get().references().size(),
                    "References count is not as expected");

            var ref = svcMetadata.get().references().get(0);
            assertEquals("setMyService", ref.setterMethodName(),
                    "MyService reference setter name is not as expected");
            assertEquals(MyService.class, ref.serviceClass(),
                    "MyService reference class is not as expected");
            assertFalse(ref.isList(), "MyService reference should not be a list");
            assertFalse(ref.isOptional(), "MyService reference should not be optional");

        } else {
            throw new Exception("Service provider 'ud.susel.mock.svc.impl.MyAnotherServiceImpl' not found");
        }
    }
}
