package edu.utexas.turnthepage.model

data class Review(
    val userId: String = "",
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
