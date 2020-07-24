package com.hossam.hasanin.base.dataScources

import androidx.room.*
import com.hossam.hasanin.base.models.User
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface UserCashDataSource {
    @Insert(onConflict = OnConflictStrategy.REPLACE , entity = User::class)
    fun setCurrentUser(user: User): Completable

    @Query("SELECT * FROM users WHERE id = :id")
    fun getCurrentUser(id: String): Maybe<User>

    @Delete
    fun deleteCurrentUser(user: User): Completable

}