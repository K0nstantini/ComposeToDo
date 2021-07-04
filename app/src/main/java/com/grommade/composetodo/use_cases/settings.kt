package com.grommade.composetodo.use_cases

import com.grommade.composetodo.data.entity.Settings
import com.grommade.composetodo.data.repos.RepoSettings
import javax.inject.Inject

interface GetSettings {
    suspend operator fun invoke(): Settings
}


class GetSettingsImpl @Inject constructor(
    private val repoSettings: RepoSettings
) : GetSettings {

    override suspend fun invoke(): Settings {
        return when (val set = repoSettings.getSettings()) {
            emptyList<Settings>() -> throw Exception("Settings isn't initialised")
            else -> set.first()
        }
    }

}

interface UpdateSettings {
    suspend operator fun invoke(settings: Settings)
}


class UpdateSettingsImpl @Inject constructor(
    private val repoSettings: RepoSettings
) : UpdateSettings {

    override suspend fun invoke(settings: Settings) {
        when (settings.id) {
            1 -> repoSettings.updateSettings(settings)
            else -> throw Exception("Settings isn't initialised")
        }
    }

}