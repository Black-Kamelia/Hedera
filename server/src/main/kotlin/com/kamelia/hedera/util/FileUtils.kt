package com.kamelia.hedera.util

import com.kamelia.hedera.core.UploadCodeGenerationException
import com.kamelia.hedera.rest.file.Files
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.io.File
import java.nio.file.Path
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileUtils {

    private val UPLOAD_PATH = Path.of(Environment.uploadFolder)

    init {
        java.nio.file.Files.createDirectories(UPLOAD_PATH)
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

    /**
     * Writes the content of the [file] to the disk.
     * The file name on the disk is generated with a unique code.
     * The file will then be written in `[UPLOAD_PATH]/[owner]/[code]`.
     *
     * @param file the file to write to the disk
     * @param owner the owner of the file
     *
     * @return the code of the file, the mime-type of the file, and the size of the file in bytes
     */
    suspend fun write(
        owner: UUID,
        file: PartData.FileItem,
        filename: String
    ): Triple<String, String, Long> = withContext(Dispatchers.IO) {
        val fileBytes = file.streamProvider().readBytes()
        val fileCode = generateUniqueCode()
        val directory = UPLOAD_PATH.resolve(owner.toString())
        java.nio.file.Files.createDirectories(directory)
        val filePath = directory.resolve(fileCode)
        java.nio.file.Files.write(filePath, fileBytes)
        val fileMimeType = MimeTypes.typeFromFile(filename)
        val fileSize = java.nio.file.Files.size(filePath)
        Triple(fileCode, fileMimeType, fileSize)
    }

    /**
     * Deletes the file with the given [code] and [owner] from the disk.
     *
     * @param owner the owner of the file
     * @param code the code of the file
     */
    fun delete(owner: UUID, code: String) {
        val file = UPLOAD_PATH.resolve(owner.toString()).resolve(code).toFile()
        if (file.exists()) file.delete()
    }

    private suspend fun generateUniqueCode(): String {
        repeat(10) {
            val code = String.random(10)
            if (Files.findByCode(code) == null) {
                return code
            }
        }
        throw UploadCodeGenerationException()
    }
}
