package com.example.snakegame

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.example.snakegame.databinding.ActivityMainBinding
import com.example.snakegame.model.Constants.CELL_SIZE
import kotlin.math.abs

import com.example.snakegame.model.SnakeDirection
import com.example.snakegame.view.SnakeView
import com.example.snakegame.viewmodel.SnakeViewModel

/**
 * The main activity for the Snake Game.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var binding: ActivityMainBinding
    private lateinit var snakeView: SnakeView
    private lateinit var gestureDetector: GestureDetector
    private lateinit var snakeViewModel: SnakeViewModel

    private var initialX = 0
    private var initialY = 0
    private var snakeX = 0
    private var snakeY = 0
    private var screenHeight = 0
    private var screenWidth = 0
    private var currentDirection = SnakeDirection.RIGHT // Initial direction

    private var cellSizeX = 0
    private var cellSizeY = 0


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


        cellSizeX = screenWidth / CELL_SIZE
        cellSizeY = screenHeight / CELL_SIZE

        initializeSwipeDetection()
        snakeViewModel = SnakeViewModel()
        snakeView = SnakeView(this, null, binding.gameView.holder, snakeViewModel)
    }

    override fun onResume() {
        super.onResume()
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

    /**
     * Logic needed for detecting swipes for changing the snake's direction
     */
    private fun initializeSwipeDetection() {
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
                            changeDirection(SnakeDirection.RIGHT)
                        } else {
                            changeDirection(SnakeDirection.LEFT)
                        }
                        return true
                    } else if (abs(deltaY) > 100 && abs(velocityY) > 100) {
                        //Vertical swipe
                        if (deltaY > 0) {
                            changeDirection(SnakeDirection.DOWN)
                        } else {
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
}
