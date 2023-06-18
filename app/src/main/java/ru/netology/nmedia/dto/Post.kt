package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
) {
    fun getNumberToString(number: Int): String {

        val countStr = number.toString().count() / 3.0
        val remDivisionK = (number / 1_000.0).toString().split(".")[1][0].toString().toInt()
        val remDivisionM = if(number > 1_000_000) (number / 1_000_000.0).toString().split(".")[1][0].toString().toString().toInt() else 0

        return when {
            0 < countStr && countStr <= 1 -> "$number"
            1 < countStr && countStr <= 2 -> "${number / 1_000}${if (remDivisionK > 0 && number < 10_000) ",$remDivisionK" else ""}K"
            else -> "${number / 1_000_000}${if (remDivisionM > 0 && number > 1_000_000) ",$remDivisionM" else ""}M"
        }

    }

    fun postVideoVisibility(video: String?): Int {
        return if (video == null) 0 else 1
    }

}

