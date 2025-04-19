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

    fun getUserBooks(onComplete: (List<Pair<Book, BookStatus>>) -> Unit) {
        firestore
            .collection("users")
            .document(userId)
            .collection("books")
            .get()
            .addOnSuccessListener { snapshot ->
                val result = snapshot.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val author = doc.getString("author") ?: return@mapNotNull null
                    val coverId = doc.getLong("coverId")?.toInt()
                    val statusStr = doc.getString("status") ?: return@mapNotNull null
                    val status = BookStatus.valueOf(statusStr)
                    Pair(Book(title, author, coverId), status)
                }
                onComplete(result)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

}
