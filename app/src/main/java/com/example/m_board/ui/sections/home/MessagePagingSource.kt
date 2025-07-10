package com.example.m_board.ui.sections.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// TODO: test
class MessagePagingSource : PagingSource<DocumentSnapshot, Message>() {
    private val TAG = this::class.simpleName

    companion object {
        const val PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Message> {
        try {
            val key = params.key
            val currentQuery = Firebase.firestore
                .collection(HomeViewModel.BOARD_PATH)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .let {
                    if (key == null) it
                    else it.startAfter(key)
                }
                .limit(PAGE_SIZE.toLong()).get().await()

            val data = currentQuery.documents.mapNotNull { it.toObject(Message::class.java) }

//            Log.d(TAG,"data = ${GsonBuilder().setPrettyPrinting().create().toJson(currentQuery.documents)}")

            return LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = currentQuery.documents.lastOrNull()

            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Message>): DocumentSnapshot? {
        return null
    }
}