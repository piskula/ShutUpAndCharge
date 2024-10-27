package sk.momosilabs.suac.server.notification.service

interface NotificationAccountPersistence {

    fun isAdmin(userId: String): Boolean
    fun getAssignedCompanyIds(userId: String): Set<Long>

}
