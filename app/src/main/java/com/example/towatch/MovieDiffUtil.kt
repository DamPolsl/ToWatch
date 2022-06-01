package com.example.towatch

import androidx.recyclerview.widget.DiffUtil

class MovieDiffUtil(
    private var oldList: List<Movie>,
    private var newList: List<Movie>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.count()
    }

    override fun getNewListSize(): Int {
        return newList.count()
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return when {
            old.id != new.id -> false
            old.title != new.title -> false
            old.year != new.year -> false
            old.poster != new.poster -> false
            old.plot != new.plot -> false
            else -> true
        }
    }

}