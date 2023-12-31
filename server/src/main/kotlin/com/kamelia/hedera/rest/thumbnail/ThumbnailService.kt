package com.kamelia.hedera.rest.thumbnail

import com.kamelia.hedera.core.FileNotFoundException
import com.kamelia.hedera.database.Connection
import com.kamelia.hedera.rest.file.DiskFileService
import com.kamelia.hedera.util.Environment
import io.ktor.util.logging.*
import io.trbl.blurhash.BlurHash
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.logging.Logger
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.resizers.configurations.ScalingMode
import net.coobird.thumbnailator.tasks.UnsupportedFormatException
import javax.imageio.ImageIO
import com.kamelia.hedera.rest.file.File as HederaFile

object ThumbnailService {

    private val LOGGER = KtorSimpleLogger("ThumbnailService")
    private val THUMBNAIL_PATH = Path.of(Environment.thumbnailFolder)

    init {
        Files.createDirectories(THUMBNAIL_PATH)
    }

    fun getBlurhashOrNull(
        file: File?
    ): String? {
        if (file == null) return null
        val bufferedImage = ImageIO.read(file)
        return BlurHash.encode(bufferedImage, 6, 4)
    }

    fun createThumbnail(
        originalFile: File,
        mimeType: String,
        fileCode: String
    ): File? = when (mimeType) {
        "image/png",
        "image/gif",
        "image/jpeg" -> try {
            val t1 = System.currentTimeMillis()
            val thumbnail = THUMBNAIL_PATH.resolve("$fileCode.png").toFile()
            Thumbnails.of(originalFile)
                .scalingMode(ScalingMode.PROGRESSIVE_BILINEAR)
                .size(320, 480)
                .outputFormat("png")
                .toFile(thumbnail)
            val t2 = System.currentTimeMillis()
            LOGGER.info("Generated thumbnail for file $fileCode (took ${t2 - t1}ms)")

            thumbnail
        } catch (e: UnsupportedFormatException) {
            LOGGER.info("Failed to generate thumbnail for file $fileCode: ${e.message}")
            null
        }

        else -> null
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