
document.getElementById('filterForm').addEventListener("submit", (e)=>{
    e.preventDefault();
    filter();
});
let employeeSelect = document.getElementById('employeeSelect');
function populateEmployeeSelect(){
    fetch('/manager/allEmployees')
    .then(res => res.json())
    .then(employeeList => {
        for(const employee of employeeList){
            let myOption = document.createElement("option");
            myOption.setAttribute("value", employee.id);
            myOption.textContent=employee.firstName+" "+employee.lastName+" ("+employee.email+")";
            employeeSelect.appendChild(myOption);
        }    
    });
}
let listHead = document.getElementById('myTable');
function filter(){
    let employee = document.getElementById('employeeSelect').value;
    let status = document.getElementById('statusSelect').value;
    let address;
    if(employee === 'all'){
        if(status === 'all')
            address = "/manager/allReqs";
        else if(status === 'pending')
            address = '/manager/allPendingReqs';
        else if (status === 'resolved'){
            address = '/manager/allResolvedReqs';
        }
    }else(address="/manager/userReqs?id="+employee+"&status="+status);

    //TODO: RE-ADD THE APPROVE/DENY FUNCTIONALITY
    fetch(address)
    .then(res => res.json())
    .then(requestList => {
        let tableHead = document.querySelector(".tableHead").cloneNode(true);
        listHead.innerHTML = '';
        listHead.appendChild(tableHead);
        for(const request of requestList){
            let nextNode = document.createElement("tr");
            nextNode.classList.add("listItem")

            let dateField = document.createElement("td");
            dateField.classList.add("listField");
            dateField.classList.add("dateField");
            let date = new Date(parseInt(request.dateOf));
            dateField.textContent=date.toLocaleDateString("en-US");
            nextNode.appendChild(dateField);

            let amountField = document.createElement("td");
            amountField.classList.add("listField");
            amountField.classList.add("amountField");
            amountField.textContent = request.amount.toLocaleString("en-US", {
              style: "currency",
              currency: "USD"
            });
            nextNode.appendChild(amountField);


            let detailsField = document.createElement("td");
            detailsField.classList.add("listField");
            detailsField.classList.add("detailsField");
            detailsField.textContent = request.details;
            nextNode.appendChild(detailsField);

            let creatorField = document.createElement("td");
            creatorField.classList.add("listField");
            creatorField.classList.add("creatorField");
            creatorField.textContent = request.employeeCreator.firstName+" "+request.employeeCreator.lastName;
            nextNode.appendChild(creatorField);
            
            let statusField = document.createElement("td");
            statusField.classList.add("listField");
            statusField.classList.add("statusField");
            statusField.textContent = request.status;
            nextNode.appendChild(statusField);

            let resolvedByField = document.createElement("td");
            resolvedByField.classList.add("listField");
            resolvedByField.classList.add("resolvedByField");
            if(request.status !== "pending")
                resolvedByField.textContent=request.managerResolver.firstName+" "+request.managerResolver.lastName;
            nextNode.appendChild(resolvedByField);

            let resolveField = document.createElement("td");
            resolveField.classList.add("listField");
            resolveField.classList.add("resolveField");


            //TODO: MAKE IT SO THESE FORMS DON'T HAVE TO REFRESH THE PAGE, BUT DO A FETCH
            let approveForm = document.createElement("form");
            approveForm.classList.add("resolveForm");
            approveForm.setAttribute("action", "/manager/approveReq");
            approveForm.setAttribute("method", "POST");
            let hiddenReqId = document.createElement("input");
            approveForm.appendChild(hiddenReqId);
            hiddenReqId.setAttribute("type", "hidden");
            hiddenReqId.setAttribute("name", "reqId");
            hiddenReqId.setAttribute("value", request.id);
            let approveButton = document.createElement("input");
            approveButton.classList.add("approveButton");
            approveForm.appendChild(approveButton);
            approveButton.setAttribute("type", "submit");
            approveButton.setAttribute("value", "Approve");
            
            let denyForm = document.createElement("form");
            denyForm.classList.add("resolveForm");
            denyForm.setAttribute("action", "/manager/denyReq");
            denyForm.setAttribute("method", "POST");
            denyForm.appendChild(hiddenReqId.cloneNode());
            let denyButton = document.createElement("input");
            denyButton.classList.add("denyButton");
            denyForm.appendChild(denyButton);
            denyButton.setAttribute("type", "submit");
            denyButton.setAttribute("value", "Deny");

            if(request.status ==="pending"){
                resolveField.appendChild(approveForm);
                resolveField.appendChild(denyForm);
            }
            else{
                let spacer = document.createElement("div");
                spacer.classList.add("spacer");
                resolveField.appendChild(spacer);
            }

            nextNode.appendChild(resolveField);


            listHead.appendChild(nextNode);
        }
    });
}
populateEmployeeSelect();
filter();