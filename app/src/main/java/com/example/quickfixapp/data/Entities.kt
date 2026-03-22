package com.arijeet.quickfixapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "categories"
)
data class Category(
    @PrimaryKey val categoryId: String,
    val categoryName: String,
    val iconRes: Int
)

@Entity(
    tableName = "issues",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["categoryId"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["categoryId"])]
)
data class Issue(
    @PrimaryKey val issueId: String,
    val categoryId: String,
    val title: String,
    val cause: String,
    val steps: List<String>,
    val settingsIntent: String? = null
)

@Entity(
    tableName = "history",
    foreignKeys = [ForeignKey(
        entity = Issue::class,
        parentColumns = ["issueId"],
        childColumns = ["issueId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["issueId"])]
)
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val issueId: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
