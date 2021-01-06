function verifyIdentical(newPassConfirm){
    if(newPassConfirm.value != document.getElementById("newPass").value)
        newPassConfirm.setCustomValidity('Password must match');
    else
        newPassConfirm.setCustomValidity('');
}