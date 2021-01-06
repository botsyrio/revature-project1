package com.revature.controllers;

import com.revature.daos.EmployeeDao;
import com.revature.models.Employee;
import io.javalin.Javalin;
import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.mail.EmailException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)

/**
 * tests for the EmployeeController class
 */
public class EmployeeControllerTest {

    /**
     * checks that EmployeeController.handleUpdateProfilePost calls updateEmployee with the employee in the current user
     * session
     * @throws Exception possible mocking exception
     */
    @Test
    public void handleUpdateProfilePostCallsUpdateWithRightParam() throws Exception {
        String testEmail = "test@example.com";
        String testFirstName = "Bob";
        String testLastName = "Grove";
        String testJobTitle = null;
        String testBirthday = null;
        String testAboutMe = null;

        Employee testCurrentUser = new Employee();
        testCurrentUser.setPassword("password");

        Context ctx = mock(Context.class);

        when(ctx.sessionAttribute("currentUser")).thenReturn(testCurrentUser);
        when(ctx.formParam("email")).thenReturn(testEmail);
        when(ctx.formParam("firstName")).thenReturn(testFirstName);
        when(ctx.formParam("lastName")).thenReturn(testLastName);
        when(ctx.formParam("jobTitle")).thenReturn(testJobTitle);
        when(ctx.formParam("birthday")).thenReturn(testBirthday);
        when(ctx.formParam("aboutMe")).thenReturn(testAboutMe);

        testCurrentUser.setEmail(testEmail);
        testCurrentUser.setFirstName(testFirstName);
        testCurrentUser.setLastName(testLastName);

        EmployeeDao employeeDao = mock(EmployeeDao.class);
        EmployeeController.setEmpDao(employeeDao);

        List<Employee> mockList = new ArrayList<>();
        mockList.add(testCurrentUser);
        when(employeeDao.getEmployeeByEmailPassword(anyString(), anyString())).thenReturn(mockList);

        EmployeeController.handleUpdateProfilePost.handle(ctx);

        verify(employeeDao, times(1)).updateEmployee(testCurrentUser);
    }

    /**
     * tests that EmployeeController.handleEmployeePasswordResetPost sets the html to the correct value if given an
     * email that violates email format
     * @throws Exception
     */
    @Test
    public void handleEmployeePasswordResetPostRedirectsOnInvalidEmail() throws Exception {
        Employee employeeCtx = new Employee();
        employeeCtx.setEmail("test1");

        Context mockCtx = mock(Context.class);
        when(mockCtx.formParam("email")).thenReturn("");

        EmployeeDao mockEmpDao = mock(EmployeeDao.class);
        when(mockEmpDao.getEmployeeByEmail(anyString())).thenReturn(employeeCtx);
        System.out.println(mockEmpDao.getEmployeeByEmail(""));
        EmployeeController.setEmpDao(mockEmpDao);

        EmployeeController.handleEmployeePasswordResetPost.handle(mockCtx);
        verify(mockCtx).html("email in system but not valid email");
    }

    /**
     * tests that sendRegisteredEmail throws an exception if the email violates email format
     * @throws EmailException
     */
    @Test(expected = EmailException.class)
    public void sendRegisteredEmailThrowsExceptionOnInvalidEmail() throws EmailException {
        Employee employeeCtx = new Employee();
        employeeCtx.setEmail("test1");
        employeeCtx.setLastName("Mr");
        employeeCtx.setLastName("Test");

        EmployeeController.sendRegisteredEmail(employeeCtx);

    }


}
