package com.bigcompany.repository;

import com.bigcompany.model.Employee;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing employee data.
 */
public interface EmployeeRepository {

    /**
     * Finds all employees from the given data source.
     *
     * @param filePath the path to the data source file.
     * @return a list of all employees.
     * @throws IOException if there is an error reading the data source.
     */
    Optional<List<Employee>> findAll(String filePath) throws IOException;
}