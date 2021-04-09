function index() {
    document.location.href = "/"
}

function workplace() {
    document.location.href = "/user/"
}

function create_issue() {
    var form = new FormData()
    form.append("name", find("#pet-name").value)
    form.append("pet_photo", find("#pet-photo").files[0])
    alert(find("#pet-photo").files[0])
    form.append("status", 0)

    request("/issue/save", "POST", form, function(xhr, code, status, response) {
        if (code == 200) {
            dialog("Заявка создана")
            workplace();
        } else {
            dialog("Ошибка в создании заявки")
        }
    })
}