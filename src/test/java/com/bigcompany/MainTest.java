package com.bigcompany;

import org.junit.jupiter.api.Test;
import java.nio.file.Paths;

public class MainTest {

    @Test
    public void testMain_NoArgs() {
        // This test simply ensures that running main with no arguments does not throw an exception.
        // It covers the initial argument check.
        Main.main(new String[0]);
    }

    @Test
    public void testMain_FileNotFound() {
        // This test ensures that running main with a non-existent file
        // is handled gracefully without an exception. It covers the empty optional path.
        String nonExistentFilePath = "non-existent-file.csv";
        Main.main(new String[]{nonExistentFilePath});
    }

    @Test
    public void testMain_Success() {
        // This test runs the main method through its successful execution path
        // to ensure no exceptions are thrown and to get coverage on the main logic.
        String filePath = Paths.get("src", "main", "resources", "employees.csv").toFile().getAbsolutePath();
        Main.main(new String[]{filePath});
    }

    @Test
    public void testMain_Constructor() {
        // This test is to get coverage on the private constructor for utility classes.
        new Main();
    }
}