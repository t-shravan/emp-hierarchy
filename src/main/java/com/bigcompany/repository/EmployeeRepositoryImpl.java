package com.bigcompany.repository;

import com.bigcompany.model.Employee;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An implementation of {@link EmployeeRepository} that reads employee data from a CSV file.
 * <p>
 * This repository handles parsing of a CSV file with a specific format:
 * Id,firstName,lastName,salary,managerId
 * It skips the header row and is resilient to malformed rows, which it logs as errors.
 * </p>
 */
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);

    /**
     * {@inheritDoc}
     * <p>
     * This implementation reads from a CSV file. It expects the file to have a header,
     * which will be skipped.
     * </p>
     * @throws IOException if the file is not found or another I/O error occurs.
     */
    @Override
    public Optional<List<Employee>> findAll(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip header
            reader.readNext();

            String[] line;
            while ((line = reader.readNext()) != null) {
                try {
                    Employee employee = parseEmployee(line);
                    employees.add(employee);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    logger.error("Skipping malformed row: {}", String.join(",", line), e);
                }
            }
        } catch (CsvValidationException e) {
            logger.error("CSV validation error while reading file: {}", filePath, e);
            throw new IOException("Error reading CSV file", e);
        }
        logger.info("Successfully loaded {} employees from {}", employees.size(), filePath);
        return Optional.of(employees);
    }

    /**
     * Parses a single line from the CSV file into an {@link Employee} object.
     *
     * @param line An array of strings representing a row in the CSV.
     * @return The parsed {@link Employee}.
     * @throws NumberFormatException if a numeric field (ID, salary, managerId) is not a valid number.
     * @throws ArrayIndexOutOfBoundsException if the row does not have enough columns.
     */
    private Employee parseEmployee(String[] line) {
        Long id = Long.parseLong(line[0].trim());
        String firstName = line[1].trim();
        String lastName = line[2].trim();
        BigDecimal salary = new BigDecimal(line[3].trim());
        Long managerId = (line.length > 4 && line[4] != null && !line[4].trim().isEmpty())
                ? Long.parseLong(line[4].trim()) : null;

        return new Employee.Builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .salary(salary)
                .managerId(managerId)
                .build();
    }
}