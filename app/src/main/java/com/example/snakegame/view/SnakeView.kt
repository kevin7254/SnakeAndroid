package com.example.snakegame.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.snakegame.viewmodel.SnakeViewModel
import kotlinx.coroutines.GlobalScope

class SnakeView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet),
    SurfaceHolder.Callback {
    private val surfaceHolder: SurfaceHolder = holder
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
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private fun drawGame() {
        val canvas = surfaceHolder.lockCanvas()
        canvas.drawColor(Color.BLACK)

        val snakePaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }


    }
}