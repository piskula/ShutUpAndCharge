package org.momosi.shutupandcharge.core.common.version

import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.onNetworkFailure
import org.momosi.shutupandcharge.core.common.network.onNoConnectionFailure
import org.momosi.shutupandcharge.core.common.version.api.VersionApi
import org.momosi.shutupandcharge.core.common.version.dto.toServerVersion
import org.momosi.shutupandcharge.core.common.version.model.ServerVersion

private const val TAG = "VersionRepository"

class VersionRepositoryImpl(private val versionApi: VersionApi) : VersionRepository {

    override suspend fun getServerVersion(): Result<ServerVersion> = runCatchingCancellable {
        versionApi.getBuildInfo().toServerVersion()
    }
        .onNoConnectionFailure { Logger.w(TAG, it, "Failed to get server version (offline)") }
        .onNetworkFailure { Logger.e(TAG, it, "Failed to get server version") }
}
