package ovh.geoffrey_druelle.nantestoilettes.data.local.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Toilet")
data class Toilet(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var strId: String,
    var name: String,
    var pole: String?,
    var type: String?,
    var schedule: String?,
    var address: String,
    var city: String,
    var reduceMobility: String?,
    var latitude: Double,
    var longitude: Double,
    var favorite: Boolean
) {

    @Ignore
    constructor() : this(
        id = 0,
        strId = "",
        name = "",
        pole = "",
        type = "",
        schedule = "",
        address = "",
        city = "",
        reduceMobility = "",
        latitude = 0.0,
        longitude = 0.0,
        favorite = false
    )
}
