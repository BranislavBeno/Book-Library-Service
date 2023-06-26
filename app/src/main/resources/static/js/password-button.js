function login_show_hide() {
  var passwd_input = document.getElementById("password-input");
  var passwd_icon = document.getElementById("password-icon");
  show_hide(passwd_input, passwd_icon);
}

function previous_show_hide() {
  var passwd_input = document.getElementById("previous-password-input");
  var passwd_icon = document.getElementById("previous-password-icon");
  show_hide(passwd_input, passwd_icon);
}

function proposed1_show_hide() {
  var passwd_input = document.getElementById("proposed-password-1-input");
  var passwd_icon = document.getElementById("proposed-password-1-icon");
  show_hide(passwd_input, passwd_icon);
}

function proposed2_show_hide() {
  var passwd_input = document.getElementById("proposed-password-2-input");
  var passwd_icon = document.getElementById("proposed-password-2-icon");
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