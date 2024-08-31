package fr.alexis_hery.artexplorer.storage.utility.file

import android.content.Context
import org.json.JSONException
import org.json.JSONObject

abstract class JSONFileStorage<T>(context: Context, name: String) : FileStorage<T>(context, name, "json") {
    protected abstract fun objectToJson(id: Int, obj: T): JSONObject
    protected abstract fun jsonToObject(json: JSONObject): T

    override fun dataToString(data: HashMap<Int, T>): String {
        val json = JSONObject()
        data.forEach{pair -> json.put("${pair.key}", objectToJson(pair.key, pair.value))}
        return json.toString()
    }

    override fun stringToData(value: String): HashMap<Int, T> {
        val data = HashMap<Int, T>()
        try {
            val jsonObject = JSONObject(value)
            val dataArray = jsonObject.getJSONArray("Data")
            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val id = item.getInt("id")
                data[id] = jsonToObject(item)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return data
    }

}