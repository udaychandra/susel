package io.github.udaychandra.susel.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SuselActivateTest {

    @Test
    public void noActivateTest() {
        Susel.reset();
        Exception ex = assertThrows(NullPointerException.class, () -> Susel.get(Susel.class));
        assertEquals("You have to activate Susel before using it", ex.getMessage());
    }
}
