package com.bagaspardanailham.notein.di

import android.content.Context
import com.bagaspardanailham.notein.data.local.NoteInDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideNoteInDatabase(@ApplicationContext context: Context): NoteInDatabase =
        NoteInDatabase.getDatabase(context)

}