package com.kamelia.hedera.rest.file

import com.kamelia.hedera.core.InsufficientDiskQuotaException
import com.kamelia.hedera.core.UploadCodeGenerationException
import com.kamelia.hedera.rest.user.DiskQuotaService.getDiskQuota
import com.kamelia.hedera.rest.user.User
import com.kamelia.hedera.util.Environment
import com.kamelia.hedera.util.MimeTypes
import com.kamelia.hedera.util.random
import io.ktor.http.content.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kamelia.hedera.rest.file.File as HederaFile

class UploadedFileContainer(
    val code: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val file: File,
)

object DiskFileService {

    private val UPLOAD_PATH = Path.of(Environment.uploadFolder)

    init {
        Files.createDirectories(UPLOAD_PATH)
    }

    /**
     * Retrieves the file with the given [code] and [owner] from the disk, or null.
     *
     * @param code The code of the file to retrieve.
     * @param owner The owner of the file to retrieve.
     *
     * @return The file with the given [code] and [owner] or null.
     */
    fun getOrNull(owner: UUID, code: String): File? {
        val file = UPLOAD_PATH.resolve(owner.toString()).resolve(code).toFile()
        return if (file.exists()) file else null
    }

    suspend fun receiveFile(
        owner: User,
        filePart: PartData.FileItem,
        fileName: String,
    ): UploadedFileContainer = withContext(Dispatchers.IO) {
        val (diskQuota, maximumDiskQuota) = owner.getDiskQuota()
        var tmpDiskQuota = diskQuota

        val fileCode = generateUniqueCode()
        val filePath = Files.createDirectories(UPLOAD_PATH.resolve(owner.id.toString())).resolve(fileCode)

        val inputStream = filePart.streamProvider()
        val outputStream = Files.newOutputStream(filePath)
        val buffer = ByteArray(1024 * 1024 * 10) // 10.0 MB buffer

        do {
            val readBytes = inputStream.readNBytes(buffer, 0, buffer.size)
            tmpDiskQuota += readBytes
            if (tmpDiskQuota >= maximumDiskQuota && maximumDiskQuota != -1L) {
                Files.delete(filePath)
                throw InsufficientDiskQuotaException()
            }
            outputStream.write(buffer, 0, readBytes)
        } while (readBytes != 0)

        outputStream.close()
        inputStream.close()

        UploadedFileContainer(
            code = fileCode,
            name = fileName,
            size = Files.size(filePath),
            mimeType = MimeTypes.typeFromFile(fileName),
            file = filePath.toFile()
        )
    }

    /**
     * Deletes the file with the given [code] and [owner] from the disk.
     *
     * @param owner the owner of the file
     * @param code the code of the file
     * @return true if the file was deleted, false otherwise
     */
    fun delete(owner: UUID, code: String): Boolean {
        val file = UPLOAD_PATH.resolve(owner.toString()).resolve(code).toFile()
        if (file.exists()) return file.delete()
        return false
    }

    private fun generateUniqueCode(): String {
        repeat(10) {
            val code = String.random(10)
            if (HederaFile.findByCode(code) == null) {
                return code
            }
        }
        throw UploadCodeGenerationException()
    }
}
