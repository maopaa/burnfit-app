package com.mortega.burnfit.base

class Model {

    var name: String ?= null
    var bajoPeso: String ?= null
    var medioPeso: String ?= null
    var altoPeso: String ?= null
    var time: String ?= null

    constructor():this("","","","","") {

    }

    constructor(name: String?, bajoPeso: String?, medioPeso: String?, altoPeso: String?, time: String?) {

        this.name = name
        this.bajoPeso = "$bajoPeso kcal"
        this.medioPeso = "$medioPeso kcal"
        this.altoPeso = "$altoPeso kcal"
        this.time = "$time min."
    }
}