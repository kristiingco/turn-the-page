package edu.utexas.turnthepage.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utexas.turnthepage.model.Book
import edu.utexas.turnthepage.model.BookStatus
import edu.utexas.turnthepage.model.Goal
import edu.utexas.turnthepage.model.Review

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")


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


    fun submitReview(review: Review, onComplete: (Boolean) -> Unit) {
        val bookId = "${review.bookTitle}|${review.bookAuthor}"

        firestore
            .collection("books")
            .document(bookId)
            .collection("reviews")
            .document(review.userId)
            .set(review)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun getReviewsForBook(book: Book, onComplete: (List<Review>) -> Unit) {
        val bookId = "${book.title}|${book.author}"

        firestore
            .collection("books")
            .document(bookId)
            .collection("reviews")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.mapNotNull { doc ->
                    val rating = doc.getLong("rating")?.toInt() ?: return@mapNotNull null
                    val comment = doc.getString("comment") ?: ""
                    val userId = doc.getString("userId") ?: "anon"
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    Review(userId, book.title, book.author, rating, comment, timestamp)
                }
                onComplete(reviews)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun getUserReviewForBook(book: Book, userId: String, onComplete: (Review?) -> Unit) {
        val bookId = "${book.title}|${book.author}"

        firestore
            .collection("books")
            .document(bookId)
            .collection("reviews")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val rating = doc.getLong("rating")?.toInt() ?: 0
                    val comment = doc.getString("comment") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    onComplete(
                        Review(userId, book.title, book.author, rating, comment, timestamp)
                    )
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { onComplete(null) }
    }

    fun setGoal(goal: Goal, onComplete: (Boolean) -> Unit) {
        firestore
            .collection("users")
            .document(userId)
            .collection("meta")
            .document("goal")
            .set(goal)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getGoal(onComplete: (Goal?) -> Unit) {
        firestore
            .collection("users")
            .document(userId)
            .collection("meta")
            .document("goal")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val targetCount = doc.getLong("targetCount")?.toInt() ?: 0
                    val year = doc.getLong("year")?.toInt() ?: 2025
                    onComplete(Goal(userId, targetCount, year))
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { onComplete(null) }
    }


}
