package fr.alexis_hery.artexplorer


class OeuvreModel(
    val id: Int,
    val image: String ,
    val name: String ,
    val description: String ,
    val type: String ,
    var liked: Boolean
){
    companion object {
        const val ID = "id"
        const val IMAGE = "image"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val TYPE = "type"
        const val LIKED = "liked"
    }

}
