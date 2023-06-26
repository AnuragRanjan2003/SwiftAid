package com.hackfest.swiftaid.di

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.models.LocationLiveData
import com.hackfest.swiftaid.models.NearestAmbulanceData
import com.hackfest.swiftaid.repository.OSMApi
import com.hackfest.swiftaid.repository.OSMInterface
import com.hackfest.swiftaid.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Singleton
    @Provides
    fun provideDatabase(app: Application): FirebaseDatabase = FirebaseDatabase.getInstance(
        app.getString(
            R.string.firebaseUrl
        )
    )

    @Singleton
    @Provides
    fun provideFUser(mAuth: FirebaseAuth): FirebaseUser? = mAuth.currentUser

    @Singleton
    @Provides
    fun OSMApi(): OSMApi = OSMApi

    @Provides
    @Singleton
    fun providesOSMApi(api: OSMApi): OSMInterface = api.instance

    @Provides
    @Singleton
    fun provideRepository(auth : FirebaseAuth , database: FirebaseDatabase,api: OSMInterface) : Repository = Repository(auth, database,api)

    @Provides
    fun provideLiveData(app : Application) : LocationLiveData = LocationLiveData(app)

    @Provides
    @Singleton
    fun provideNearestAmbulanceData(auth: FirebaseAuth,database: FirebaseDatabase) : NearestAmbulanceData = NearestAmbulanceData(auth, database)


}