function save_issue() {
    var form = new FormData()
    form.append("name", find("#pet-name").value)
    let status = find("#issue-status").value
    form.append("status", status)
    form.append("issue_id", currentIssueId)
    currentIssueId = undefined

    if (status == 2) {
        form.append("result_photo", find("#result-photo").files[0])
    }

    request("/issue/save", "POST", form, function(xhr, code, status, response) {
        if (code == 200) {
            dialog("Заявка сохранена")
            workplace();
        } else {
            dialog("Ошибка в сохранении заявки")
        }
    })

    find("#create-issue").style = "";
}

function edit(id) {
    request('/groom/issue/' + id, 'GET', null, function(xhr, code, status, response) {
        if (code == 200) {
           issue = JSON.parse(response)

           if (issue.status == 2) {
               dialog("Нельзя редактировать оказанную услугу!")
               return
           }

           currentIssueId = id
           find("#create-issue").style = "opacity:1.0;"

           with(issue.user.bio) {
               find("#user").innerHTML = "Заявка от " + last_name + " " + first_name + " " + patronymic
           }
           find("#pet-name").value = issue.name
           find("#pet-photo-current").src = "/issue/photo/" + issue.pet_photo
        } else {
            dialog("Возникла ошибка при получении данных с сервера. Повторите попытку позже")
        }
    })
}