package sk.momosilabs.suac.server.transaction.temporary.service.getTransactionList

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.momosilabs.suac.server.common.IsAdmin
import sk.momosilabs.suac.server.transaction.temporary.model.TransactionTemporary
import sk.momosilabs.suac.server.transaction.temporary.persistence.TransactionTemporaryPersistence

@Service
open class GetTransactionTemporaryList(
    private val transactionPersistence: TransactionTemporaryPersistence,
): GetTransactionTemporaryListUseCase {

    @IsAdmin
    @Transactional(readOnly = true)
    override fun get(pageable: Pageable): Page<TransactionTemporary> =
        transactionPersistence.getAll(pageable = pageable)

}
