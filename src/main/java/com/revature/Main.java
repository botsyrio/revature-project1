package com.revature;

import com.revature.controllers.EmployeeController;
import com.revature.controllers.LoginController;
import com.revature.controllers.reimbursementReqController;
import io.javalin.Javalin;
import io.javalin.http.Handler;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(8000);


        app.routes(() -> {
            before(LoginController.ensureLoginBeforeViewingEmployee);
            before(LoginController.ensureLoginBeforeViewingManager);

            post("/employeeLogin", LoginController.handleEmployeeLoginPost);
            post("/employee/requestSubmit", reimbursementReqController.handleRequestSubmitPost);
            get("/employee/myReqs", reimbursementReqController.handleMyReqsGet);
            get("/employee/myPendingReqs", reimbursementReqController.handleMyPendingReqsGet);
            get("/employee/myResolvedReqs", reimbursementReqController.handleMyResolvedReqsGet);
            get("/employee/myProfile", EmployeeController.handleMyProfileGet);
            post("/employee/updateProfile", EmployeeController.handleUpdateProfilePost);
            post("/employee/updatePassword", EmployeeController.handleUpdatePasswordPost);

            post("/managerLogin", LoginController.handleManagerLoginPost);
            get("/manager/myProfile", EmployeeController.handleMyProfileGet);
            get("/manager/allReqs", reimbursementReqController.handleAllReqsGet);
            get("/manager/allPendingReqs", reimbursementReqController.handleAllPendingReqsGet);
            post("/manager/approveReq", reimbursementReqController.handleApproveReqPost);
            post("/manager/denyReq", reimbursementReqController.handleDenyReqPost);
            get("/manager/allResolvedReqs", reimbursementReqController.handleAllResolvedReqsGet);
            get("/manager/allEmployees", EmployeeController.handleAllEmployeesGet);
            get("/manager/userReqs", reimbursementReqController.handleUserReqsGet);
            post("/manager/registerEmployee", EmployeeController.handleRegisterEmployeePost);

            post("/employeePasswordReset", EmployeeController.handleEmployeePasswordResetPost);
            get("/logout", LoginController.handleLogoutGet);
        });
    }



}
