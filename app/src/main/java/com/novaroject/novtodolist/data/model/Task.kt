package com.novaroject.novtodolist.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val completedBy: String? = null,
    val completedByName: String? = null,
    val dueDate: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val ownerId: String = "",
    val priority: Int = 0
)
