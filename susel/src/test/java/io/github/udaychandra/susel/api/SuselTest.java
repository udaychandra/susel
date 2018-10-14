package io.github.udaychandra.susel.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.github.udaychandra.susel.mock.svc.MyAnotherService;
import io.github.udaychandra.susel.mock.svc.MyService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SuselTest {

    @BeforeAll
    public static void setup() {
        Susel.reset();
        Susel.activate(Map.of("name", "world"));
    }

    @Test
    public void getAllServicesTest() {
        List<MyService> myServices = Susel.getAll(MyService.class);
        assertEquals(2, myServices.size(), "Two service providers should be loaded for MyService");
    }

    @Test
    public void suselLifecycleTest() {
        MyAnotherService anotherService = Susel.get(MyAnotherService.class);
        assertNotNull(anotherService, "At least one service provider should be loaded for MyAnotherService");

        assertEquals("Hello world", anotherService.hello());
    }
}
