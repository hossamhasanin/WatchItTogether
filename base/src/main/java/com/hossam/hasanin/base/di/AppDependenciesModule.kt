package com.hossam.hasanin.base.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.base.MainDataBase
import com.hossam.hasanin.base.dataScources.*
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.base.repositories.AuthRepository
import com.hossam.hasanin.base.repositories.AuthRepositoryImp
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import com.hossam.hasanin.base.repositories.MainRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppDependenciesModule {
    @Singleton
    @Provides
    fun bindFireStore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun bindNavigationManager(): NavigationManager{
        return NavigationManager()
    }

    @Singleton
    @Provides
    fun bindFireAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @NetworkDataSource
    @Provides
    fun bindNetworkDataSource(firestore: FirebaseFirestore , mAuth: FirebaseAuth): DataSource {
        return NetworkDataSourceImp(firestore , mAuth)
    }

    @Provides
    fun bindMainRepo(@NetworkDataSource networkDataSource: DataSource): MainRepository{
        return MainRepositoryImp(networkDataSource)
    }

    @Provides
    fun bindAuthRepo(dataSource: AuthenticationDataSource, userCashDataSource: UserCashDataSource, mAuth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImp(dataSource, userCashDataSource, mAuth)
    }

    @Provides
    fun bindAuthDataSource(mAuth: FirebaseAuth , firestore: FirebaseFirestore): AuthenticationDataSource {
        return AuthenticationDataSourceImp(mAuth , firestore)
    }

    @Provides
    fun bindDatabase(@ApplicationContext context: Context): MainDataBase{
        return Room.databaseBuilder(context , MainDataBase::class.java , "watchTogether").build()
    }

    @Provides
    fun bindUserCashDataSource(mainDataBase: MainDataBase): UserCashDataSource {
        return mainDataBase.cashDao()
    }



}
