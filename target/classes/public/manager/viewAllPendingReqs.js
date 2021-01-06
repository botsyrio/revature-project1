console.log("hello");
let listRoot = document.getElementById('allPendingList');

fetch('/manager/allPendingReqs')
    .then(res => res.json())
    .then(requestList => {
        for(const request of requestList){
            let nextNode = document.createElement("div");

            let dateField = document.createElement("div");
            let date = new Date(parseInt(request.dateOf));
            dateField.textContent=date;
            nextNode.appendChild(dateField);

            let amountField = document.createElement("div");
            amountField.textContent = request.amount;
            nextNode.appendChild(amountField);

            let detailsField = document.createElement("div");
            detailsField.textContent = request.details;
            nextNode.appendChild(detailsField);

            let creatorField = document.createElement("div");
            creatorField.textContent = request.employeeCreator.firstName;
            nextNode.appendChild(creatorField);

            let statusField = document.createElement("div");
            statusField.textContent = request.status;
            nextNode.appendChild(statusField);

            let approveForm = document.createElement("form");
            approveForm.setAttribute("action", "/manager/approveReq");
            approveForm.setAttribute("method", "POST");
            let hiddenReqId = document.createElement("input");
            approveForm.appendChild(hiddenReqId);
            hiddenReqId.setAttribute("type", "hidden");
            hiddenReqId.setAttribute("name", "reqId");
            hiddenReqId.setAttribute("value", request.id);
            let approveButton = document.createElement("input");
            approveForm.appendChild(approveButton);
            approveButton.setAttribute("type", "submit");
            approveButton.setAttribute("value", "Approve");
            nextNode.appendChild(approveForm);
            
            let denyForm = document.createElement("form");
            denyForm.setAttribute("action", "/manager/denyReq");
            denyForm.setAttribute("method", "POST");
            denyForm.appendChild(hiddenReqId.cloneNode());
            let denyButton = document.createElement("input");
            denyForm.appendChild(denyButton);
            denyButton.setAttribute("type", "submit");
            denyButton.setAttribute("value", "Deny");
            nextNode.appendChild(denyForm);

            listRoot.appendChild(nextNode);
        }
    });