package com.bigcompany.helper;

import com.bigcompany.analysis.SalaryAnalysisReport;
import com.bigcompany.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisHelperTest {

    private Employee manager;
    private Employee subordinate1;
    private Employee subordinate2;

    @BeforeEach
    void setUp() {
        manager = new Employee.Builder().id(1L).firstName("Manager").lastName("Test").build();
        subordinate1 = new Employee.Builder().id(2L).firstName("Sub").lastName("One").salary(new BigDecimal("80000")).build();
        subordinate2 = new Employee.Builder().id(3L).firstName("Sub").lastName("Two").salary(new BigDecimal("120000")).build();
        manager.addSubordinate(subordinate1);
        manager.addSubordinate(subordinate2);
        // Average subordinate salary is 100,000
    }

    @Test
    void testAnalyzeManagerSalary_Underpaid() {
        manager = new Employee.Builder().id(1L).salary(new BigDecimal("110000")).build(); // Lower bound is 120,000
        manager.addSubordinate(subordinate1);
        manager.addSubordinate(subordinate2);

        Optional<SalaryAnalysisReport> report = AnalysisHelper.analyzeManagerSalary(manager);

        assertTrue(report.isPresent());
        assertTrue(report.get().isUnderpaid());
        assertFalse(report.get().isOverpaid());
        assertEquals(0, new BigDecimal("10000.00").compareTo(report.get().difference()));
    }

    @Test
    void testAnalyzeManagerSalary_Overpaid() {
        manager = new Employee.Builder().id(1L).salary(new BigDecimal("160000")).build(); // Upper bound is 150,000
        manager.addSubordinate(subordinate1);
        manager.addSubordinate(subordinate2);

        Optional<SalaryAnalysisReport> report = AnalysisHelper.analyzeManagerSalary(manager);

        assertTrue(report.isPresent());
        assertFalse(report.get().isUnderpaid());
        assertTrue(report.get().isOverpaid());
        assertEquals(0, new BigDecimal("10000.00").compareTo(report.get().difference()));
    }

    @Test
    void testAnalyzeManagerSalary_CorrectlyPaid() {
        manager = new Employee.Builder().id(1L).salary(new BigDecimal("130000")).build(); // Between 120k and 150k
        manager.addSubordinate(subordinate1);
        manager.addSubordinate(subordinate2);

        Optional<SalaryAnalysisReport> report = AnalysisHelper.analyzeManagerSalary(manager);

        assertFalse(report.isPresent());
    }

    @Test
    void testAnalyzeManagerSalary_NotAManager() {
        Employee nonManager = new Employee.Builder().id(4L).salary(new BigDecimal("100000")).build();
        Optional<SalaryAnalysisReport> report = AnalysisHelper.analyzeManagerSalary(nonManager);
        assertFalse(report.isPresent());
    }
}