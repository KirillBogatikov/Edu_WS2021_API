Task = function(name, text, date) {
    if (typeof(name) == 'object') {
        Object.assign(this, name)
        this.date = new Date(name.date)
        return
    }
    
    this.name = name
    this.date = date
    this.text = text || ""
    this.ready = false
    this.sub_tasks = new Collection("sub_tasks")
    this.ready = false
}

function one_day(a, b) {
    return a.getDate() == b.getDate() && a.getMonth() == b.getMonth() && a.getYear() == b.getYear()
}

Task.prototype.getStatus = function() {
    let now = new Date()
    let delta = (this.date - now) / 1000

    if (delta < 0 && !one_day(this.date, now)) {
        return Status.Expired
    }

    if (one_day(this.date, now)) {
        return Status.Today
    }

    if (delta < 2 * 24 * 60 * 60) {
        return Status.Tomorrow
    }

    if (delta < 3 * 24 * 60 * 60) {
        return Status.AfterTomorrow
    }

    return Status.Future
}

SubTask = function(name, description) {
    this.name = name
    this.description = description
}

constant_descriptor = function(v) {
    get = function(){ return v }
    if (typeof(v) == 'function') {
        get = v
    }
    
    return {
        configurable: false,
        get: get,
        set: function() { throw "It is constant!" }
    }
};

Status = {};

Object.defineProperty(Status, "Future", constant_descriptor(0x0))
Object.defineProperty(Status, "AfterTomorrow", constant_descriptor(0x1))
Object.defineProperty(Status, "Tomorrow", constant_descriptor(0x2))
Object.defineProperty(Status, "Today", constant_descriptor(0x3))
Object.defineProperty(Status, "Expired", constant_descriptor(0x4))

function make_ready(name) {
    db.get("tasks", name).ready = true
    db.save()
}