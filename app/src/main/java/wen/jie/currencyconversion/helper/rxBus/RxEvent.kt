package wen.jie.currencyconversion.helper.rxBus

abstract class RxEvent<K, V> {
    var key: K? = null
    var value: V? = null
    var viewModeHashCode: Int = 0

    constructor(k: K, v: V) {
        this.key = k
        this.value = v
    }

    constructor() {
        // empty constructor
    }
}