package sk.momosilabs.suac.server.common

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('MOMO_USER')")
annotation class IsUser

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole('MOMO_ADMIN')")
annotation class IsAdmin
