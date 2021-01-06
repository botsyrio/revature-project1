let loginEle = document.querySelector(".loginDropdown");
function loginClick(){
    if(!loginEle.classList.contains("clicked")){
        loginEle.classList.add("clicked");
    }
    else{
        loginEle.classList.remove("clicked");
    }
}
loginEle.addEventListener("click", loginClick);
