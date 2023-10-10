package com.example.snakegame.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import android.view.SurfaceView
import com.example.snakegame.model.Constants.CELL_SIZE
import com.example.snakegame.model.State
import com.example.snakegame.viewmodel.SnakeViewModel
import kotlinx.coroutines.launch

/**
 * The class responsible for everything UI-related.
 */
class SnakeView(
    context: Context,
    attributeSet: AttributeSet?,
    private val surfaceHolder: SurfaceHolder,
    private val snakeViewModel: SnakeViewModel,
) : SurfaceView(context, attributeSet),
    SurfaceHolder.Callback {
    private val paint = Paint()

    init {
        surfaceHolder.addCallback(this)
        paint.color = Color.GREEN
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        snakeViewModel.startGame()
        val lifecycleOwner = context as? LifecycleOwner
        lifecycleOwner?.lifecycleScope?.launch {
            snakeViewModel.gameState.collect {
                drawGame(it)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d("Kevin", Thread.currentThread().name)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d("Kevin", Thread.currentThread().name)
    }

    /**
     * Logic for drawing the snake and food.
     */
    private fun drawGame(gameState: State) {
        val canvas = surfaceHolder.lockCanvas() ?: return

        canvas.drawColor(Color.parseColor("#242424"))

        val snakePaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        val applePaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        gameState.snake.forEach {
            val left = it.x * CELL_SIZE
            val top = it.y * CELL_SIZE
            val right = left + CELL_SIZE
            val bottom = top + CELL_SIZE

            // Draw the cell using canvas.drawRect
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                snakePaint,
            )
        }

        val leftFood = gameState.food.x * CELL_SIZE
        val topFood = gameState.food.y * CELL_SIZE
        val rightFood = leftFood + CELL_SIZE
        val bottomFood = topFood + CELL_SIZE

        canvas.drawRect(
            leftFood.toFloat(),
            topFood.toFloat(),
            rightFood.toFloat(),
            bottomFood.toFloat(),
            applePaint
        )

        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
        canvas.drawRect(
            0f,
            0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            borderPaint,
        )
        surfaceHolder.unlockCanvasAndPost(canvas)
    }
}
