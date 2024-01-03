package com.kamelia.hedera.rest.thumbnail

import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.configuration.GlobalConfigurationService
import com.kamelia.hedera.rest.file.DiskFileService
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.withReentrantLock
import io.ktor.util.logging.*
import io.trbl.blurhash.BlurHash
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.resizers.configurations.ScalingMode
import net.coobird.thumbnailator.tasks.UnsupportedFormatException
import javax.imageio.ImageIO
import com.kamelia.hedera.rest.file.File as HederaFile

data class ThumbnailContainer(
    val creationDate: Instant,
    val path: Path,
) : Comparable<ThumbnailContainer> {
    override fun compareTo(other: ThumbnailContainer): Int {
        // Compare by creation date, then by path
        return compareValuesBy(this, other, { it.creationDate }, { it.path })
    }
}

object ThumbnailService {

    private val LOGGER = KtorSimpleLogger("ThumbnailService")
    private val THUMBNAIL_PATH = Path.of(Environment.thumbnailFolder)
    private val SUPPORTED_MIMETYPES = setOf(
        "image/png",
        "image/gif",
        "image/jpeg",
    )

    private val mutex = Mutex()
    private val thumbnails = sortedSetOf<ThumbnailContainer>()

    init {
        Files.createDirectories(THUMBNAIL_PATH)
    }

    suspend fun init() = mutex.withReentrantLock {
        Files.list(THUMBNAIL_PATH).forEach {
            val creationDate = Instant.ofEpochMilli(it.toFile().lastModified())
            thumbnails.add(ThumbnailContainer(creationDate, it))
        }
        LOGGER.info("Loaded ${thumbnails.size} thumbnails")
        clearOldestFiles()
    }

    fun getBlurhashOrNull(
        file: File?
    ): String? {
        if (file == null) return null
        val bufferedImage = ImageIO.read(file)
        return BlurHash.encode(bufferedImage, 6, 4)
    }

    suspend fun getFolderSize(): Long = withContext(Dispatchers.IO) {
        mutex.withReentrantLock {
            Files.walk(THUMBNAIL_PATH).reduce(0L, { acc, path ->
                if (Files.isRegularFile(path)) acc + Files.size(path) else acc
            }, { acc1, acc2 -> acc1 + acc2 })
        }
    }

    suspend fun clearOldestFiles() = withContext(Dispatchers.IO) {
        mutex.withReentrantLock {
            val maximum = GlobalConfigurationService.currentConfiguration.maximumThumbnailCount

            val toDelete = thumbnails.size - maximum
            if (toDelete <= 0) return@withReentrantLock

            val deletedThumbnails = thumbnails.take(toDelete).toSet()
            thumbnails.removeAll(deletedThumbnails)
            deletedThumbnails.forEach { Files.delete(it.path) }

            LOGGER.info("Deleted $toDelete thumbnails")
        }
    }

    suspend fun createThumbnail(
        originalFile: File,
        mimeType: String,
        fileCode: String
    ): File? {
        if (!SUPPORTED_MIMETYPES.contains(mimeType)) return null

        return try {
            val t1 = System.currentTimeMillis()
            val thumbnail = THUMBNAIL_PATH.resolve("$fileCode.png").toFile()
            Thumbnails.of(originalFile)
                .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                .size(320, 480)
                .outputFormat("png")
                .toFile(thumbnail)
            val t2 = System.currentTimeMillis()
            LOGGER.info("Generated thumbnail for file $fileCode (took ${t2 - t1}ms)")

            mutex.withReentrantLock {
                thumbnails.add(ThumbnailContainer(Instant.now(), thumbnail.toPath()))
                clearOldestFiles()
            }
            thumbnail
        } catch (e: UnsupportedFormatException) {
            LOGGER.info("Failed to generate thumbnail for file $fileCode: ${e.message}")
            null
        }
    }

    suspend fun getThumbnail(
        requesterId: UUID?,
        code: String
    ): File? = Connection.transaction {
        if (requesterId == null) return@transaction null

        val file = HederaFile.findByCode(code) ?: throw FileNotFoundException()
        if (file.ownerId != requesterId) throw FileNotFoundException()

        val thumbnail = THUMBNAIL_PATH.resolve("${file.code}.png").toFile()
        if (!thumbnail.exists()) {
            val originalFile = DiskFileService.getOrNull(requesterId, code) ?: throw FileNotFoundException()
            return@transaction createThumbnail(originalFile, file.mimeType, file.code)
        }

        thumbnail
    }

}
