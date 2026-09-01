package com.example.listenin.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaRepository {

    private data class Spec(
        val collection: android.net.Uri,
        val titleColumn: String,
        val subtitleColumn: String
    )

    private fun spec(type: MediaType): Spec = when (type) {
        MediaType.AUDIO -> Spec(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        MediaType.VIDEO -> Spec(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.RESOLUTION
        )
    }

    suspend fun loadMedia(context: Context, type: MediaType): List<MediaItem> =
        withContext(Dispatchers.IO) {
            val spec = spec(type)
            query(context, type, spec.collection, spec.titleColumn, spec.subtitleColumn)
        }

    suspend fun loadItem(context: Context, type: MediaType, id: Long): MediaItem? =
        withContext(Dispatchers.IO) {
            val spec = spec(type)
            query(
                context = context,
                type = type,
                collection = spec.collection,
                titleColumn = spec.titleColumn,
                subtitleColumn = spec.subtitleColumn,
                selection = "${MediaStore.MediaColumns._ID} = ?",
                selectionArgs = arrayOf(id.toString())
            ).firstOrNull()
        }

    private fun query(
        context: Context,
        type: MediaType,
        collection: android.net.Uri,
        titleColumn: String,
        subtitleColumn: String,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): List<MediaItem> {
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            titleColumn,
            subtitleColumn,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.SIZE
        )
        val sortOrder = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        val items = mutableListOf<MediaItem>()
        context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val titleCol = cursor.getColumnIndex(titleColumn)
            val subtitleCol = cursor.getColumnIndex(subtitleColumn)
            val durationCol = cursor.getColumnIndex(MediaStore.MediaColumns.DURATION)
            val dateCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)
            val sizeCol = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                items += MediaItem(
                    id = id,
                    uri = ContentUris.withAppendedId(collection, id),
                    title = cursor.stringOr(titleCol, "Unknown"),
                    subtitle = cursor.stringOr(subtitleCol, ""),
                    durationMs = cursor.longOr(durationCol),
                    dateAddedSeconds = cursor.longOr(dateCol),
                    sizeBytes = cursor.longOr(sizeCol),
                    type = type
                )
            }
        }
        return items
    }

    private fun android.database.Cursor.stringOr(column: Int, fallback: String): String =
        if (column >= 0 && !isNull(column)) getString(column) ?: fallback else fallback

    private fun android.database.Cursor.longOr(column: Int): Long =
        if (column >= 0 && !isNull(column)) getLong(column) else 0L
}
