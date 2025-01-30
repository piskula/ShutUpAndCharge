package sk.momosilabs.suac.server.account.service.assignChipUid

interface AssignChipUidUseCase {

    fun assignChipUid(accountId: Long, chipUid: String?)

}
