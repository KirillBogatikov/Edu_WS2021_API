query = function(q, all) {
    if (all)
        return document.querySelectorAll(q)

    return document.querySelector(q)
}

elem = function(tag) {
    return document.createElement(tag)
}

DialogLevel = -1
Dialog = function(dom, shadow) {
    this.dom = dom;
    this.shadow = shadow
}

const FPS = 90
const TIME = 200 / 1000

Dialog.prototype.show = function() {
    if (this.visible) {
        let i = parseInt(this.dom.style["z-index"])
        if (isNaN(i)) {
            this.dom.style["z-index"] = 4
        } else {
            this.dom.style["z-index"] = i + 2
        }

        return false
    }

    this.visible = true

    if (this.shadow) {
        this.call_shadow = this.shadow.show()
    }

    query("MAIN").appendChild(this.dom)

    let t = this;
    let o = 0.0;
    let a = function() {
        t.dom.style["opacity"] = o
        o += 1 / (FPS * TIME)
        if (o < 1)
            t.animation = setTimeout(a, 1000 / FPS)
    };
    a()
    return true
}

Dialog.prototype.hide = function(slide_down) {
    let i = parseInt(this.dom.style["z-index"])
    if (!isNaN(i)) {
        this.dom.style["z-index"] = i - 2
    }

    if (!this.visible || slide_down) {
        return false
    }

    if (this.shadow) {
        this.shadow.hide(!this.call_shadow)
        this.call_shadow = false
    }

    let t = this;
    let o = 1.0;
    let a = function() {
        t.dom.style["opacity"] = o
        o -= 1 / (FPS * TIME)
        if (o > 0) {
            t.animation = setTimeout(a, 1000 / FPS)
        } else {
            t.dom.remove()
            t.visible = false
        }
    };
    a()
    return true
}

Dialog.prototype.hideForce = function() {
    this.dom.remove()
}

function toggle_check(li) {
    let last = query("LI[checked]")
    if (last) last.removeAttribute("checked")
    if (li) li.setAttribute("checked", "")
}

DateFormat = new Intl.DateTimeFormat('ru-RU')

function show_tasks(status) {
    Result.remove()
    if (ResultHolder.lastElementChild) ResultHolder.lastElementChild.remove()

    toggle_check(this)

    let filter = typeof(status) == 'function' ? status : (k, v) => v.getStatus() == status
    let items =  db.getCollection("tasks").get(filter, a => new Task(a))
    let table = elem("table")

    let row;
    let i = 0
    for (let key in items) {
        if (i % 2 == 0) {
            row = elem("tr")
        }

        let cell = elem("td")
        let task = items[key]
        let html = "<div class='task-view'><span class='task-date'>" + DateFormat.format(task.date) + "</span><span class='task-title'>" + task.name + "</span><span class='task-desc'>" + task.desc + "</span>"

        if (task.ready) {
            html += "<input type='checkbox' checked onclick='this.checked = true' >Выполнено"
        }

        html += "<hr></hr>"

        let list = "<ul class='no-marker'>"

        let sub_tasks = task.sub_tasks.values
        for (let key in sub_tasks) {
            let subtask = sub_tasks[key]
            list += "<li><span class='task-title subtask'>" + subtask.name + "</span><span class='task-desc subtask'>" + subtask.description + "</span><hr></hr></li>"
        }
        cell.innerHTML += html + list + "</ul><button onclick='make_ready(`" + task.name + "`)'>Отметить выполненным</button></div>"
        row.append(cell)
        
        if (i % 2 != 0) {
            table.append(row)
            row = null
        }
        i++
    }

    if (row != null) {
        table.append(row)
    }

    Result.style["align-items"] = "center"
    if (i > 0) {
        ResultHolder.append(table)
        setTimeout(function() {
            console.log(table.offsetHeight, parseInt(query("MAIN").style["min-height"]))
            if (table.offsetHeight > parseInt(query("MAIN").style["min-height"])) {
                Result.style["align-items"] = "normal"
            }
        }, 200)
    } else {
        let noTasks = elem("span")
        noTasks.class = "no-tasks"
        noTasks.innerHTML = "Нет задач"
        ResultHolder.append(noTasks)
    }

    query("MAIN").append(Result)
}

let Shadow, TaskForm, SubTaskForm;
let db  = new Database()
let current_task;

onload = function() {
    let headerHeight = query("HEADER").offsetHeight
    let bodyMinHeight = query("HTML").offsetHeight - headerHeight
    query("MAIN").style["min-height"] = bodyMinHeight
    query(".dialog-container").style["height"] = bodyMinHeight

    query(".dialog-container").style["margin-top"] = headerHeight

    Shadow = new Dialog(query("#shadow"))
    Shadow.hideForce();

    TaskForm = new Dialog(query("#task"), Shadow)
    TaskForm.hideForce();
    TaskForm.__show_origin = TaskForm.show
    TaskForm.show = function() {
        current_task = new Task()
        this.__show_origin.apply(this, arguments)
        toggle_check(query("#new-task"))
    }
    TaskForm.__hide_origin = TaskForm.hide
    TaskForm.hide = function() {
        toggle_check(false)
        this.__hide_origin.apply(this, arguments)
    }

    TaskForm.save = function() {
        let name = query("#task-name").value
        let date = new Date(query("#task-date").value)
        let desc = query("#task-desc").value

        current_task.name = name
        current_task.date = date
        current_task.desc = desc

        db.getCollection("tasks").save(current_task.name, current_task)
        delete current_task
        this.hide()

        query("#subtask-no-tasks").style["display"] = "block"
        query("#subtask-list").innerHTML = ""
    };

    SubTaskForm = new Dialog(query("#subtask"), Shadow)
    SubTaskForm.hideForce();
    SubTaskForm.open = function(info) {
        this.edit_mode = true
        this.show()
        query("#subtask-name").value = info.name
        query("#subtask-desc").value = info.description
    }
    SubTaskForm.save = function() {
        let name = query("#subtask-name").value
        let desc = query("#subtask-desc").value

        let c = current_task.sub_tasks
        if (c.has(name) && !edit_mode) {
            alert("Подзадача с таким названием уже есть в этой задаче")
            return
        }

        this.edit_mode = false

        let subTask = new SubTask(name, desc)
        c.save(name, subTask)

        query("#subtask-no-tasks").style["display"] = "none"
        let list = query("#subtask-list")

        let newItem = elem("li")
        newItem.innerHTML = name
        newItem.onclick = function() {
            SubTaskForm.open(subTask)
        }
        list.insertBefore(newItem, list.lastChild)
        this.hide()
    }

    Result = query("#result")
    ResultHolder = query("#result-holder")
    Result.remove()

    if (document,location.hash.length > 0) {
        query(document.location.hash).click()
    } else {
        query("#today").click()
    }
}

