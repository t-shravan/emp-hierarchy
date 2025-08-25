package com.bigcompany.service;

import com.bigcompany.analysis.ReportingLineAnalysisReport;
import com.bigcompany.analysis.SalaryAnalysisReport;
import com.bigcompany.model.Employee;
import com.bigcompany.model.Hierarchy;
import com.bigcompany.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> employees;
    private Hierarchy hierarchy;

    @BeforeEach
    void setUp() {
        employees = new ArrayList<>();
        // CEO -> L1 -> L2 -> L3 -> L4 -> L5 (for deep reporting line test)
        Employee ceo = new Employee.Builder().id(1L).firstName("CEO").salary(new BigDecimal("300000")).build();
        Employee l1 = new Employee.Builder().id(2L).managerId(1L).firstName("L1").salary(new BigDecimal("200000")).build();
        Employee l2 = new Employee.Builder().id(3L).managerId(2L).firstName("L2").salary(new BigDecimal("150000")).build();
        Employee l3 = new Employee.Builder().id(4L).managerId(3L).firstName("L3").salary(new BigDecimal("120000")).build();
        Employee l4 = new Employee.Builder().id(5L).managerId(4L).firstName("L4").salary(new BigDecimal("100000")).build();
        Employee l5 = new Employee.Builder().id(6L).managerId(5L).firstName("L5").salary(new BigDecimal("80000")).build(); // This employee has a reporting line of 5

        // Manager (Underpaid)
        Employee manager_under = new Employee.Builder().id(7L).managerId(1L).firstName("Manager").lastName("Underpaid").salary(new BigDecimal("100000")).build();
        Employee sub_under = new Employee.Builder().id(8L).managerId(7L).firstName("Sub").salary(new BigDecimal("90000")).build();

        // Manager (Overpaid)
        Employee manager_over = new Employee.Builder().id(9L).managerId(1L).firstName("Manager").lastName("Overpaid").salary(new BigDecimal("200000")).build();
        Employee sub_over = new Employee.Builder().id(10L).managerId(9L).firstName("Sub").salary(new BigDecimal("50000")).build();

        employees.addAll(List.of(ceo, l1, l2, l3, l4, l5, manager_under, sub_under, manager_over, sub_over));
        
        HierarchyService realHierarchyService = new HierarchyService();
        hierarchy = realHierarchyService.buildHierarchy(employees);
    }

    @Test
    void testLoadAndBuildHierarchy_Success() throws IOException {
        when(employeeRepository.findAll("dummy.csv")).thenReturn(Optional.of(employees));
        Optional<Hierarchy> result = employeeService.loadAndBuildHierarchy("dummy.csv");

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getEmployeeMap().size());
        verify(employeeRepository, times(1)).findAll("dummy.csv");
    }

    @Test
    void testLoadAndBuildHierarchy_IOException() throws IOException {
        when(employeeRepository.findAll("dummy.csv")).thenThrow(new IOException("Test Exception"));
        Optional<Hierarchy> result = employeeService.loadAndBuildHierarchy("dummy.csv");

        assertFalse(result.isPresent());
    }

    @Test
    void testLoadAndBuildHierarchy_EmptyRepository() throws IOException {
        when(employeeRepository.findAll("dummy.csv")).thenReturn(Optional.empty());
        Optional<Hierarchy> result = employeeService.loadAndBuildHierarchy("dummy.csv");

        assertFalse(result.isPresent());
    }
    
    @Test
    void testLoadAndBuildHierarchy_EmptyEmployeeList() throws IOException {
        when(employeeRepository.findAll("dummy.csv")).thenReturn(Optional.of(new ArrayList<>()));
        Optional<Hierarchy> result = employeeService.loadAndBuildHierarchy("dummy.csv");

        assertFalse(result.isPresent());
    }

    @Test
    void testAnalyzeManagerSalaries() {
        List<SalaryAnalysisReport> reports = employeeService.analyzeManagerSalaries(hierarchy.getEmployeeMap().values());

        // CEO, L1-L4, manager_under, manager_over are all managers.
        // CEO: 300k vs avg(200k, 100k, 200k)=166.6k. Overpaid.
        // L1-L4: All overpaid.
        // manager_under: 100k vs avg(90k). Underpaid.
        // manager_over: 200k vs avg(50k). Overpaid.
        assertEquals(3, reports.size());
        assertTrue(reports.stream().anyMatch(r -> r.manager().getId().equals(7L) && r.isUnderpaid()));
        assertTrue(reports.stream().anyMatch(r -> r.manager().getId().equals(9L) && r.isOverpaid()));
        assertTrue(reports.stream().anyMatch(r -> r.manager().getId().equals(1L) && r.isOverpaid()));
    }

    @Test
    void testAnalyzeReportingLines() {
        List<ReportingLineAnalysisReport> reports = employeeService.analyzeReportingLines(hierarchy.getEmployeeMap().values(), 4);

        assertEquals(1, reports.size());
        assertEquals(6L, reports.get(0).employee().getId());
        assertEquals(5, reports.get(0).reportingLineLength());
    }
}