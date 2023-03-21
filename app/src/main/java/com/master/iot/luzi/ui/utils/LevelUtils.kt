package com.master.iot.luzi.ui.utils

enum class Levels(val currentLevel: Int, val nextLevel: Int?, val rangeVal: Int) {
    LEVEL_0(0, 1, 0),
    LEVEL_1(1, 2, 10),
    LEVEL_2(2, 3, 20),
    LEVEL_3(3, 4, 30),
    LEVEL_4(4, 5, 40),
    LEVEL_5(5, null, 55)
}

fun Levels.getNextLevel(): Levels? = Levels.values().firstOrNull { it.currentLevel==this.nextLevel }
