package com.shabelnikd.noteapp.models

import com.google.firebase.Timestamp

data class Message(
    val message: String,
    val timestamp: Timestamp?,
    val docId: String
)