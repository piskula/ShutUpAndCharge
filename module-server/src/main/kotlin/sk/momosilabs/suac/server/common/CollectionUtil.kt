package sk.momosilabs.suac.server.common

inline fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R): Set<R> = mapTo(HashSet(), transform)
