package edu.itesm.pandemia

data class Pais(var nombre: String,
                var latitude: Double,
                var longitude: Double,
                var casos: Double,
                var recuperados: Double,
                var defunciones: Double,
                var tests: Double)