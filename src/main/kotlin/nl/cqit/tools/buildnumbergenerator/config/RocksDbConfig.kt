package nl.cqit.tools.buildnumbergenerator.config

import jakarta.annotation.PreDestroy
import org.rocksdb.Options
import org.rocksdb.RocksDB
import org.rocksdb.RocksDBException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Files.createDirectories
import java.nio.file.Paths
import kotlin.io.path.absolute

@Configuration
class RocksDbConfig(@Value("\${rocksdb.path}") private val rocksDbPath: String) {

    companion object {
        val log by LoggerDelegate()
    }

    private lateinit var rocksDB: RocksDB

    @Bean
    fun rocksDb(): RocksDB {
        RocksDB.loadLibrary()
        val options = Options().apply {
            setCreateIfMissing(true)
            // Add other RocksDB options as needed
        }
        val absolutePath = Paths.get(rocksDbPath).absolute()
        try {
            log.info("Trying to open RocksDB at $absolutePath ...")
            createDirectories(absolutePath)
            rocksDB = RocksDB.open(options, absolutePath.toString())
            log.info("RocksDB opened successfully at $absolutePath")
            return rocksDB
        } catch (e: RocksDBException) {
            log.error("Error opening RocksDB at $absolutePath: ${e.message}", e)
            throw e
        }
    }

    @PreDestroy
    fun closeRocksDb() {
        if (::rocksDB.isInitialized) {
            rocksDB.close()
            log.info("RocksDB closed.")
        }
    }
}