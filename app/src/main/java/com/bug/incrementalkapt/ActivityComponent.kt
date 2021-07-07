package com.bug.incrementalkapt

import dagger.Component
import javax.inject.Singleton

@[
    Singleton Component(
        modules = [
            GeneratedMultiBindModule::class
        ]
    )
]
interface ActivityComponent {
    @Component.Factory
    interface Factory {
        fun create(): ActivityComponent
    }
}