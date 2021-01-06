
fetch('/employee/myProfile')
    .then(res => res.json())
    .then(profile => {
        document.getElementById("emailValue").textContent=profile.email;
        document.getElementById("firstNameValue").textContent=profile.firstName;
        document.getElementById("lastNameValue").textContent=profile.lastName;
        document.getElementById("jobTitleValue").textContent=profile.jobTitle;
        if(profile.birthday != null)
            document.getElementById("birthdayValue").textContent=new Date(parseInt(profile.birthday)).toLocaleDateString("en-US");
        document.getElementById("aboutMeValue").textContent=profile.aboutMe;
    });