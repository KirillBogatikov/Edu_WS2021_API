find = function(q){ return document.querySelector(q) }

get_cookie = function(name) {
    parts = document.cookie.split(name + "=")
    if (parts.length > 1) {
        return parts[1].split(";")[0]
    }

    return "";
}

request = function(url, method, body, callback, contentType) {
    let xhr = new XMLHttpRequest()
    xhr.open(method, url, true)
    xhr.setRequestHeader("X-CSRFToken", get_cookie("csrftoken"))
    if (contentType) {
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
    }
    xhr.send(body)
    xhr.onreadystatechange = function() {
        if (xhr.readyState != 4) return;

        callback(xhr, xhr.status, xhr.statusText, xhr.responseText)
    }
}

dialog = function(msg) {
    alert(msg)
}