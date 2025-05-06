package nl.cqit.tools.buildnumbergenerator.storage

import com.fasterxml.jackson.databind.ObjectMapper
import nl.cqit.tools.buildnumbergenerator.config.LoggerDelegate
import org.rocksdb.RocksDB
import org.rocksdb.RocksDBException
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

@Service
class RocksDbStore(
    val rocksDb: RocksDB,
    val objectMapper: ObjectMapper,
) {
    private val charset: Charset = StandardCharsets.UTF_8

    companion object {
        val log by LoggerDelegate()
    }

    fun String.toByteArray() = this.toByteArray(charset)

    operator fun <V> set(key: String, value: V) {
        try {
            val valueBytes = objectMapper.writeValueAsBytes(value)
            rocksDb.put(key.toByteArray(), valueBytes)
        } catch (e: RocksDBException) {
            log.error("RocksDB put error for key '{}': {}", key, e.message, e)
            throw RuntimeException("RocksDB put error: ${e.message}", e)
        } catch (e: Exception) {
            log.error("Serialization error for key '{}': {}", key, e.message, e)
            throw RuntimeException("Serialization error: ${e.message}", e)
        }
    }

    final inline operator fun <reified V> get(key: String): V? {
        return try {
            val bytes = rocksDb.get(key.toByteArray())
            if (bytes != null) {
                objectMapper.readValue(bytes, V::class.java)
            } else {
                null
            }
        } catch (e: RocksDBException) {
            log.error("RocksDB get error for key '{}': {}", key, e.message, e)
            throw RuntimeException("RocksDB get error: ${e.message}", e)
        } catch (e: Exception) {
            log.error("Deserialization error for key '{}': {}", key, e.message, e)
            throw RuntimeException("Deserialization error: ${e.message}", e)
        }
    }

    fun delete(key: String) {
        try {
            rocksDb.delete(key.toByteArray())
        } catch (e: RocksDBException) {
            log.error("RocksDB delete error for key '{}': {}", key, e.message, e)
            throw RuntimeException("RocksDB delete error: ${e.message}", e)
        }
    }
}