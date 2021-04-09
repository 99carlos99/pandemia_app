package edu.itesm.pandemia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val url = "https://disease.sh/v3/covid-19/countries"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        cargaDatos()
        getCountries()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    fun viewDataGson(view: View){

        mMap.clear()
        for (pais in paisesGson){
            mMap.addMarker(
                    MarkerOptions().position(LatLng(pais?.countryInfo.lat?:0.0, pais?.countryInfo.long?:0.0)).title("País: ${pais.nombre}").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_country)))
        }
    }

    fun viewData(view: View){
        mMap.clear()
        for (pais in data){
            mMap.addMarker(
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("País: ${pais.nombre}").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_covidd)))
        }
    }

    fun topTests(view: View){
        mMap.clear()
        val dTests = data.sortedByDescending {
            it.tests
        }
        val topTests = dTests.dropLast(dTests.size - 10)
        for (pais in topTests){
            mMap.addMarker(
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("No. de tests: ${pais.tests}").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_cases))

            )
        }
    }

    fun topCases(view: View){
        mMap.clear()
        val dCases = data.sortedByDescending {
            it.casos
        }
        val topTests = dCases.dropLast(dCases.size - 10)

        for (pais in topTests){
            mMap.addMarker(
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("No. de casos: ${pais.casos}").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_test))
            )
        }
    }

    fun topDeaths(view: View){
        mMap.clear()
        val dDeaths = data.sortedByDescending {
            it.defunciones
        }
        val topDeaths = dDeaths.dropLast(dDeaths.size - 10)

        for (pais in topDeaths){
            mMap.addMarker(
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("No de muertes: ${pais.defunciones}").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_muertes))
            )
        }
    }

    private val data = mutableListOf<Pais>()
    fun cargaDatos(){
        val requestQueue = Volley.newRequestQueue(this)
        val peticion = JsonArrayRequest(Request.Method.GET,url,null,Response.Listener {
            val jsonArray  = it
            for (i in 0 until jsonArray.length()){

                val pais = jsonArray.getJSONObject(i)
                val nombre = pais.getString("country")
                val countryInfoData = pais.getJSONObject("countryInfo")
                val defunciones = pais.getDouble("deaths")
                val tests = pais.getDouble("tests")

                val latitude = countryInfoData.getDouble("lat")
                val longitude = countryInfoData.getDouble("long")
                val casos = pais.getDouble("cases")
                val recuperdos = pais.getDouble("recovered")

                val paisObject = Pais(nombre,latitude, longitude, casos, recuperdos, defunciones, tests)
                data.add(paisObject)

            }

        }, Response.ErrorListener {

        })
        requestQueue.add(peticion)
    }

    private fun getRetroFit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://disease.sh/v3/covid-19/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private lateinit var paisesGson: ArrayList<PaisGson>

    private fun getCountries(){
        val callToService = getRetroFit().create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val responseFromService = callToService.getCountries()
            runOnUiThread {
                paisesGson = responseFromService.body() as ArrayList<PaisGson>
                if(responseFromService.isSuccessful){
                    Toast.makeText(applicationContext, "Datos obtenidos", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}