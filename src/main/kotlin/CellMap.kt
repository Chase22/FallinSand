package de.chasenet.falling

import java.awt.image.BufferedImage
import kotlin.math.floor
import kotlin.math.roundToInt

class CellMap(val width: Int, val height: Int) {
    private val cells = Array(width * height) { Cell.AIR }

    operator fun get(point: Point) = cells[toIndex(point)]

    val cellMap: List<CellPoint>
        get() = cells.mapIndexed { index, cell ->
            val (x, y) = toCoords(index)
            CellPoint(x, y, cell)
        }

    fun toIndex(point: Point) = width * point.y + point.x

    fun toCoords(index: Int) = Point(index % width, index / width)

    fun contains(point: Point) = (0 until width).contains(point.x) && (0 until height).contains(point.y)

    operator fun set(point: Point, value: Cell) {
        if (contains(point)) cells[toIndex(point)] = value
    }

    fun moveCell(start: Point, target: Point, force: Boolean = false): Boolean {
        if (!contains(target)) return false

        if (this[target] == Cell.AIR || force) {
            this[target] = this[start]
            this[start] = Cell.AIR
            return true
        }
        return false
    }

    fun render(): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)

        cellMap.forEach { (x, y, cell) ->
            image.setRGB(x, y, cell.element.color.rgb)
        }
        return image
    }

    fun update() {
        cellMap.forEach { (x, y, cell) ->
            cell.element.update(Point(x, y), this)
        }
    }
}

open class Point(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun div(other: Float) = Point(
        floor(x / other).toInt(),
        floor(y / other).toInt()
    )

    override fun toString(): String = "Point($x, $y)"

    companion object {
        val UP = Point(0, -1)
        val DOWN = Point(0, 1)
        val LEFT = Point(-1, 0)
        val RIGHT = Point(1, 0)
    }
}

class CellPoint(x: Int, y: Int, val cell: Cell) : Point(x, y) {
    operator fun component3() = cell
}