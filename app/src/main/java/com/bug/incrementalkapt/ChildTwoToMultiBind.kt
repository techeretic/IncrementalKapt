package com.bug.incrementalkapt

import com.bug.annotations.BindInMultiBindModule
import javax.inject.Inject

@BindInMultiBindModule
class ChildTwoToMultiBind @Inject constructor(
    private val dummyDependencyOne: DummyDependencyOne
) : BaseMultiBindType
