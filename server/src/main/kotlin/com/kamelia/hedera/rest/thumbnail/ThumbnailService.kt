package com.kamelia.hedera.rest.thumbnail

import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.core.constant.Actions
import com.kamelia.hedera.core.response.ActionResponse
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
import kotlin.io.path.createDirectories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    //private val thumbnails = sortedSetOf<ThumbnailContainer>()
    private val thumbnails = mutableMapOf<UUID, TreeSet<ThumbnailContainer>>()

    suspend fun init() = withContext(Dispatchers.IO) {
        mutex.withReentrantLock {
            Files.createDirectories(THUMBNAIL_PATH.createDirectories())
            Files.list(THUMBNAIL_PATH).forEach {
                val userId = UUID.fromString(it.fileName.toString())
                runBlocking {
                    Files.list(it).forEach { thumbnail ->
                        val creationDate = Instant.ofEpochMilli(thumbnail.toFile().lastModified())
                        val userThumbnails = thumbnails.computeIfAbsent(userId) { TreeSet() }
                        userThumbnails.add(ThumbnailContainer(creationDate, thumbnail))
                    }
                }
                launch { clearOldestFiles(userId) }
            }

            val count = thumbnails.values.sumOf { it.size }
            LOGGER.info("Loaded $count thumbnails")
        }
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
                if (Files.isRegularFile(path)) acc + runBlocking { Files.size(path) } else acc
            }, { acc1, acc2 -> acc1 + acc2 })
        }
    }

    suspend fun clearCache(): ActionResponse<Nothing> = withContext(Dispatchers.IO) {
        mutex.withReentrantLock {
            Files.walk(THUMBNAIL_PATH).forEach {
                if (Files.isRegularFile(it)) runBlocking { Files.delete(it) }
            }

            ActionResponse.ok(
                title = Actions.Maintenance.ClearThumbnailCache.success.title,
            )
        }
    }

    suspend fun clearOldestFiles() = withContext(Dispatchers.IO) {
        mutex.withReentrantLock {
            thumbnails.keys.forEach { userId -> clearOldestFiles(userId) }
        }
    }

    private suspend fun clearOldestFiles(ownerId: UUID) = withContext(Dispatchers.IO) {
        mutex.withReentrantLock {
            val maximum = GlobalConfigurationService.currentConfiguration.maximumThumbnailCount

            val userThumbnails = thumbnails[ownerId] ?: return@withReentrantLock
            val toDelete = userThumbnails.size - maximum
            if (toDelete <= 0) return@withReentrantLock

            val deletedThumbnails = userThumbnails.take(toDelete).toSet()
            userThumbnails.removeAll(deletedThumbnails)
            deletedThumbnails.forEach { Files.delete(it.path) }

            LOGGER.info("Deleted $toDelete thumbnails")
        }
    }

    suspend fun createThumbnail(
        originalFile: File,
        ownerId: UUID,
        mimeType: String,
        fileCode: String
    ): File? {
        if (!SUPPORTED_MIMETYPES.contains(mimeType)) return null

        return try {
            val t1 = System.currentTimeMillis()
            val thumbnail = THUMBNAIL_PATH
                .resolve(ownerId.toString())
                .createDirectories()
                .resolve("$fileCode.png")
                .toFile()
            Thumbnails.of(originalFile)
                .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                .size(320, 480)
                .outputFormat("png")
                .toFile(thumbnail)
            val t2 = System.currentTimeMillis()
            LOGGER.info("Generated thumbnail for file $fileCode (took ${t2 - t1}ms)")

            mutex.withReentrantLock {
                val userThumbnails = thumbnails.computeIfAbsent(ownerId) { TreeSet() }
                userThumbnails.add(ThumbnailContainer(Instant.now(), thumbnail.toPath()))
                clearOldestFiles(ownerId)
            }
            thumbnail
        } catch (e: UnsupportedFormatException) {
            LOGGER.info("Failed to generate thumbnail for file $fileCode: ${e.message}")
            null
        }
    }

    suspend fun getThumbnail(
        ownerId: UUID,
        code: String
    ): File? = Connection.transaction {
        val file = HederaFile.findByCode(code) ?: throw FileNotFoundException()
        if (file.ownerId != ownerId) throw FileNotFoundException()

        val thumbnail = THUMBNAIL_PATH
            .resolve(ownerId.toString())
            .resolve("${file.code}.png")
            .toFile()
        if (!thumbnail.exists()) {
            val originalFile = DiskFileService.getOrNull(ownerId, code) ?: throw FileNotFoundException()
            return@transaction createThumbnail(originalFile, ownerId, file.mimeType, file.code)
        }

        thumbnail
    }

}
