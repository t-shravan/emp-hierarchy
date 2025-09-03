package com.bigcompany.helper;

import com.bigcompany.analysis.ReportingLineAnalysisReport;
import com.bigcompany.analysis.SalaryAnalysisReport;
import com.bigcompany.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportGeneratorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private ReportGenerator reportGenerator;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        reportGenerator = new ReportGenerator();
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testPrintSalaryAnalysis() {
        Employee manager = new Employee.Builder().id(1L).firstName("Test").lastName("Manager").build();
        SalaryAnalysisReport underpaid = new SalaryAnalysisReport(manager, BigDecimal.ZERO, new BigDecimal("1000"), true, false);
        SalaryAnalysisReport overpaid = new SalaryAnalysisReport(manager, BigDecimal.ZERO, new BigDecimal("2000"), false, true);

        reportGenerator.printSalaryAnalysis(List.of(underpaid, overpaid));

        String output = outContent.toString();
        assertTrue(output.contains("--- Salary Analysis Report ---"));
        assertTrue(output.contains("Managers Earning Less Than They Should:"));
        assertTrue(output.contains("Discrepancy: -$1000"));
        assertTrue(output.contains("Managers Earning More Than They Should:"));
        assertTrue(output.contains("Discrepancy: +$2000"));
    }

    @Test
    void testPrintReportingLineAnalysis() {
        Employee employee = new Employee.Builder().id(1L).firstName("Test").lastName("Employee").build();
        ReportingLineAnalysisReport report = new ReportingLineAnalysisReport(employee, 5);

        reportGenerator.printReportingLineAnalysis(List.of(report), 4);
        
        String output = outContent.toString();
        assertTrue(output.contains("--- Reporting Line Analysis Report ---"));
        assertTrue(output.contains("Employees with a Reporting Line Longer Than 4:"));
        assertTrue(output.contains("Reporting Line Length: 5"));
    }
}