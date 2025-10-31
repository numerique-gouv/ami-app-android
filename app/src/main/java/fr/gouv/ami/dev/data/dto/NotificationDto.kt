package fr.gouv.ami.dev.data.dto

data class NotificationDto(
    var userId: Int,
    var message: String,
    var sender: String?,
    var title: String?,
    var id: Int?,
    var date: String?,
    var isNotified: Boolean
)
