package com.bigcompany.service;

import com.bigcompany.model.Employee;
import com.bigcompany.model.Hierarchy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HierarchyServiceTest {

    private HierarchyService hierarchyService;
    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        hierarchyService = new HierarchyService();
        employees = new ArrayList<>();

        // CEO
        Employee ceo = new Employee.Builder().id(1L).firstName("John").lastName("CEO").salary(new BigDecimal("200000")).build();
        employees.add(ceo);
        // Manager
        Employee manager = new Employee.Builder().id(2L).firstName("Manager").lastName("One").salary(new BigDecimal("100000")).managerId(1L).build();
        employees.add(manager);
        // Subordinate
        Employee subordinate = new Employee.Builder().id(3L).firstName("Sub").lastName("One").salary(new BigDecimal("50000")).managerId(2L).build();
        employees.add(subordinate);
    }

    @Test
    void testBuildHierarchy_Success() {
        Hierarchy hierarchy = hierarchyService.buildHierarchy(employees);

        assertNotNull(hierarchy);
        assertNotNull(hierarchy.getCeo());
        assertEquals(1L, hierarchy.getCeo().getId());
        assertEquals(3, hierarchy.getEmployeeMap().size());

        Employee ceo = hierarchy.getCeo();
        Employee manager = hierarchy.getEmployeeMap().get(2L);
        Employee subordinate = hierarchy.getEmployeeMap().get(3L);

        assertEquals(0, ceo.getDepth());
        assertEquals(1, manager.getDepth());
        assertEquals(2, subordinate.getDepth());

        assertEquals(1, ceo.getSubordinates().size());
        assertTrue(ceo.getSubordinates().contains(manager));
        assertEquals(ceo, manager.getManager());
    }

    @Test
    void testBuildHierarchy_UnorderedList() {
        Collections.shuffle(employees);
        Hierarchy hierarchy = hierarchyService.buildHierarchy(employees);

        assertNotNull(hierarchy);
        assertNotNull(hierarchy.getCeo());
        assertEquals(1L, hierarchy.getCeo().getId());
        assertEquals(3, hierarchy.getEmployeeMap().size());
    }

    @Test
    void testBuildHierarchy_NoCeoFound() {
        // Recreate employees where everyone has a managerId
        List<Employee> noCeoList = new ArrayList<>();
        Employee e1 = new Employee.Builder().id(1L).managerId(2L).build();
        Employee e2 = new Employee.Builder().id(2L).managerId(1L).build(); // Circular dependency
        noCeoList.add(e1);
        noCeoList.add(e2);
        
        assertThrows(IllegalStateException.class, () -> hierarchyService.buildHierarchy(noCeoList));
    }

    @Test
    void testBuildHierarchy_EmptyList() {
        assertThrows(IllegalStateException.class, () -> hierarchyService.buildHierarchy(new ArrayList<>()));
    }
}