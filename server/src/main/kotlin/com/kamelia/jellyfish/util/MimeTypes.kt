package com.kamelia.jellyfish.util

object MimeTypes {

    private lateinit var mimeToExt: MutableMap<String, String>
    private lateinit var extToMime: MutableMap<String, String>

    fun init() {
        mimeToExt = mutableMapOf()
        extToMime = mutableMapOf()
        MimeTypes::class.java.getResource("/mime-type-catalog.csv")!!
            .readText()
            .lines()
            .forEach { line ->
                val (ext, mime) = line.split(",")
                mimeToExt[mime] = ext
                extToMime[ext] = mime
            }
    }

    fun typeFromExt(ext: String): String = extToMime[ext] ?: "application/unknown"

    fun typeFromFile(file: String): String = extToMime
        .entries
        .filter { (ext, _) -> file.lowercase().endsWith(ext) }
        .maxByOrNull { (ext) -> ext.length }
        ?.value
        ?: "application/unknown"

    fun extFromType(type: String): String = mimeToExt[type.lowercase()] ?: ""
}