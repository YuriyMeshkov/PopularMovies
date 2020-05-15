package com.jobc.popularmoviestest.data.movies.internalmemory
import com.jobc.popularmoviestest.data.movies.utils.Result
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jobc.popularmoviestest.data.movies.model.movie.ListMovies
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.*
import java.lang.Exception

class MoviesDataSourceMemory {

    suspend fun writeDataResultToMemory(dataForWrite: String, pathName: String, fileName: String) {
        withContext(Dispatchers.IO) {
            val path = File(pathName)
            val pathFile = File(path, fileName)
            if (!path.exists()) {
                path.mkdirs()
            }
            var bf: BufferedWriter? = null
            try {
                bf = pathFile.bufferedWriter()
                bf.write(dataForWrite)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                bf?.close()
            }
        }
    }

    suspend fun writePoster(
        bitmap: Bitmap,
        fileName: String,
        filePath: String
    ) {
        withContext(Dispatchers.IO) {
            val path = File(filePath)

            if (!path.exists()) {
                path.mkdirs()
            }

            val myPath = File(path, fileName)
            if (myPath.exists()) {
                myPath.delete()
            }
            var fos: FileOutputStream? = null

            try {
                fos = myPath.outputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                fos?.close()
            }
            Result.Success("Ok")
        }
    }

    suspend fun loadingPoster(
        fileName: String,
        filePath: String
    ): Result<Bitmap> {
        return withContext(Dispatchers.IO) {
            val path = File(filePath, fileName)
            val fileInputStream: FileInputStream? = null

            try {
                val fis = path.inputStream()
                val bitmap = BitmapFactory.decodeStream(fis)
                Result.Success(bitmap)
            } catch (e: Exception) {
                Result.Error(e.message.toString())
            }
            finally {
                fileInputStream?.close()
                if (filePath.contains(fileName) && path.exists()) {
                    path.delete()
                }
            }
        }
    }

    suspend fun loadingJsonStringFromMemory(path: String, nameFile: String) : Result<String> {
        return withContext(Dispatchers.IO) {
            val pathFile = File(path, nameFile)
            when (!pathFile.exists()) {
                true -> {
                    Result.Error("error")
                }
                false -> {
                    var br: BufferedReader? = null
                    try {
                        br = pathFile.bufferedReader()
                        Result.Success(br.readText())
                    } catch (e: Exception) {
                        Result.Error("error")
                    } finally {
                        br?.close()
                    }
                }
            }
        }
    }

    suspend fun loadingMoviesFromMemory(path: String, nameFile: String) : Result<ListMovies> {
        val resultJsonString = loadingJsonStringFromMemory(path, nameFile)
        return when(resultJsonString is Result.Success) {
            true -> {
                try {
                    val result = Gson().fromJson(resultJsonString.data, ListMovies::class.java)
                    Result.Success(result)
                } catch (e: JSONException) {
                    Result.Error(e.message.toString())
                }
            }
            false -> {
                Result.Error("error")
            }
        }
    }

    suspend fun deleteAllFilesFromDir(pathPosters: String) {
        withContext(Dispatchers.IO) {
            val pathFile = File(pathPosters)
            if (pathFile.exists()) {
                val files = pathFile.listFiles()
                files?.forEach {
                    it.delete()
                }
            }
        }
    }
}