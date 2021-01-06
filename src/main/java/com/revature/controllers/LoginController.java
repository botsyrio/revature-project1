package com.revature.controllers;

import com.revature.daos.EmployeeDao;
import com.revature.daos.ManagerDao;
import com.revature.models.Employee;
import com.revature.models.Manager;
import io.javalin.http.Handler;

import java.util.List;

/**
 * holds handles that deal with login/authentication management
 */
public class LoginController {

    /**
     * handles a login request for an employee - if an email exists with the given email and password, sets them as the
     * current user, if not keeps them out
     */
    public static Handler handleEmployeeLoginPost = ctx -> {
        EmployeeDao emDao = new EmployeeDao();
        System.out.println("email: "+ctx.formParam("email"));
        System.out.println("password: "+ctx.formParam("password"));
        List<Employee> employeeList = emDao.getEmployeeByEmailPassword(ctx.formParam("email"),
                ctx.formParam("password"));

        if (employeeList.size() == 0) {
            ctx.redirect("/employeeLoginFail.html");
        } else {
            ctx.sessionAttribute("currentUser", employeeList.get(0));
            ctx.redirect("/employee/homepage.html");
        }
    };

    /**
     * handles a login request for a manager
     */
    public static Handler handleManagerLoginPost = ctx -> {
        ManagerDao manDao = new ManagerDao();
        List<Manager> managerList = manDao.getManagerByEmailPassword(ctx.formParam("email"),
                ctx.formParam("password"));

        if (managerList.size() == 0) {
            ctx.redirect("/managerLoginFail.html");
        } else {
            ctx.sessionAttribute("currentUser", managerList.get(0));
            ctx.redirect("/manager/homepage.html");
        }
    };

    /**
     * handles a logout request - removes the current user for the session and redirects them to the index
     */
    public static Handler handleLogoutGet = ctx->{
        ctx.sessionAttribute("currentUser", null);
        ctx.redirect("/");
    };

    /**
     * route guarder for all pages in the /employee subdomain
     */
    public static Handler ensureLoginBeforeViewingEmployee = ctx -> {

        if (!ctx.path().startsWith("/employee/")) {
            return;
        }
        if (ctx.sessionAttribute("currentUser") == null || !(ctx.sessionAttribute("currentUser") instanceof Employee)) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect("/employeeLogin.html");
        }

    };

    /**
     * route guarder for all pages in the /manager subdomain
     */
    public static Handler ensureLoginBeforeViewingManager = ctx -> {
        if (!ctx.path().startsWith("/manager/")) {
            return;
        }
        if (ctx.sessionAttribute("currentUser") == null || !(ctx.sessionAttribute("currentUser") instanceof Manager)) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect("/managerLogin.html");
        }

    };

}
