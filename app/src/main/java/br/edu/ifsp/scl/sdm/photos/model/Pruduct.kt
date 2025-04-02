package br.edu.ifsp.scl.sdm.photos.model

data class Pruduct(
    val albumId: Int,
    val id: Int,
    val thumbnailUrl: String,
    val title: String,
    val url: String
) {
    override fun toString(): String {
        return title
    }
}