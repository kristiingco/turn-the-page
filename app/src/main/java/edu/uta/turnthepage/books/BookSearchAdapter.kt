package edu.utexas.turnthepage.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.utexas.turnthepage.databinding.RowBookBinding
import edu.utexas.turnthepage.model.Book

class BookSearchAdapter :
    ListAdapter<Book, BookSearchAdapter.BookViewHolder>(BookDiffCallback()) {

    class BookViewHolder(private val binding: RowBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.titleText.text = book.title
            binding.authorText.text = book.author
            Glide.with(binding.coverImage.context)
                .load(book.getCoverUrl())
                .into(binding.coverImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            RowBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
        oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
        oldItem == newItem
}
