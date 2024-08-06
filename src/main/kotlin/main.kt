package de.chasenet.falling

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.min
import kotlin.system.measureNanoTime

val max_fps = 100
val max_tps = 100

var fps = max_fps
var tps = max_tps

fun main() {
    JFrame().apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val cellMap = CellMap(200, 200)

        val pixelPanel = PixelPanel(cellMap)
        add(pixelPanel)
        size = Dimension(800, 800)
        isVisible = true

        Thread {
            while (true) {
                val runtimeMillis = measureNanoTime {
                    pixelPanel.repaint()
                }.toFloat() / 1000000
                val delay = ((1000/ min(max_fps, tps)) - runtimeMillis).coerceAtLeast(0.0f)
                Thread.sleep(delay.toLong())
                fps = (1000/(runtimeMillis+delay)).toInt()
            }
        }.start()

        Thread {
            while (true) {
                val runtimeMillis = measureNanoTime {
                    cellMap[Point(100, 0)] = Cell.SAND
                    cellMap.update()
                }.toFloat() / 1000000

                val delay = ((1000/ max_tps) - runtimeMillis).coerceAtLeast(0.0f)
                Thread.sleep(delay.toLong())
                tps = (1000/(runtimeMillis+delay)).toInt()
            }
        }.start()
    }
}

class PixelPanel(private val cellMap: CellMap) : JPanel() {
    private val scalingFactor: Float
        get() = min(
            this.width.toFloat() / cellMap.width.toFloat(),
            this.height.toFloat() / cellMap.height.toFloat()
        )

    init {
        val mouseEventListener = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                super.mouseClicked(e)
                setCell(e)
            }

            override fun mouseDragged(e: MouseEvent) {
                super.mouseDragged(e)
                setCell(e)
            }

            private fun setCell(e: MouseEvent) {
                val cell = if (e.button == MouseEvent.BUTTON1) Cell.WALL else Cell.SAND
                val point = worldToMapCoordinate(Point(e.x, e.y))
                cellMap[point] = cell
            }
        }
        addMouseListener(mouseEventListener)
        addMouseMotionListener(mouseEventListener)
    }

    fun worldToMapCoordinate(point: Point): Point = point / scalingFactor

    override fun paint(g: Graphics) {
        super.paint(g)
        val renderedTileset = cellMap.render()

        val scaledImage =
            renderedTileset.getScaledInstance(
                /* width = */ (renderedTileset.width * scalingFactor).toInt(),
                /* height = */ (renderedTileset.height * scalingFactor).toInt(),
                /* hints = */ 0
            )

        g.drawImage(
            /* img = */ scaledImage,
            /* x = */ 0,
            /* y = */ 0,
            /* bgcolor = */ Color.BLACK,
            /* observer = */ null
        )

        g.color = Color.WHITE
        g.drawString("$fps FPS", 0, 20)
        g.drawString("$tps TPS", 0, 50)
    }
}