package fr.gouv.ami.data.dto

data class NotificationDto(
    var id: String,
    var userId: String,
    var message: String,
    var sender: String?,
    var title: String?,
    var unread: Boolean,
    var date: String,
    var isNotified: Boolean
)
