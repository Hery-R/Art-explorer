package fr.alexis_hery.artexplorer.storage

import android.content.Context
import fr.alexis_hery.artexplorer.OeuvreModel
import fr.alexis_hery.artexplorer.storage.utility.file.JSONFileStorage
import org.json.JSONObject

class OeuvreJSONFileStorage(context: Context): JSONFileStorage<OeuvreModel>(context, "Data") {
    override fun create(id: Int, obj: OeuvreModel): OeuvreModel {
        return OeuvreModel(id, obj.image, obj.name, obj.description, obj.type, obj.liked)
    }

    override fun objectToJson(id: Int, obj: OeuvreModel): JSONObject {
        val json = JSONObject()
        json.put(OeuvreModel.ID, id)
        json.put(OeuvreModel.IMAGE, obj.image)
        json.put(OeuvreModel.NAME, obj.name)
        json.put(OeuvreModel.DESCRIPTION, obj.description)
        json.put(OeuvreModel.TYPE, obj.type)
        json.put(OeuvreModel.LIKED, obj.liked)
        return json
    }

    override fun jsonToObject(json: JSONObject): OeuvreModel {
        return OeuvreModel(
            json.getInt(OeuvreModel.ID),
            json.getString(OeuvreModel.IMAGE),
            json.getString(OeuvreModel.NAME),
            json.getString(OeuvreModel.DESCRIPTION),
            json.getString(OeuvreModel.TYPE),
            json.getBoolean(OeuvreModel.LIKED)
        )
    }
}