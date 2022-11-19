package com.master.iot.luzi.data.mtpetrol

import com.google.gson.annotations.SerializedName

data class MTPetrolPricesResponse(
    val Fecha: String,
    val ListaEESSPrecio: List<MTPetrolPriceItem>,
    val Nota: String,
    val ResultadoConsulta: String
) {
    data class MTPetrolPriceItem(
        @SerializedName("% BioEtanol")
        val bioEtanol: String,
        @SerializedName("C.P.")
        val cP: String,
        @SerializedName("Dirección")
        val dirección: String,
        @SerializedName("Horario")
        val horario: String,
        @SerializedName("IDCCAA")
        val iDCCAA: String,
        @SerializedName("IDEESS")
        val iDEESS: String,
        @SerializedName("IDMunicipio")
        val iDMunicipio: String,
        @SerializedName("IDProvincia")
        val iDProvincia: String,
        @SerializedName("Latitud")
        val latitud: String,
        @SerializedName("Localidad")
        val localidad: String,
        @SerializedName("Longitud (WGS84)")
        val longitudWGS84: String,
        @SerializedName("Margen")
        val margen: String,
        @SerializedName("Municipio")
        val municipio: String,
        @SerializedName("Precio Biodiesel")
        val precioBiodiesel: String,
        @SerializedName("Precio Bioetanol")
        val precioBioetanol: String,
        @SerializedName("Precio Gas Natural Comprimido")
        val precioGasNaturalComprimido: String,
        @SerializedName("Precio Gas Natural Licuado")
        val precioGasNaturalLicuado: String,
        @SerializedName("Precio Gases licuados del petróleo")
        val precioGasesLicuadosDelPetróleo: String,
        @SerializedName("Precio Gasoleo A")
        val precioGasoleoA: String,
        @SerializedName("Precio Gasoleo B")
        val precioGasoleoB: String,
        @SerializedName("Precio Gasoleo Premium")
        val precioGasoleoPremium: String,
        @SerializedName("Precio Gasolina 95 E10")
        val precioGasolina95E10: String,
        @SerializedName("Precio Gasolina 95 E5")
        val precioGasolina95E5: String,
        @SerializedName("Precio Gasolina 95 E5 Premium")
        val precioGasolina95E5Premium: String,
        @SerializedName("Precio Gasolina 98 E10")
        val precioGasolina98E10: String,
        @SerializedName("Precio Gasolina 98 E5")
        val precioGasolina98E5: String,
        @SerializedName("Precio Hidrogeno")
        val precioHidrogeno: String,
        @SerializedName("Provincia")
        val provincia: String,
        @SerializedName("Remisión")
        val remisión: String,
        @SerializedName("Rótulo")
        val rótulo: String,
        @SerializedName("Tipo Venta")
        val tipoVenta: String,
        @SerializedName("% Éster metílico")
        val ésterMetílico: String
    )
}