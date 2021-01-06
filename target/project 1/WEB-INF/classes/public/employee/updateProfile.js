fetch('/employee/myProfile')
    .then(res => res.json())
    .then(profile => {
        document.getElementById("email").setAttribute("value", profile.email);
        document.getElementById("firstName").setAttribute("value", profile.firstName);
        document.getElementById("lastName").setAttribute("value", profile.lastName);
        if(profile.jobTitle != null){

            document.getElementById("jobTitle").setAttribute("value", profile.jobTitle);
        }
        if(profile.birthday != null){
            const myBirthday = new Date(parseInt(profile.birthday));

            let day = ("0" + myBirthday.getDate()).slice(-2);
            let month = ("0" + (myBirthday.getMonth() + 1)).slice(-2);
            let myBirthdayVal = myBirthday.getFullYear()+"-"+(month)+"-"+(day);

            //const myBirthdayVal = myBirthday.getFullYear()+"-"+(myBirthday.getMonth()+1)+"-"+myBirthday.getDate();
            console.log(myBirthdayVal);
            document.getElementById("birthday").setAttribute("value", myBirthdayVal);
        }

        if(profile.aboutMe != null)
            document.getElementById("aboutMe").textContent= profile.aboutMe;
    });