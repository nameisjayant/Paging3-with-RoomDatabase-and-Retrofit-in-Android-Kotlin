package com.example.roomdatabasewithpaging3.Data.Repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.roomdatabasewithpaging3.Data.Database.Database
import com.example.roomdatabasewithpaging3.Data.Dogs
import com.example.roomdatabasewithpaging3.Data.RemoteKeys
import com.example.roomdatabasewithpaging3.Network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
@ExperimentalPagingApi
class DogsRemoteMediator constructor(
        private val db:Database,
        private val apiService: ApiService
)  : RemoteMediator<Int,Dogs>() {
    private val STARTING_PAGE_INDEX = 1

    override suspend fun initialize(): InitializeAction {
       return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Dogs>): MediatorResult {
        val pageKeyData = getKeyPageData(loadType, state)
        val page  = when(pageKeyData){
            is MediatorResult.Success ->{
                return pageKeyData
            }else->{
                pageKeyData as Int
            }
        }

      try {
          val response = apiService.getAllDogs(page,state.config.pageSize)
          val endOfList = response.isEmpty()
          db.withTransaction {
              if(loadType == LoadType.REFRESH){
                  db.remoteKeyDao().clearAll()
                  db.getDao().clearAll()
              }
              val prevKey = if (page == STARTING_PAGE_INDEX) null else page-1
              val nextKey = if(endOfList) null else page+1
              val keys = response.map {
                  RemoteKeys(it.id,prevKey,nextKey)
              }
              db.remoteKeyDao().insertRemote(keys)
              db.getDao().insert(response)
          }
         return MediatorResult.Success(endOfPaginationReached = endOfList)
      }catch (e:IOException){
       return   MediatorResult.Error(e)
      }catch (e:HttpException){
          return MediatorResult.Error(e)
      }
    }


    private suspend fun getKeyPageData(loadType: LoadType,state: PagingState<Int, Dogs>) : Any{
        return when(loadType){
            LoadType.REFRESH->{
                val remoteKeys = getRefreshRemoteKey(state)
                remoteKeys?.nextKey?.minus(1)?:STARTING_PAGE_INDEX
            }
            LoadType.PREPEND->{
                val remoteKeys = getFirstRemoteKey(state)
               val prevKey = remoteKeys?.prevKey ?:MediatorResult.Success(
                    endOfPaginationReached = false
                )
                prevKey
            }
            LoadType.APPEND->{
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey ?:MediatorResult.Success(
                    endOfPaginationReached = true
                )
                nextKey
            }
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, Dogs>) : RemoteKeys? {
        return withContext(Dispatchers.IO){
            state.pages
                .firstOrNull { it.data.isNotEmpty() }
                ?.data?.firstOrNull()
                ?.let { dog -> db.remoteKeyDao().getRemoteKeys(dog.id)}
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, Dogs>) : RemoteKeys? {
        return withContext(Dispatchers.IO){
            state.pages
                .lastOrNull{it.data.isNotEmpty()}
                ?.data?.lastOrNull()
                ?.let { dog -> db.remoteKeyDao().getRemoteKeys(dog.id) }
        }
    }

    private suspend fun getRefreshRemoteKey(state: PagingState<Int, Dogs>) : RemoteKeys? {
        return withContext(Dispatchers.IO){
            state.anchorPosition?.let { position->
                state.closestItemToPosition(position)?.id?.let {repId->
                    db.remoteKeyDao().getRemoteKeys(repId)
                }
            }
        }
    }

}
