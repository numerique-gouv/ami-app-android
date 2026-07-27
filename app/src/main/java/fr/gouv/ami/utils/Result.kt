package fr.gouv.ami.utils

sealed interface Result<out T, out E> {

    data class Success<T>(
        val value: T
    ) : Result<T, Nothing>

    data class Failure<E>(
        val error: E
    ) : Result<Nothing, E>
}