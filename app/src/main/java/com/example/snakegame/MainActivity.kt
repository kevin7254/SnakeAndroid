package com.example.snakegame

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path.Direction
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.example.snakegame.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var gameRunnable: Runnable
    private lateinit var snakeSegments: MutableList<SnakeSegment>
    private lateinit var gestureDetector: GestureDetector
    private var running = false
    private val frameRate = 128L

    private var initialX = 0
    private var initialY = 0
    private var snakeX = 0
    private var snakeY = 0
    private var foodX = 0
    private var foodY = 0
    private var screenHeight = 0
    private var screenWidth = 0
    private var previousX = 0
    private var previousY = 0
    private var currentDirection = Direction.RIGHT // Initial direction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        context = applicationContext
        handler = Handler(Looper.getMainLooper())
        val displayMetrics = resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        initialX = screenWidth / 2
        initialY = screenHeight / 2
        snakeSegments = mutableListOf(SnakeSegment(initialX, initialY))
        val (newFoodX, newFoodY) = generateRandomCoords()
        foodX = newFoodX
        foodY = newFoodY

        binding.gameView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                startGameLoop()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                stopGameLoop()
            }
        })

        initGestureDectector()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    private fun changeDirection(newDirection: Direction) {
        if (currentDirection != newDirection) {
            currentDirection = newDirection
        }
    }

    private fun startGameLoop() {
        running = true
        gameRunnable = Runnable {
            if (running) {
                updateGame()
                drawGame()
                handler.postDelayed(gameRunnable, frameRate)
            }
        }
        handler.post(gameRunnable)
    }

    private fun stopGameLoop() {
        running = false
        handler.removeCallbacks(gameRunnable)
    }

    private fun updateGame() {
        when (currentDirection) {
            Direction.DOWN -> snakeY += CELL_SIZE
            Direction.UP -> snakeY -= CELL_SIZE
            Direction.LEFT -> snakeX -= CELL_SIZE
            Direction.RIGHT -> snakeX += CELL_SIZE
        }

        for (i in snakeSegments.size - 1 downTo 1) {
            snakeSegments[i] = snakeSegments[i - 1]
        }

        snakeSegments[0] = SnakeSegment(snakeX, snakeY)

        if (snakeY < 0 || snakeY > screenHeight || snakeX < 0 || snakeX > screenWidth) {
            stopGameLoop()
            return
        }

        if (snakeX == foodX && snakeY == foodY) {
            foodEaten()
        }

    }

    private fun drawGame() {
        val canvas = binding.gameView.holder.lockCanvas() ?: return

        canvas.drawColor(Color.BLACK)

        val snakePaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        snakeSegments.forEach {
            canvas.drawRect(
                it.x.toFloat(),
                it.y.toFloat(),
                (it.x + CELL_SIZE).toFloat(),
                (it.y + CELL_SIZE).toFloat(),
                snakePaint
            )
        }

        val applePaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        canvas.drawRect(
            foodX.toFloat(),
            foodY.toFloat(),
            (foodX + FOOD_RADIUS).toFloat(),
            (foodY + FOOD_RADIUS).toFloat(),
            applePaint
        )

        binding.gameView.holder.unlockCanvasAndPost(canvas)
    }

    private fun foodEaten() {
        snakeSegments.add(SnakeSegment(foodX, foodY))
        val (newFoodX, newFoodY) = generateRandomCoords()
        foodX = newFoodX
        foodY = newFoodY
    }

    private fun generateRandomCoords(): Pair<Int, Int> {
        val maxX = screenWidth / FOOD_RADIUS
        val maxY = screenHeight / FOOD_RADIUS
        val randomX = (0 until maxX).random() * FOOD_RADIUS
        val randomY = (0 until maxY).random() * FOOD_RADIUS
        return Pair(randomX, randomY)
    }

    private fun initGestureDectector() {
        gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val deltaX = e2.x - e1.x
                    val deltaY = e2.y - e1.y

                    if (abs(deltaX) > abs(deltaY) && abs(deltaX) > 100 && abs(velocityX) > 100) {
                        //Horizontal swipe
                        if (deltaX > 0) {
                            Log.d(Companion::class.java.toString(), "action right move")
                            changeDirection(Direction.RIGHT)
                        } else {
                            Log.d(Companion::class.java.toString(), "action left move")
                            changeDirection(Direction.LEFT)
                        }
                        return true
                    } else if (abs(deltaY) > 100 && abs(velocityY) > 100) {
                        //Vertical swipe
                        if (deltaY > 0) {
                            Log.d(Companion::class.java.toString(), "action down move")
                            changeDirection(Direction.DOWN)
                        } else {
                            Log.d(Companion::class.java.toString(), "action up move")
                            changeDirection(Direction.UP)
                        }
                        return true
                    }
                    return false
                }

                override fun onSingleTapUp(event: MotionEvent): Boolean {
                    lateinit var direction: Direction

                    if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
                        // Clicked over snake
                        if (event.y < snakeY + CELL_SIZE) {
                            direction = Direction.UP
                        }
                        // Clicked under snake
                        else if (event.y > snakeY + CELL_SIZE) {
                            direction = Direction.DOWN
                        }
                    } else if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                        // Clicked left of snake
                        if (event.x < snakeX + CELL_SIZE) {
                            direction = Direction.LEFT
                        }
                        // Clicked right of snake
                        else if (event.x > snakeX + CELL_SIZE) {
                            direction = Direction.RIGHT
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

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    data class SnakeSegment(val x: Int, val y: Int)
}