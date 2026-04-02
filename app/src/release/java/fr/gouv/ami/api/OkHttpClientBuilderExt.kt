package fr.gouv.ami.api

import okhttp3.OkHttpClient

internal fun OkHttpClient.Builder.applyDebugSslConfig(): OkHttpClient.Builder = this
