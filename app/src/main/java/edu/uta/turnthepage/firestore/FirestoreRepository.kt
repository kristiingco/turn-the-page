package edu.utexas.turnthepage.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utexas.turnthepage.model.Book
import edu.utexas.turnthepage.model.BookStatus

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

    fun saveBookStatus(book: Book, status: BookStatus, onComplete: (Boolean) -> Unit) {
        val bookId = "${book.title}|${book.author}"

        val bookMap = hashMapOf(
            "title" to book.title,
            "author" to book.author,
            "coverId" to book.coverId,
            "status" to status.name
        )

        firestore
            .collection("users")
            .document(userId)
            .collection("books")
            .document(bookId)
            .set(bookMap)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
