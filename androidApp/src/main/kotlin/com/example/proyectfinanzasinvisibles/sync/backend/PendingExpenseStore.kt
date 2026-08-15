package com.example.proyectfinanzasinvisibles.sync.backend

import android.content.Context
import com.example.proyectfinanzasinvisibles.backend.data.Gasto
import org.json.JSONArray
import org.json.JSONObject

/** Cola pequeña y privada para conservar gastos pendientes aunque Android cierre el proceso. */
object PendingExpenseStore {
    private const val PREFERENCES = "pending_expenses"
    private const val KEY = "items"

    @Synchronized
    fun save(context: Context, gasto: Gasto) {
        val items = getAll(context).associateBy { it.id }.toMutableMap()
        items[gasto.id] = gasto
        write(context, items.values.sortedBy { it.fecha }.takeLast(200))
    }

    @Synchronized
    fun remove(context: Context, id: String) {
        write(context, getAll(context).filterNot { it.id == id })
    }

    @Synchronized
    fun clear(context: Context) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Synchronized
    fun getAll(context: Context): List<Gasto> {
        val raw = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(Gasto(
                        id = item.getString("id"),
                        descripcion = item.getString("descripcion"),
                        monto = item.getDouble("monto"),
                        categoria = item.getString("categoria"),
                        tipo = item.getString("tipo"),
                        estado = item.optString("estado", "Pendiente"),
                        fecha = item.optLong("fecha", 0L),
                        sincronizado = false
                    ))
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun write(context: Context, gastos: List<Gasto>) {
        val array = JSONArray()
        gastos.forEach { gasto ->
            array.put(JSONObject().apply {
                put("id", gasto.id)
                put("descripcion", gasto.descripcion)
                put("monto", gasto.monto)
                put("categoria", gasto.categoria)
                put("tipo", gasto.tipo)
                put("estado", gasto.estado)
                put("fecha", gasto.fecha)
            })
        }
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }
}
