package org.momosi.shutupandcharge.core.common.environment

object AppEnvironment {

    var isDebug: Boolean = false
        private set

    fun init(isDebug: Boolean) {
        this.isDebug = isDebug
    }
}
