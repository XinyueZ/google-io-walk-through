package com.demo.workmanager.ext

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

fun <FRG : Fragment> KClass<FRG>.newInstance(
    context: Context?,
    args: Bundle? = Bundle.EMPTY
) =
    context?.run {
        (Fragment.instantiate(
            context,
            java.name
        ) as FRG).apply { arguments = args }
    } ?: kotlin.run { throw NullPointerException("[Context] cannot be null.") }