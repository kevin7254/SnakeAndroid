package com.example.snakegame.view

import android.app.GameState
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import android.view.SurfaceView
import com.example.snakegame.MainActivity
import com.example.snakegame.model.State
import com.example.snakegame.viewmodel.SnakeViewModel
import kotlinx.coroutines.launch

class SnakeView(context: Context, attributeSet: AttributeSet?, private val surfaceHolder: SurfaceHolder) : SurfaceView(context, attributeSet),
    SurfaceHolder.Callback {
    private val paint = Paint()
    private val cellSize = 70

    private lateinit var snakeViewModel: SnakeViewModel

    init {
        surfaceHolder.addCallback(this)
        paint.color = Color.GREEN
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        snakeViewModel = SnakeViewModel()
        snakeViewModel.startGame()
        val lifecycleOwner = context as? LifecycleOwner
        lifecycleOwner?.lifecycleScope?.launch {
            /*snakeViewModel.gameState.collect {
                Log.d("Kevin", "$it")
                drawGame(it)
            }*/
        }



    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d("Kevin", Thread.currentThread().name)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d("Kevin", Thread.currentThread().name)
    }

    private fun drawGame(gameState: State) {

        val canvas = surfaceHolder.lockCanvas() ?: return
        canvas.drawColor(Color.BLACK)

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

        canvas.drawRect(
            gameState.food.x.toFloat(),
            gameState.food.y.toFloat(),
            (gameState.food.x + cellSize).toFloat(),
            (gameState.food.y + cellSize).toFloat(),
            applePaint
        )

        surfaceHolder.unlockCanvasAndPost(canvas)

    }
}