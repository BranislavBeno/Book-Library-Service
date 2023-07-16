function login_show_hide() {
  var passwd_input = document.getElementById("password-input");
  var passwd_icon = document.getElementById("password-icon");
  show_hide(passwd_input, passwd_icon);
}

function repeat_password_show_hide() {
  var passwd_input = document.getElementById("repeat-password-input");
  var passwd_icon = document.getElementById("repeat-password-icon");
  show_hide(passwd_input, passwd_icon);
}

function show_hide(input, icon) {
  if (input.type === "password") {
    input.type = "text";
    icon.classList.remove("fa-eye-slash");
    icon.classList.add("fa-eye");
  } else {
    input.type = "password";
    icon.classList.remove("fa-eye");
    icon.classList.add("fa-eye-slash");
  }
}