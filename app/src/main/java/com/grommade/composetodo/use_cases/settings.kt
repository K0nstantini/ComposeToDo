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


interface UpdateSettings {
    suspend operator fun invoke(settings: Settings)
}


class UpdateSettingsImpl @Inject constructor(
    private val repo: Repository
) : UpdateSettings {

    override suspend fun invoke(settings: Settings) {
        when (settings.id) {
            1 -> repo.updateSettings(settings)
            else -> throw IllegalArgumentException("Settings isn't initialised")
        }
    }

}