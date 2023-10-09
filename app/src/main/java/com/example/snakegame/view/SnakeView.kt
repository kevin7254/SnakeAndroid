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
import com.example.snakegame.model.State
import com.example.snakegame.viewmodel.SnakeViewModel
import kotlinx.coroutines.launch

class SnakeView(
    context: Context,
    attributeSet: AttributeSet?,
    private val surfaceHolder: SurfaceHolder,
    private val snakeViewModel: SnakeViewModel,
) : SurfaceView(context, attributeSet),
    SurfaceHolder.Callback {
    private val paint = Paint()
    private val cellSize = 70

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

    private suspend fun drawGame(gameState: State) {
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
            val left = it.x * cellSize
            val top = it.y * cellSize
            val right = left + cellSize
            val bottom = top + cellSize

            // Draw the cell using canvas.drawRect
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                snakePaint,
            )
        }

        val leftFood = gameState.food.x * cellSize
        val topFood = gameState.food.y * cellSize
        val rightFood = leftFood + cellSize
        val bottomFood = topFood + cellSize

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
            strokeWidth = 10f // e.g., 10f
        }
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), borderPaint)

        surfaceHolder.unlockCanvasAndPost(canvas)
    }
}
