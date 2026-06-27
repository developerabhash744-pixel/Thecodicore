package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    @Test
    public void testAppTitle() {
        App app = new App();
        assertEquals("my-web-app - Visual Studio Code", app.getTitle());
    }
}
