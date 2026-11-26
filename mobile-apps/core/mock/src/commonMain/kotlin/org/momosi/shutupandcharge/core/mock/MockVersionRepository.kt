package org.momosi.shutupandcharge.core.mock

import org.momosi.shutupandcharge.core.common.version.VersionRepository
import org.momosi.shutupandcharge.core.common.version.model.ServerVersion

private const val MOCK_SERVER_VERSION = "demo"

internal class MockVersionRepository(private val backend: MockBackend) : VersionRepository {

    override suspend fun getServerVersion(): Result<ServerVersion> = mockCall { ServerVersion(version = MOCK_SERVER_VERSION, builtAt = backend.now()) }
}
