package com.bigcompany.service;

import com.bigcompany.analysis.ReportingLineAnalysisReport;
import com.bigcompany.analysis.SalaryAnalysisReport;
import com.bigcompany.helper.AnalysisHelper;
import com.bigcompany.model.Employee;
import com.bigcompany.model.Hierarchy;
import com.bigcompany.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for handling high-level operations related to employees.
 * <p>
 * This service orchestrates the loading of employee data, the construction of the
 * hierarchy, and the execution of various analyses like salary and reporting line checks.
 * </p>
 */
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final HierarchyService hierarchyService;

    /**
     * Constructs an EmployeeService with the given repository.
     *
     * @param employeeRepository The repository to use for fetching employee data.
     */
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.hierarchyService = new HierarchyService();
    }

    /**
     * Loads employees from a data source and builds the organizational hierarchy.
     *
     * @param filePath The path to the data source file.
     * @return An {@link Optional} containing the constructed {@link Hierarchy}, or an
     *         empty Optional if loading or building fails, or if no employees are found.
     */
    public Optional<Hierarchy> loadAndBuildHierarchy(String filePath) {
        try {
            Optional<List<Employee>> employeesOptional = employeeRepository.findAll(filePath);
            if (employeesOptional.isEmpty() || employeesOptional.get().isEmpty()) {
                logger.warn("No employees found in the data source: {}", filePath);
                return Optional.empty();
            }
            return Optional.of(hierarchyService.buildHierarchy(employeesOptional.get()));
        } catch (IOException | IllegalStateException e) {
            logger.error("Failed to load or build employee hierarchy from {}: {}", filePath, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Analyzes the salaries of all managers in a given collection of employees.
     *
     * @param employees A collection of all employees in the organization.
     * @return A list of {@link SalaryAnalysisReport} for managers with salary discrepancies.
     */
    public List<SalaryAnalysisReport> analyzeManagerSalaries(Collection<Employee> employees) {
        return employees.stream()
                .filter(e -> !e.getSubordinates().isEmpty()) // Optimization: Only analyze managers
                .map(AnalysisHelper::analyzeManagerSalary)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Analyzes the reporting lines for all employees to find those that are too long.
     *
     * @param employees A collection of all employees in the organization.
     * @param maxDepth The maximum allowed depth of a reporting line.
     * @return A list of {@link ReportingLineAnalysisReport} for employees with overly long reporting lines.
     */
    public List<ReportingLineAnalysisReport> analyzeReportingLines(Collection<Employee> employees, int maxDepth) {
        return employees.stream()
                .filter(e -> e.getDepth() > maxDepth) // Optimization: Use pre-calculated depth
                .map(e -> new ReportingLineAnalysisReport(e, e.getDepth()))
                .collect(Collectors.toList());
    }
}