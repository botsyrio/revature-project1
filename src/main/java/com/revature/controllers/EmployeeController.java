package com.revature.controllers;

import com.revature.daos.EmployeeDao;
import com.revature.models.Employee;
import com.revature.models.ReimbursementReq;
import io.javalin.http.Handler;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

/**
 * holds all the handlers responsible for interfacing with employee objects
 */
public class EmployeeController {
    /**
     * log4j logger that logs every change to employee objects
     */
    private static final Logger logger = LogManager.getLogger(EmployeeController.class);
    /**
     * the employeeDao responsible for interfacing with the database
     */
    private static EmployeeDao empDao = new EmployeeDao();

    /**
     * setter for empDao
     * @param empDao the new value for the empDao object
     */
    public static void setEmpDao(EmployeeDao empDao){
        EmployeeController.empDao = empDao;
    }

    /**
     * retrieves the currentUser object and returns it as a JSON object
     */
    public static Handler handleMyProfileGet = ctx->{
        Object myProfile = ctx.sessionAttribute("currentUser");
        ctx.json(myProfile);
    };

    /**
     * updates the state of the current user (except the password) in tjhe database using form data
     */
    public static Handler handleUpdateProfilePost = ctx ->{
        Employee myProfile = ctx.sessionAttribute("currentUser");
        myProfile.setEmail(ctx.formParam("email"));
        myProfile.setFirstName(ctx.formParam("firstName"));
        myProfile.setLastName(ctx.formParam("lastName"));
        myProfile.setJobTitle(ctx.formParam("jobTitle"));
        if(ctx.formParam("birthday")==null||ctx.formParam("birthday").equals(""))
            myProfile.setBirthday(null);
       else
            myProfile.setBirthday(Date.valueOf(ctx.formParam("birthday")));
        myProfile.setAboutMe(ctx.formParam("aboutMe"));

        empDao.updateEmployee(myProfile);

        ctx.sessionAttribute("currentUser", empDao.getEmployeeByEmailPassword(myProfile.getEmail(),
                myProfile.getPassword()).get(0));
        logger.info(myProfile.getEmail()+" update profile success");
        ctx.redirect("/employee/myProfile.html");
    };

    /**
     * updates the password of the current user
     */
    public static Handler handleUpdatePasswordPost = ctx->{
        Employee myProfile = ctx.sessionAttribute("currentUser");
        if(ctx.formParam("oldPass").equals(myProfile.getPassword())){
            myProfile.setPassword(ctx.formParam("newPass"));
            empDao.updateEmployee(myProfile);
            logger.info(myProfile.getEmail()+" update password success");
            ctx.redirect("/updatePasswordSuccess.html");
        }else{
            logger.info(myProfile.getEmail()+" update password fail");
            ctx.redirect("/updatePasswordFail.html");
        }
    };

    /**
     * retrieves all the employees in the database and returns them as a json object
     */
    public static Handler handleAllEmployeesGet = ctx ->{
        EmployeeDao emDao = new EmployeeDao();
        List<Employee> allEmployees = emDao.getAllEmployees();
        ctx.json(allEmployees);
    };

    /**
     * registers a new employee from form data
     */
    public static Handler handleRegisterEmployeePost = ctx ->{
        Employee myProfile = new Employee();
        myProfile.setEmail(ctx.formParam("email"));
        myProfile.setFirstName(ctx.formParam("firstName"));
        myProfile.setLastName(ctx.formParam("lastName"));

        myProfile.setPassword(Integer.toString((int)(Math.random()*1000000000)));

        try{
            empDao.insertEmployee(myProfile);
            sendRegisteredEmail(myProfile);
            logger.info(myProfile.getEmail()+" registration success");
            ctx.redirect("/registerEmployeeSuccess.html");
        }catch (Exception e){
            logger.info(myProfile.getEmail()+" registration fail");
            ctx.redirect("/registerEmployeeFail.html");

        }



    };

    public static Handler handleEmployeePasswordResetPost = ctx ->{
        Employee myProfile = empDao.getEmployeeByEmail(ctx.formParam("email"));
        if (myProfile!=null){
            myProfile.setPassword(Integer.toString((int)(Math.random()*1000000000)));
            empDao.updateEmployee(myProfile);

            String message = "Hello. It appears you have recently reset your password. Your new password is: "
                    +myProfile.getPassword();
            Email email = new SimpleEmail();
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("reimbursementexamplesite1234@gmail.com",
                    "Pc8&y31NSwEAW"));
            email.setSSLOnConnect(true);
            email.setFrom("reimbursementexamplesite1234@gmail.com");
            email.setSubject("Your password has been reset"); // subject from HTML-form
            email.setMsg(message); // message from HTML-form
            try {
                email.addTo(myProfile.getEmail());
                email.send(); // will throw email-exception if something is wrong
                logger.info(myProfile.getEmail()+" password reset success");
                ctx.redirect("employeePasswordResetSuccess.html");
            }catch(Exception e){
                ctx.html("email in system but not valid email");
            }
        }else {
            logger.info(ctx.formParam("email")+" password reset fail - email not in system");
            ctx.redirect("employeePasswordResetFail.html");
        }
    };

    public static void sendRegisteredEmail(Employee emp) throws EmailException {
        String message = "Hello "+emp.getFirstName()+" "+emp.getLastName()+",\n\nYour account is now registered!" +
                "Your sign-in credentials are:\nE-mail: "+emp.getEmail()+" (obviously)\nTemporary password: "
                +emp.getPassword()+"\n\nWe look forward to seeing you soon!";
        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("reimbursementexamplesite1234@gmail.com", "Pc8&y31NSwEAW"));
        email.setSSLOnConnect(true);
        email.setFrom("reimbursementexamplesite1234@gmail.com");
        email.setSubject("You have been registered!"); // subject from HTML-form
        email.setMsg(message); // message from HTML-form
        email.addTo(emp.getEmail());
        email.send(); // will throw email-exception if something is wrong
    }


}
