function password_show_hide() {
  var passwd_input = document.getElementById("password-input");
  var passwd_icon = document.getElementById("password-icon");
  if (passwd_input.type === "password") {
    passwd_input.type = "text";
    passwd_icon.classList.remove("fa-eye-slash");
    passwd_icon.classList.add("fa-eye");
  } else {
    passwd_input.type = "password";
    passwd_icon.classList.remove("fa-eye");
    passwd_icon.classList.add("fa-eye-slash");
  }
}