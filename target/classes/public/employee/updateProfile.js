fetch('/employee/myProfile')
    .then(res => res.json())
    .then(profile => {
        document.getElementById("email").setAttribute("value", profile.email);
        document.getElementById("firstName").setAttribute("value", profile.firstName);
        document.getElementById("lastName").setAttribute("value", profile.lastName);
        if(profile.jobTitle != null)
            document.getElementById("jobTitle").setAttribute("value", profile.jobTitle);
        if(profile.birthday != null){
            const myBirthday = new Date(parseInt(profile.birthday));
            const myBirthdayVal = myBirthday.getFullYear()+"-"+(myBirthday.getMonth()+1)+"-"+myBirthday.getDate();
            document.getElementById("birthday").setAttribute("value", myBirthdayVal);
        }
        if(profile.aboutMe != null)
            document.getElementById("aboutMe").textContent= profile.aboutMe;
    });