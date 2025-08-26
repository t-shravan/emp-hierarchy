package com.bigcompany;

import com.bigcompany.helper.ReportGenerator;
import com.bigcompany.model.Employee;
import com.bigcompany.model.Hierarchy;
import com.bigcompany.repository.EmployeeRepositoryImpl;
import com.bigcompany.repository.EmployeeRepository;
import com.bigcompany.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

/**
 * The main entry point for the employee hierarchy analysis application.
 * <p>
 * This class is responsible for parsing command-line arguments, initializing services,
 * and orchestrating the analysis process. It expects a single command-line argument:
 * the path to the employees CSV file.
 * </p>
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int MAX_REPORTING_LINE_DEPTH = 4;

    /**
     * The main method that runs the employee analysis.
     *
     * @param args Command-line arguments. Expects one argument: the file path to the CSV.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("Please provide the path to the employees CSV file as an argument.");
            return;
        }

        String filePath = args[0];
        logger.info("Starting employee analysis for file: {}", filePath);

        EmployeeRepository employeeRepository = new EmployeeRepositoryImpl();
        EmployeeService employeeService = new EmployeeService(employeeRepository);
        ReportGenerator reportGenerator = new ReportGenerator();

        Optional<Hierarchy> hierarchyOptional = employeeService.loadAndBuildHierarchy(filePath);

        if (hierarchyOptional.isEmpty()) {
            logger.error("Failed to load or process employee data. Exiting.");
            return;
        }

        Hierarchy hierarchy = hierarchyOptional.get();

        Collection<Employee> employees = hierarchy.getEmployeeMap().values();

        // Perform and print analyses
        reportGenerator.printSalaryAnalysis(employeeService.analyzeManagerSalaries(employees));
        reportGenerator.printReportingLineAnalysis(
                employeeService.analyzeReportingLines(employees, MAX_REPORTING_LINE_DEPTH),
                MAX_REPORTING_LINE_DEPTH
        );

        logger.info("Employee analysis finished.");
    }
}