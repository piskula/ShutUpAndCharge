package sk.momosilabs.suac.server.transaction.temporary.controller

import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO
import sk.momosilabs.suac.api.transaction.finished.dto.TransactionFilterDTO
import sk.momosilabs.suac.api.transaction.temporary.TransactionTemporaryApi
import sk.momosilabs.suac.api.transaction.temporary.dto.TransactionTemporaryDTO

@RestController
class TransactionTemporaryController(
) : TransactionTemporaryApi {

    override fun getList(filter: TransactionFilterDTO, pageable: PageableDTO): PageDTO<TransactionTemporaryDTO> {
        TODO("Not yet implemented")
    }

}
