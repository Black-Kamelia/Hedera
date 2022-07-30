package com.kamelia.jellyfish.util

import com.kamelia.jellyfish.core.UploadCodeGenerationException
import com.kamelia.jellyfish.rest.file.Files
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.io.File
import java.nio.file.Path
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileUtils {

    private val UPLOAD_PATH = Path.of("upload")

    init {
        java.nio.file.Files.createDirectories(UPLOAD_PATH)
    }

    fun getOrNull(owner: UUID, code: String): File? {
        val file = UPLOAD_PATH.resolve(owner.toString()).resolve(code).toFile()
        return if (file.exists()) file else null
    }

    suspend fun write(
        owner: UUID,
        file: PartData.FileItem
    ): Triple<String, String, Long> = withContext(Dispatchers.IO) {
        val fileBytes = file.streamProvider().readBytes()
        val fileCode = generateUniqueCode()
        val directory = UPLOAD_PATH.resolve(owner.toString())
        java.nio.file.Files.createDirectories(directory)
        val filePath = directory.resolve(fileCode)
        java.nio.file.Files.write(filePath, fileBytes)
        val fileMimeType = MimeTypes.typeFromFile(file.originalFileName!!)
        val fileSize = java.nio.file.Files.size(filePath)
        Triple(fileCode, fileMimeType, fileSize)
    }

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
