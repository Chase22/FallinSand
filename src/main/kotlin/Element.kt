package de.chasenet.falling

import java.awt.Color

sealed class Element {
    abstract val color: Color

    open fun update(position: Point, cellMap: CellMap) {}

    object Wall : Element() {
        override val color: Color = Color.DARK_GRAY

        override fun update(position: Point, cellMap: CellMap) {}
    }

    object Sand : Element() {
        override val color: Color = Color.YELLOW

        override fun update(position: Point, cellMap: CellMap) {
            if (!cellMap.moveCell(position, position + Point.DOWN)) {
                if (!cellMap.moveCell(position, position + Point.DOWN + Point.LEFT)) {
                    cellMap.moveCell(position, position + Point.DOWN + Point.RIGHT)
                }
            }
        }
    }

    object Air : Element() {
        override val color: Color = Color.BLACK
    }
}

enum class Cell(val element: Element) {
    AIR(Element.Air), WALL(Element.Wall), SAND(Element.Sand)
}