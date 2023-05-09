package com.master.iot.luzi

import com.master.iot.luzi.domain.MTPetrolRepository
import com.master.iot.luzi.domain.REERepository
import com.master.iot.luzi.domain.TesseractRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun provideREERepository(): REERepository = REERepository()

    @Provides
    fun provideMTPetrolRepository(): MTPetrolRepository = MTPetrolRepository()

    @Provides
    fun provideTesseractRepository(): TesseractRepository = TesseractRepository()

}