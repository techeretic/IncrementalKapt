package com.bug.incrementalkapt

import dagger.MapKey
import kotlin.reflect.KClass


@[
Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
Retention(value = AnnotationRetention.RUNTIME)
MapKey
]
annotation class MultiBindKey(val value: KClass<out BaseMultiBindType>)