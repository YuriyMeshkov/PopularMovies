package com.jobc.popularmoviestest.data.movies.repository

import android.graphics.Bitmap
import com.jobc.popularmoviestest.data.movies.internalmemory.MoviesDataSourceMemory
import com.jobc.popularmoviestest.data.movies.model.movie.ListMovies
import com.jobc.popularmoviestest.data.movies.utils.Result

class RepositoryMoviesInternalMemory {

    private val dataSource = MoviesDataSourceMemory()

    suspend fun writeDataResultToMemory(dataForWrite: String, pathName: String, fileName: String) {
        dataSource.writeDataResultToMemory(dataForWrite, pathName, fileName)
    }

    suspend fun writePoster(
        bitmap: Bitmap,
        fileName: String,
        filePath: String
    ) {
        dataSource.writePoster(bitmap, fileName, filePath)
    }

    suspend fun loadingPoster(
        fileName: String,
        filePath: String
    ) : Result<Bitmap> =
        dataSource.loadingPoster(fileName, filePath)

    suspend fun loadingDataFromMemory(path: String, nameFile: String) : Result<ListMovies> =
        dataSource.loadingMoviesFromMemory(path, nameFile)

    suspend fun loadingJsonStringFromMemory(
        path: String,
        nameFile: String
    ) : Result<String> = dataSource.loadingJsonStringFromMemory(path, nameFile)

    suspend fun deleteAllFilesFromDir(pathPosters: String) {
        dataSource.deleteAllFilesFromDir(pathPosters)
    }
}