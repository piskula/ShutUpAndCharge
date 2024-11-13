package sk.momosilabs.suac.server.account.service.setUserVerifiedFlag

interface SetUserVerifiedFlagUseCase {

    fun setFlag(accountId: Long, isVerified: Boolean): Boolean

}
