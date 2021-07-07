package com.bug.incrementalkapt

import dagger.Module
import dagger.Provides

@Module
object ActivityModule {

    @Provides
    fun providesDummyDependencyOne(): DummyDependencyOne {
        return DummyDependencyOne()
    }
}