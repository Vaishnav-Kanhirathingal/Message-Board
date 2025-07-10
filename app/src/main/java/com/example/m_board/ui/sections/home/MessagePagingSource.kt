package com.example.m_board.ui.sections.home

import androidx.paging.PagingSource
import androidx.paging.PagingState

// TODO: test 
class MessagePagingSource(
    val testList: List<Message> // TODO: remove later
) : PagingSource<Int, Message>() {

    companion object {
        const val PAGE_SIZE = 10

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val key = params.key ?: 0
        val startIndex = key * PAGE_SIZE
        val endIndex = ((key + 1) * PAGE_SIZE) - 1
        try {
            val toLoad = testList.subList(
                fromIndex = startIndex,
                toIndex = endIndex
            )
            return LoadResult.Page(
                data = toLoad,
                prevKey = (key - 1).takeUnless { it < 0 },
                nextKey = (key + 1).takeUnless { endIndex > testList.lastIndex }

            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}