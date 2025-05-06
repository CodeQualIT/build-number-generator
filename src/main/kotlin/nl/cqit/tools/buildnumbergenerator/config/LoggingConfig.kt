package nl.cqit.tools.buildnumbergenerator.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

/**
 * To use, add this to your class:
 *
 * `companion object { val log by LoggerDelegate() }`
 */
class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
    override fun getValue(thisRef: R, property: KProperty<*>): Logger =
        getLogger(getClassForLogging(thisRef.javaClass))
}

private fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}

private fun getLogger(forClass: Class<*>): Logger =
    LoggerFactory.getLogger(forClass)