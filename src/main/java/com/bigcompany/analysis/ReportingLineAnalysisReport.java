package com.bigcompany.analysis;

import com.bigcompany.model.Employee;

/**
 * A record to hold the results of the reporting line analysis for a single employee.
 *
 * @param employee The employee being analyzed.
 * @param reportingLineLength The calculated length of the employee's reporting line.
 */
public record ReportingLineAnalysisReport(
        Employee employee,
        int reportingLineLength
) {}