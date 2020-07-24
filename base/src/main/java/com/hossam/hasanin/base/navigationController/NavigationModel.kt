package com.hossam.hasanin.base.navigationController

import android.app.Activity
import android.os.Bundle

data class NavigationModel(
    val destination: Int,
    val activity: Activity,
    val data: Bundle
)