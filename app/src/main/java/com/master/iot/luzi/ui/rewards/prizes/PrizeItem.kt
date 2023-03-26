package com.master.iot.luzi.ui.rewards.prizes

import com.master.iot.luzi.R

class PrizeItem(
    val title: String,
    val description: String,
    val resourceId: Int,
    val level: Int = 0
)

fun getPrizesList(): List<PrizeItem> = listOf(
    PrizeItem(
        "Descuento 2% con Endesa",
        "Usa el código LUZI.2${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
        R.mipmap.star_1,
        level = 1
    ),
    PrizeItem(
        "Recupera 1x20€",
        "Solo para nuevos usuarios: Por cada 20€ gastados en tu factura de la luz, recupera 1€, registrando el codigo el código LUZI.1x20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
        R.mipmap.star_1,
        level = 1
    ),
    PrizeItem(
        "Descuento 5% con Endesa",
        "Usa el código LUZI.5${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
        R.mipmap.star_2,
        level = 2
    ),

    PrizeItem(
        "Recupera 2x20€",
        "Solo para nuevos usuarios: Por cada 20€ gastados en tu factura de la luz, recupera 2€, registrando el codigo el código LUZI.2x20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
        R.mipmap.star_2,
        level = 2
    ),
    PrizeItem(
        "Descuento 7% con Endesa",
        "Usa el código LUZI.7${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
        R.mipmap.star_3,
        level = 3
    ),
    PrizeItem(
        "Descuento 10% con Endesa",
        "Usa el código LUZI.10${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
        R.mipmap.star_4,
        level = 4
    ),

    PrizeItem(
        "Recupera 3x20€",
        "Solo para nuevos usuarios: Por cada 20€ gastados en tu factura de la luz, recupera 3€, registrando el codigo el código LUZI.3x20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com. Este cupon no es acumulable y tiene uso unico.",
        R.mipmap.star_4,
        level = 4
    ),
    PrizeItem(
        "Descuento 20% con Endesa",
        "Usa el código LUZI.20${generateUniqueCode()} en el apartado personal de tu perfil en endesa.com",
        R.mipmap.star_5,
        level = 5
    )
)

private fun generateUniqueCode(): String {
    return "AG78TDF22"
}