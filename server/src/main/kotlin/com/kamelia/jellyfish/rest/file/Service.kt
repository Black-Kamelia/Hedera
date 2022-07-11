package com.kamelia.jellyfish.rest.file

import java.nio.file.Files as NIOFiles
import com.kamelia.jellyfish.core.UploadCodeGenerationException
import com.kamelia.jellyfish.rest.user.User
import com.kamelia.jellyfish.util.ErrorDTO
import com.kamelia.jellyfish.util.QueryResult
import com.kamelia.jellyfish.util.random
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import java.net.URLConnection
import java.nio.file.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FileService {

    private val UPLOAD_PATH = Path.of("upload")

    init {
        NIOFiles.createDirectories(UPLOAD_PATH)
    }

    suspend fun handleFile(part: PartData.FileItem, creator: User): QueryResult<FileRepresentationDTO, List<ErrorDTO>> {
        val fileName = part.originalFileName!!
        val fileCode = generateUniqueCode()// + "." + Path.of(fileName).extension
        val fileBytes = part.streamProvider().readBytes()

        return withContext(Dispatchers.IO) {
            val directory = UPLOAD_PATH.resolve(creator.id.toString())
            NIOFiles.createDirectories(directory)
            val filePath = directory.resolve(fileCode)
            NIOFiles.write(filePath, fileBytes)
            val fileMimeType = URLConnection.guessContentTypeFromName(fileName) ?: "application/octet-stream"
            val fileSize = NIOFiles.size(filePath)

            QueryResult.ok(Files.create(
                code = fileCode,
                name = fileName,
                mimeType = fileMimeType,
                size = fileSize,
                creator = creator
            ).toRepresentationDTO())
        }
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
