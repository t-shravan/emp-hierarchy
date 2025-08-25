package com.bigcompany.repository;

import com.bigcompany.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CsvEmployeeRepositoryTest {

    private EmployeeRepository repository;
    private String validCsvPath;
    private String malformedCsvPath;

    @BeforeEach
    void setUp() throws URISyntaxException {
        repository = new EmployeeRepositoryImpl();
        validCsvPath = Paths.get(getClass().getClassLoader().getResource("employees.csv").toURI()).toString();
        malformedCsvPath = Paths.get(getClass().getClassLoader().getResource("test-employees.csv").toURI()).toString();
    }

    @Test
    void testFindAll_Success() throws IOException {
        Optional<List<Employee>> employeesOptional = repository.findAll(validCsvPath);
        assertTrue(employeesOptional.isPresent());
        assertEquals(34, employeesOptional.get().size());
    }

    @Test
    void testFindAll_FileNotFound() {
        assertThrows(IOException.class, () -> repository.findAll("nonexistent.csv"));
    }

    @Test
    void testFindAll_MalformedRow() throws IOException {
        Optional<List<Employee>> employeesOptional = repository.findAll(malformedCsvPath);
        assertTrue(employeesOptional.isPresent());
        // Should load 3 valid employees and skip the bad row
        assertEquals(3, employeesOptional.get().size());
    }
}