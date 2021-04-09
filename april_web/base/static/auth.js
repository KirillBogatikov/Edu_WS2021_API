
function login() {
    let login = find("#l-login")
    let password = find("#l-password")

    request("/login", "POST", JSON.stringify({
        login: login.value,
        password: password.value
    }), function(xhr, status, st, response) {
        switch(status) {
            case 200:
                document.location.href = "/"
                break;
            case 404:
                dialog("Пользователь не найден")
                break;
            case 403:
                dialog("Пароль введен не верно")
            default:
                dialog(response)
                break;
        }
    })
}

function signup() {
    let login = find("#s-login")
    let password = find("#s-password")
    let password_check = find("#password-check")
    let first_name = find("#first-name")
    let last_name = find("#last-name")
    let patronymic = find("#patronymic")
    let email = find("#email")

    request("/signup", "POST", JSON.stringify({
        login: login.value,
        password: password.value,
        password_check: password_check,
        first_name: first_name.value,
        last_name: last_name.value,
        patronymic: patronymic.value,
        email: email.value
    }), function(xhr, status, st, response) {

    })
}

function logout() {
    request("/logout", "GET", null, function(xhr, code, status, response) {
        if (code == 200) {
            dialog("Вы вышли из аккаунта")
            document.location.href = "/"
        }
    })
}