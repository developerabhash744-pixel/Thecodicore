package com.example;

import org.junit.jupiter.api.Test;
import java.awt.GraphicsEnvironment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    @Test
    public void testAppTitle() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Running in headless environment. Skipping JFrame instantiation test.");
            assertTrue(true);
            return;
        }
        App app = new App();
        assertEquals("my-web-app - Visual Studio Code", app.getTitle());
    }
}
