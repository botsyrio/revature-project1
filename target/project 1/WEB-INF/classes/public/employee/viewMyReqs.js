document.getElementById('filterForm').addEventListener("submit", (e)=>{
    e.preventDefault();
    filter();
});

let listHead = document.getElementById('myTable');
function filter(){

    let status = document.getElementById('statusSelect').value;
    console.log(status);
    let address;
    if(status === 'all')
        address = "/employee/myReqs";
    else if(status === 'pending')
        address = '/employee/myPendingReqs';
    else if (status === 'resolved'){
        address = '/employee/myResolvedReqs';
    }

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


            listHead.appendChild(nextNode);
        }
    });
}
filter();