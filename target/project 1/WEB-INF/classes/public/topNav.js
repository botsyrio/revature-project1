fetch('/employee/myProfile')
    .then(res => res.json())
    .then(profile => {
        document.getElementById("myProfileLink").textContent=profile.firstName+" "+profile.lastName;
    });
