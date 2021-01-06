console.log("hello");
let listRoot = document.getElementById('myPendingList');

fetch('/employee/myPendingReqs')
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

            listRoot.appendChild(nextNode);
        }
    });