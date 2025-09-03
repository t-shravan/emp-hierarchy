package com.bigcompany.analysis;

import com.bigcompany.model.Employee;

import java.math.BigDecimal;

/**
 * A record to hold the results of the salary analysis for a single manager.
 *
 * @param manager The manager whose salary is being analyzed.
 * @param subordinateAverageSalary The average salary of the manager's direct subordinates.
 * @param difference The amount by which the manager's salary is over or under the threshold.
 * @param isUnderpaid True if the manager earns less than the 20% lower bound.
 * @param isOverpaid True if the manager earns more than the 50% upper bound.
 */
public record SalaryAnalysisReport(
        Employee manager,
        BigDecimal subordinateAverageSalary,
        BigDecimal difference,
        boolean isUnderpaid,
        boolean isOverpaid
) {}