package com.revature.controllers;

import com.revature.daos.EmployeeDao;
import com.revature.daos.ManagerDao;
import com.revature.daos.ReimbursementReqDao;
import com.revature.models.Employee;
import com.revature.models.Manager;
import com.revature.models.ReimbursementReq;
import io.javalin.http.Handler;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * holds all handler methods involved with dealing with reimbursement requests
 */
public class reimbursementReqController {

    /**
     * the dao object responsible for interfacing with the reimbursement request table
     */
    private static final ReimbursementReqDao reqDao = new ReimbursementReqDao();

    /**
     * creates a reimbursement request with form data
     */
    public static Handler handleRequestSubmitPost = ctx -> {
        System.out.println("hello");
        ReimbursementReq request = new ReimbursementReq();
        request.setEmployeeCreator(ctx.sessionAttribute("currentUser"));
        request.setDateOf(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(ctx.formParam("dateOf")).getTime()));
        request.setAmount(Double.parseDouble(ctx.formParam("amount")));
        request.setDetails(ctx.formParam("details"));
        request.setStatus("pending");

        reqDao.insertReimbursementReq(request);
        ctx.redirect("/employee/viewMyReqs.html");
    };

    /**
     * returns the current user's requests in a json object
     */
    public static Handler handleMyReqsGet = ctx ->{
      List<ReimbursementReq> allMyReqs = reqDao.getAllFromEmployee(ctx.sessionAttribute("currentUser"));
      ctx.json(allMyReqs);
    };

    /**
     * returns the current user's pending requests in a json object
     */
    public static Handler handleMyPendingReqsGet = ctx -> {
        System.out.println("hello");
        List<ReimbursementReq> myPendingRequests = reqDao.getPendingFromEmployee(ctx.sessionAttribute("currentUser"));
        ctx.json(myPendingRequests);

    };
    /**
     * returns the current user's resolved requests in a json object
     */
    public static Handler handleMyResolvedReqsGet = ctx -> {
        List<ReimbursementReq> myResolvedRequests = reqDao.getResolvedFromEmployee(ctx.sessionAttribute("currentUser"));
        ctx.json(myResolvedRequests);

    };

    /**
     * retrieves the batch of user belonging to an employee as specified by form data
     */
    public static Handler handleUserReqsGet = ctx -> {
        int userId = Integer.valueOf(ctx.queryParam("id"));
        String status = ctx.queryParam("status");
        EmployeeDao employeeDao = new EmployeeDao();
        Employee myEmployee = employeeDao.getEmployeeById(userId);
        List<ReimbursementReq> requests;
        if(status.equals("all"))
            requests = reqDao.getAllFromEmployee(myEmployee);
        else if (status.equals("pending"))
            requests=reqDao.getPendingFromEmployee(myEmployee);
        else
            requests=reqDao.getResolvedFromEmployee(myEmployee);

        ctx.json(requests);
    };

    /**
     * retrieves all reimbursement requests in the system and returns them in a json object
     */
    public static Handler handleAllReqsGet = ctx -> {
        List<ReimbursementReq> allReqs = reqDao.getAll();
        ctx.json(allReqs);
    };

    /**
     * retrieves all pending reimbursement requests in the system and returns them in a json object
     */
    public static Handler handleAllPendingReqsGet = ctx -> {
        List<ReimbursementReq> allPendingRequests = reqDao.getAllPending();
        ctx.json(allPendingRequests);
    };

    /**
     * approves a specified reimbursement request
     */
    public static Handler handleApproveReqPost = ctx -> {
        ReimbursementReq myReq = reqDao.getReqById(Integer.valueOf(ctx.formParam("reqId")));
        reqDao.resolveReimbursementReq(myReq, "approved", ctx.sessionAttribute("currentUser"));
        try{
            sendResolvedReqEmail(myReq);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            ctx.redirect("/manager/viewReqs.html");
        }
    };

    /**
     * denies a specified reimbursement request
     */
    public static Handler handleDenyReqPost = ctx -> {
        ReimbursementReq myReq = reqDao.getReqById(Integer.valueOf(ctx.formParam("reqId")));
        reqDao.resolveReimbursementReq(myReq, "denied", ctx.sessionAttribute("currentUser"));
        try{
            sendResolvedReqEmail(myReq);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            ctx.redirect("/manager/viewReqs.html");
        }

    };

    /**
     * sends an email to a user when their reimbursement request is approved
     * @param req the request that was resolved
     * @throws EmailException an exception involving email sending
     */
    public static void sendResolvedReqEmail(ReimbursementReq req) throws EmailException {
        String requestString = "{Date: "+req.getDateOf()+", Amount: $"+req.getAmount()+", Details: "+req.getDetails()+"}";
        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator("reimbursementexamplesite1234@gmail.com", "Pc8&y31NSwEAW"));
        email.setSSLOnConnect(true);
        email.setFrom("reimbursementexamplesite1234@gmail.com");
        email.setSubject("Your Reimbursement Request has been resolved!"); // subject from HTML-form
        email.setMsg("Your request \n\n"+requestString+"\n\n has been resolved!"); // message from HTML-form
        email.addTo(req.getEmployeeCreator().getEmail());
        email.send(); // will throw email-exception if something is wrong
    }

    /**
     * retrieves all the resolved requests in the system and returns them as a json object
     */
    public static Handler handleAllResolvedReqsGet = ctx -> {
        List<ReimbursementReq> allPendingRequests = reqDao.getAllResolved();
        ctx.json(allPendingRequests);
    };

}
