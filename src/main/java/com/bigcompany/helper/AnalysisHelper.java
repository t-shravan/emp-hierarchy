package com.bigcompany.helper;

import com.bigcompany.analysis.SalaryAnalysisReport;
import com.bigcompany.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * A helper class that provides static methods for performing analysis on employee data.
 * <p>
 * This class contains business logic for identifying salary discrepancies.
 * </p>
 */
public class AnalysisHelper {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisHelper.class);
    private static final BigDecimal SALARY_LOWER_BOUND_MULTIPLIER = new BigDecimal("1.20");
    private static final BigDecimal SALARY_UPPER_BOUND_MULTIPLIER = new BigDecimal("1.50");

    /**
     * Analyzes a single manager's salary against the average salary of their direct subordinates.
     * <p>
     * A manager is considered underpaid if their salary is less than 120% of their subordinates' average salary.
     * A manager is considered overpaid if their salary is more than 150% of their subordinates' average salary.
     * </p>
     *
     * @param manager The manager employee to analyze. Must not be null.
     * @return An {@link Optional} containing a {@link SalaryAnalysisReport} if a discrepancy is found,
     *         otherwise an empty Optional. Returns an empty Optional if the manager has no subordinates.
     */
    public static Optional<SalaryAnalysisReport> analyzeManagerSalary(Employee manager) {
        if (manager.getSubordinates().isEmpty()) {
            return Optional.empty();
        }

        BigDecimal averageSubordinateSalary = manager.getSubordinates().stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(manager.getSubordinates().size()), 2, RoundingMode.HALF_UP);

        BigDecimal lowerBound = averageSubordinateSalary.multiply(SALARY_LOWER_BOUND_MULTIPLIER);
        BigDecimal upperBound = averageSubordinateSalary.multiply(SALARY_UPPER_BOUND_MULTIPLIER);
        BigDecimal managerSalary = manager.getSalary();

        boolean isUnderpaid = managerSalary.compareTo(lowerBound) < 0;
        boolean isOverpaid = managerSalary.compareTo(upperBound) > 0;

        logger.debug("Manager {}: Salary={}, AvgSubSalary={}, LowerBound={}, UpperBound={}, Overpaid={}",
                manager.getId(), managerSalary, averageSubordinateSalary, lowerBound, upperBound, isOverpaid);

        if (isUnderpaid) {
            BigDecimal difference = lowerBound.subtract(managerSalary);
            return Optional.of(new SalaryAnalysisReport(manager, averageSubordinateSalary, difference, true, false));
        } else if (isOverpaid) {
            BigDecimal difference = managerSalary.subtract(upperBound);
            return Optional.of(new SalaryAnalysisReport(manager, averageSubordinateSalary, difference, false, true));
        }

        return Optional.empty();
    }

}