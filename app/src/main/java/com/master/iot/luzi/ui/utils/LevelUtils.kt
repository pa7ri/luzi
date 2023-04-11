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

fun Int.getCurrentLevel(): Levels =
    Levels.values().firstOrNull { it.currentLevel==this } ?: Levels.LEVEL_0

fun getLevel(points: Int): Levels =
    when (points) {
        in Levels.LEVEL_0.rangeVal until Levels.LEVEL_1.rangeVal -> Levels.LEVEL_0
        in Levels.LEVEL_1.rangeVal until Levels.LEVEL_2.rangeVal -> Levels.LEVEL_1
        in Levels.LEVEL_2.rangeVal until Levels.LEVEL_3.rangeVal -> Levels.LEVEL_2
        in Levels.LEVEL_3.rangeVal until Levels.LEVEL_4.rangeVal -> Levels.LEVEL_3
        in Levels.LEVEL_4.rangeVal until Levels.LEVEL_5.rangeVal -> Levels.LEVEL_4
        else -> Levels.LEVEL_5
    }