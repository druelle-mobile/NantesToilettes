package ovh.geoffrey_druelle.nantestoilettes.data.remote.model

import com.google.gson.annotations.SerializedName

data class General(
    @SerializedName("nhits") val hits: Int,
    @SerializedName("parameters") val parameters: Parameters,
    @SerializedName("records") val records: List<Record>
)