Collection = function(name, callback) {
    this.name = name
    this.values = {}
    this.save_callback = callback
}

Collection.prototype.get = function(f, c) {
    if (typeof(f) == 'string') {
        return this.values[f]
    }

    f = f || (a => true)
    c = c || (a => a)
    let data = {}
    let count = 0
    for (let key in this.values) {
        let v = c(this.values[key])
        if (f(key, v)) {
            data[key] = v
            count++
        }
    }

    return data
}

Collection.prototype.save = function(key, value) {
    this.values[key] = value
    
    if (this.save_callback) 
        this.save_callback(this)
}

Collection.prototype.remove = function(key) {
      delete this.values[key]
}

Collection.prototype.has = function(key) {
    for (let k in this.values) {
        if (k == key) return true
    }
    return false
}

let __save_callback = (db) => function(c) {
     db.storage['__index'] = JSON.stringify(db.collections)
}

Database = function() {
    this.storage = localStorage
    this.collections = {}

    if (this.storage['__index']) {
        let map = JSON.parse(this.storage.__index)  
        for (let key in map) {
            let c = new Collection(key, __save_callback(this))
            c.values = map[key].values
            this.collections[key] = c
        }
    }
}

Database.prototype.getCollection = function(name) {
    if (!this.collections[name]) {
        this.collections[name] = new Collection(name, __save_callback(this))
    }    
    
    return this.collections[name]
}

Database.prototype.get = function(c, f, p) {
    return this.collections[c].get(f, p)
}

Database.prototype.save = function(c, i, v) {
    if (arguments.length == 0) {
        __save_callback(this)()
        return
    }

    return this.collections[c].save(i, v)
}