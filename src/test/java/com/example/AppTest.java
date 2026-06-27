package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    @Test
    public void testAdd() {
        App app = new App();
        assertEquals(12, app.add(5, 7));
        assertEquals(-2, app.add(3, -5));
        assertEquals(0, app.add(0, 0));
    }
}
