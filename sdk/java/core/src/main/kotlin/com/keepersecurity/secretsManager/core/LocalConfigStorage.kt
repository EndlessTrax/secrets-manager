package com.keepersecurity.secretsManager.core

import java.io.*

// LocalConfigStorage becomes in memory storage if config name is null
class LocalConfigStorage(configName: String?) : KeyValueStorage {

    private val file = configName?.let { File(it) }
    private val strings: MutableMap<String, String> = HashMap()

    init {
        if (file != null && file.exists()) {
            val inputStream = BufferedReader(FileReader(file))
            inputStream.lines().forEach {
                val kv = it.split(": ")
                strings[kv[0]] = kv[1]
            }
        }
    }

    private fun saveToFile() {
        if (file == null) return
        val outputStream = BufferedWriter(FileWriter(file))
        for (kv in strings) {
            outputStream.write("${kv.key}: ${kv.value}\n")
        }
        outputStream.close()
    }

    override fun getString(key: String): String? {
        return strings[key]
    }

    override fun saveString(key: String, value: String) {
        strings[key] = value
        saveToFile()
    }

    override fun getBytes(key: String): ByteArray? {
        val stringValue = getString(key) ?: return null
        return base64ToBytes(stringValue)
    }

    override fun saveBytes(key: String, value: ByteArray) {
        saveString(key, bytesToBase64(value))
    }

    override fun delete(key: String) {
        strings.remove(key)
        saveToFile()
    }
}