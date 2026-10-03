package app.xan.music.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import app.xan.music.R

/** Draws the supplied transparent in-app PNG at 80% of its frame. */
@Composable
fun xanLogoPainter(): Painter {
    val mark = painterResource(R.drawable.xan_in_app_mark)
    return remember(mark) { XanLogoPainter(mark) }
}

private class XanLogoPainter(
    private val mark: Painter,
) : Painter() {
    override val intrinsicSize = mark.intrinsicSize

    override fun DrawScope.onDraw() {
        val sourceSize = mark.intrinsicSize
        val scale = minOf(size.width / sourceSize.width, size.height / sourceSize.height) * 0.8f
        val markSize = Size(sourceSize.width * scale, sourceSize.height * scale)
        withTransform({
            translate(
                left = (size.width - markSize.width) / 2f,
                top = (size.height - markSize.height) / 2f,
            )
        }) {
            with(mark) {
                draw(size = markSize)
            }
        }
    }
}
