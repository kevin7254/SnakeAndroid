package com.example.snakegame

import android.content.Context
import android.graphics.PixelFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.snakegame.databinding.ActivityMainBinding
import kotlin.math.abs

import com.example.snakegame.model.SnakeDirection
import com.example.snakegame.view.SnakeView
import com.example.snakegame.viewmodel.SnakeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var binding: ActivityMainBinding
    private lateinit var snakeView: SnakeView
    private lateinit var gestureDetector: GestureDetector
    private lateinit var snakeViewModel: SnakeViewModel
    private val gridSizeX = 70
    private val gridSizeY = 70

    private var initialX = 0
    private var initialY = 0
    private var snakeX = 0
    private var snakeY = 0
    private var screenHeight = 0
    private var screenWidth = 0
    private var currentDirection = SnakeDirection.RIGHT // Initial direction

    var cellSizeX = 0
    var cellSizeY = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        context = applicationContext

        val displayMetrics = resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        initialX = screenWidth / 2
        initialY = screenHeight / 2


        cellSizeX = screenWidth / gridSizeX
        cellSizeY = screenHeight / gridSizeY

        Log.d("kevin", "x $cellSizeX y $cellSizeY")
        initGestureDectector()
        snakeViewModel = SnakeViewModel()
        snakeView = SnakeView(this, null, binding.gameView.holder, snakeViewModel)
    }

    override fun onResume() {
        super.onResume()

        snakeView.surfaceCreated(snakeView.holder)
    }

    override fun onPause() {
        super.onPause()

        snakeView.surfaceDestroyed(snakeView.holder)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    private fun changeDirection(newDirection: SnakeDirection) {
        if (currentDirection != newDirection) {
            currentDirection = newDirection
            snakeViewModel.onUserInputReceived(newDirection)
        }
    }

    private fun initGestureDectector() {
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
                ): Boolean {
                    val deltaX = e2.x - e1.x
                    val deltaY = e2.y - e1.y

                    if (abs(deltaX) > abs(deltaY) && abs(deltaX) > 100 && abs(velocityX) > 100) {
                        //Horizontal swipe
                        if (deltaX > 0) {
                            Log.d(Companion::class.java.toString(), "action right move")
                            changeDirection(SnakeDirection.RIGHT)
                        } else {
                            Log.d(Companion::class.java.toString(), "action left move")
                            changeDirection(SnakeDirection.LEFT)
                        }
                        return true
                    } else if (abs(deltaY) > 100 && abs(velocityY) > 100) {
                        //Vertical swipe
                        if (deltaY > 0) {
                            Log.d(Companion::class.java.toString(), "action down move")
                            changeDirection(SnakeDirection.DOWN)
                        } else {
                            Log.d(Companion::class.java.toString(), "action up move")
                            changeDirection(SnakeDirection.UP)
                        }
                        return true
                    }
                    return false
                }

                override fun onSingleTapUp(event: MotionEvent): Boolean {
                    lateinit var direction: SnakeDirection

                    if (currentDirection == SnakeDirection.LEFT ||
                        currentDirection == SnakeDirection.RIGHT
                    ) {
                        // Clicked over snake
                        if (event.y < snakeY + CELL_SIZE) {
                            direction = SnakeDirection.UP
                        }
                        // Clicked under snake
                        else if (event.y > snakeY + CELL_SIZE) {
                            direction = SnakeDirection.DOWN
                        }
                    } else if (currentDirection == SnakeDirection.UP ||
                        currentDirection == SnakeDirection.DOWN
                    ) {
                        // Clicked left of snake
                        if (event.x < snakeX + CELL_SIZE) {
                            direction = SnakeDirection.LEFT
                        }
                        // Clicked right of snake
                        else if (event.x > snakeX + CELL_SIZE) {
                            direction = SnakeDirection.RIGHT
                        }
                    }
                    changeDirection(direction)
                    return true
                }
            })
    }

    companion object {
        private const val CELL_SIZE = 70
        private const val FOOD_RADIUS = 70
    }
}