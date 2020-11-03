package com.slack.exercise

import android.content.Context
import java.io.*

class FileManager {

    lateinit var outputStream: FileOutputStream
    lateinit var inputStream: InputStream


    fun writeToFile(context: Context, fileName: String?, data: String) {
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_APPEND)
            outputStream.write(data.toByteArray())
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFromFile(context: Context, fileResource: Int): List<String> {
        var wordList = ArrayList<String>()
        inputStream = context.resources.openRawResource(fileResource)

        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        var nextLine = bufferedReader.readLine()
        while (nextLine != null) {
            nextLine = bufferedReader.readLine()
            wordList.add(nextLine)
        }
        return wordList
    }
}