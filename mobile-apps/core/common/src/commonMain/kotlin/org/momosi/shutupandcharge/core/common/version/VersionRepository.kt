package org.momosi.shutupandcharge.core.common.version

import org.momosi.shutupandcharge.core.common.version.model.ServerVersion

interface VersionRepository {

    suspend fun getServerVersion(): Result<ServerVersion>
}
