package ovh.geoffrey_druelle.nantestoilettes.data.remote.model

import com.google.gson.annotations.SerializedName

data class Parameters(
    @SerializedName("dataset") val dataset: String,
    @SerializedName("format") val format: String,
    @SerializedName("rows") val rows: Int,
    @SerializedName("timezone") val timezone: String
)