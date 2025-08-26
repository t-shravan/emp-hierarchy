package com.bigcompany.helper;

import com.bigcompany.analysis.ReportingLineAnalysisReport;
import com.bigcompany.analysis.SalaryAnalysisReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A helper class responsible for generating and printing analysis reports to the console via logging.
 * <p>
 * This class formats the results of different analyses into a human-readable format.
 * </p>
 */
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    /**
     * Prints the salary analysis report.
     * <p>
     * This method logs managers who are identified as being underpaid or overpaid based on the provided reports.
     * If no discrepancies are found, it logs an informational message.
     * </p>
     *
     * @param reports A list of {@link SalaryAnalysisReport} objects to be printed.
     */
    public void printSalaryAnalysis(List<SalaryAnalysisReport> reports) {
        logger.info("\n--- Salary Analysis Report ---");

        List<SalaryAnalysisReport> underpaid = reports.stream().filter(SalaryAnalysisReport::isUnderpaid).toList();
        List<SalaryAnalysisReport> overpaid = reports.stream().filter(SalaryAnalysisReport::isOverpaid).toList();

        if (underpaid.isEmpty()) {
            logger.info("No managers found who earn less than they should.");
        } else {
            logger.warn("Managers Earning Less Than They Should:");
            underpaid.forEach(report -> logger.warn(
                    "  - Manager ID: {}, Name: {} {}, Discrepancy: -${}",
                    report.manager().getId(),
                    report.manager().getFirstName(),
                    report.manager().getLastName(),
                    report.difference()
            ));
        }

        if (overpaid.isEmpty()) {
            logger.info("No managers found who earn more than they should.");
        } else {
            logger.warn("Managers Earning More Than They Should:");
            overpaid.forEach(report -> logger.warn(
                    "  - Manager ID: {}, Name: {} {}, Discrepancy: +${}",
                    report.manager().getId(),
                    report.manager().getFirstName(),
                    report.manager().getLastName(),
                    report.difference()
            ));
        }
    }

    /**
     * Prints the reporting line analysis report.
     * <p>
     * This method logs employees whose reporting line exceeds the specified maximum depth.
     * If no such employees are found, it logs an informational message.
     * </p>
     *
     * @param reports A list of {@link ReportingLineAnalysisReport} objects.
     * @param maxDepth The maximum allowed depth for a reporting line.
     */
    public void printReportingLineAnalysis(List<ReportingLineAnalysisReport> reports, int maxDepth) {
        logger.info("\n--- Reporting Line Analysis Report ---");

        if (reports.isEmpty()) {
            logger.info("No employees found with an excessive reporting line.");
        } else {
            logger.warn("Employees with a Reporting Line Longer Than {}:", maxDepth);
            reports.forEach(report -> logger.warn(
                    "  - Employee ID: {}, Name: {} {}, Reporting Line Length: {}",
                    report.employee().getId(),
                    report.employee().getFirstName(),
                    report.employee().getLastName(),
                    report.reportingLineLength()
            ));
        }
    }
}