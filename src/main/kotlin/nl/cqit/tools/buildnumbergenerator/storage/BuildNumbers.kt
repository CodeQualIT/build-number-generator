package nl.cqit.tools.buildnumbergenerator.storage

import nl.cqit.tools.buildnumbergenerator.DatedBuildNumber
import nl.cqit.tools.buildnumbergenerator.config.LoggerDelegate
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component // Using @Component

private const val CACHE_NAME = "buildNumbers"

@Component
class BuildNumbers(private val rocksDbStore: RocksDbStore) {

    companion object {
        val log by LoggerDelegate()
    }

    @Cacheable(cacheNames = [CACHE_NAME], key = "#application")
    @Retryable(
        value = [RuntimeException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    )
    operator fun get(application: String): DatedBuildNumber? {
        log.info("Attempting to get build number for application '{}'", application)
        val buildNumber: DatedBuildNumber? = rocksDbStore[application]
        log.info("Found build number: {}", buildNumber)
        return buildNumber
    }

    @CachePut(cacheNames = [CACHE_NAME], key = "#application")
    @Retryable(
        value = [RuntimeException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    )
    operator fun set(application: String, value: DatedBuildNumber): DatedBuildNumber {
        log.info("Attempting to set build number for application '{}' to '{}'", application, value)
        rocksDbStore[application] = value
        return value
    }

    @CacheEvict(cacheNames = [CACHE_NAME], key = "#key")
    @Retryable(
        value = [RuntimeException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000)
    )
    fun delete(key: String) {
        log.info("Attempting to delete build number for application '{}'", key)
        rocksDbStore.delete(key)
    }

    // --- Fallback Mechanism (for Retryable) ---

    fun get(key: String, t: Throwable): DatedBuildNumber? {
        log.warn("Fallback for getting the build number for application '{}' called due to exception: {}", key, t.message)
        // Return null as the data couldn't be retrieved
        return null
    }

    fun set(key: String, value: DatedBuildNumber, t: Throwable): DatedBuildNumber {
        log.warn("Fallback for setting the build number for application '{}' with value '{}' called due to exception: {}", key, value, t.message)
        // The CachePut on the original method will still update the cache with 'value'
        return value
    }

    fun delete(key: String, t: Throwable) {
        log.warn("Fallback for deleting the build number for application '{}' called due to exception: {}", key, t.message)
        // The CacheEvict on the original method will still remove from the cache
    }
}