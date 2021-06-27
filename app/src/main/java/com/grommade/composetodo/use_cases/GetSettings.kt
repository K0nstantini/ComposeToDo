package com.grommade.composetodo.use_cases

import com.grommade.composetodo.Repository
import com.grommade.composetodo.add_classes.ResultOf
import com.grommade.composetodo.db.entity.Settings
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

interface GetSettings {
    suspend operator fun invoke(): Settings
}


class GetSettingsImpl @Inject constructor(
    private val repo: Repository
) : GetSettings {

    override suspend fun invoke(): Settings =
        checkNotNull(repo.getSettings()) { "Settings isn't initialised" }

}