package fr.gouv.ami.utils.storage

import android.hardware.biometrics.BiometricPrompt
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.UserNotAuthenticatedException
import java.io.IOException
import java.security.UnrecoverableKeyException
import javax.crypto.AEADBadTagException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

sealed interface LocalStorageError {

    data object KeyNotFound : LocalStorageError

    data object AuthenticationRequired : LocalStorageError

    data object AuthenticationFailed : LocalStorageError

    data object AuthenticationCancelled : LocalStorageError

    data object BiometryNotAvailable : LocalStorageError

    data object BiometryNotEnrolled : LocalStorageError

    data object DeviceLocked : LocalStorageError

    data object TooManyAttempts : LocalStorageError

    data object KeyInvalidated : LocalStorageError

    data object DecryptionFailed : LocalStorageError

    data object DataStoreUnavailable : LocalStorageError

    data object DataStoreCorrupted : LocalStorageError

    data class Unknown(val cause: Throwable? = null) : LocalStorageError
}

fun LocalStorageError(exception: Throwable): LocalStorageError =
    when (exception) {

        is IOException ->
            LocalStorageError.DataStoreUnavailable

        is UserNotAuthenticatedException ->
            LocalStorageError.AuthenticationRequired

        is KeyPermanentlyInvalidatedException ->
            LocalStorageError.KeyInvalidated

        is UnrecoverableKeyException ->
            LocalStorageError.KeyNotFound

        is AEADBadTagException,
        is BadPaddingException,
        is IllegalBlockSizeException ->
            LocalStorageError.DecryptionFailed

        else ->
            LocalStorageError.Unknown(exception)
    }

fun BiometricError(errorCode: Int): LocalStorageError =
    when (errorCode) {

        // ----- Cancel -----

        BiometricPrompt.BIOMETRIC_ERROR_CANCELED,
        BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED ->
            LocalStorageError.AuthenticationCancelled


        // ----- Failed / Lockout -----

        BiometricPrompt.BIOMETRIC_ERROR_TIMEOUT ->
            LocalStorageError.TooManyAttempts

        BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT,
        BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT ->
            LocalStorageError.DeviceLocked

        BiometricPrompt.BIOMETRIC_ERROR_NO_SPACE ->
            LocalStorageError.DataStoreUnavailable

        // ----- No Biometrics -----

        BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS ->
            LocalStorageError.BiometryNotEnrolled

        BiometricPrompt.BIOMETRIC_ERROR_HW_NOT_PRESENT,
        BiometricPrompt.BIOMETRIC_ERROR_HW_UNAVAILABLE,
        BiometricPrompt.BIOMETRIC_ERROR_NOT_ENABLED_FOR_APPS,
        BiometricPrompt.BIOMETRIC_ERROR_UNABLE_TO_PROCESS,
        BiometricPrompt.BIOMETRIC_ERROR_IDENTITY_CHECK_NOT_ACTIVE,
        BiometricPrompt.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
        BiometricPrompt.BIOMETRIC_ERROR_VENDOR ->
            LocalStorageError.BiometryNotAvailable


        // ----- Authentication required -----

        BiometricPrompt.BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL ->
            LocalStorageError.AuthenticationRequired


        // ----- Fallback -----

        else ->
            LocalStorageError.Unknown()
    }
