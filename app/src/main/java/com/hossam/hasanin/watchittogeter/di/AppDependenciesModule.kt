package com.hossam.hasanin.watchittogeter.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hossam.hasanin.watchittogeter.dataScources.*
import com.hossam.hasanin.watchittogeter.repositories.MainRepository
import com.hossam.hasanin.watchittogeter.repositories.MainRepositoryImp
import com.hossam.hasanin.watchittogeter.users.UsersUseCases
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ApplicationComponent::class)
object AppDependenciesModule {
    @Provides
    fun bindFireStore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun bindFireAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @NetworkDataSource
    @Provides
    fun bindNetworkDataSource(firestore: FirebaseFirestore , mAuth: FirebaseAuth): DataSource{
        return NetworkDataSourceImp(firestore , mAuth)
    }

    @Provides
    fun bindMainRepo(@NetworkDataSource networkDataSource: DataSource): MainRepository{
        return MainRepositoryImp(networkDataSource)
    }

    @Provides
    fun bindAuthDataSource(mAuth: FirebaseAuth , firestore: FirebaseFirestore): AuthenticationDataSource{
        return AuthenticationDataSourceImp(mAuth , firestore)
    }

}
