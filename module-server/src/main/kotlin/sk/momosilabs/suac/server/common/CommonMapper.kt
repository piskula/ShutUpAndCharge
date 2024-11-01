package sk.momosilabs.suac.server.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import sk.momosilabs.suac.api.common.dto.PageDTO
import sk.momosilabs.suac.api.common.dto.PageableDTO

inline fun <T, U> Page<T>.toDto(transform: (T) -> U): PageDTO<U> =
    PageDTO(
        totalElements = totalElements,
        totalPages = totalPages,
        number = number,
        size = size,
        numberOfElements = numberOfElements,
        content = content.map { transform.invoke(it) },
    )

fun PageableDTO.toModel(): Pageable {
    if (sort.isNullOrBlank())
        return PageRequest.of(page, size)

    val sortByColumns = sort!!.split(";").map { col ->
        col.split(",").let { if (it[1] == "desc") Order.desc(it[0]) else Order.asc(it[0]) }
    }

    return PageRequest.of(page, size, Sort.by(sortByColumns))
}
