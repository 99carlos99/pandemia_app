package edu.itesm.pandemia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    fun viewData(view: View){
        mMap.clear()
        for (pais in data){
            mMap.addMarker(
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("País: ${pais.nombre}").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
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
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("No. de tests: ${pais.tests}").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

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
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("No. de casos: ${pais.casos}").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
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
                    MarkerOptions().position(LatLng(pais.latitude, pais.longitude)).title("No de muertes: ${pais.defunciones}").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
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
}