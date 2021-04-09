package edu.itesm.pandemia

import com.google.gson.annotations.SerializedName

data class PaisGson(
        @SerializedName("country")
        var nombre:String?,

        var countryInfo: CountryInfo,
        @SerializedName("cases") var cases: Double?,
        @SerializedName("recovered") var recovered: Double?
)

data class CountryInfo(
        var lat: Double?,
        var long: Double?
)