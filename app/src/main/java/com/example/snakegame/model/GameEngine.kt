package com.example.snakegame.model


import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

class GameEngine(
    private val scope: CoroutineScope,
    private val onGameEnded: () -> Unit,
    private val onFoodEaten: () -> Unit,
) {
    private val mutex = Mutex()
    private val mutableStateFlow: MutableStateFlow<State> = MutableStateFlow(
        State(
            food = Food(5, 5),
            snake = listOf(SnakeSegment(2, 2)),
            currentDirection = SnakeDirection.RIGHT,
        )
    )
    val state = mutableStateFlow

    private val currentDirection = MutableLiveData<SnakeDirection>()


    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }


    init {
        var snakeLength = 2
        scope.launch {
            while (true) {
                delay(150)
                mutableStateFlow.update {
                    val hasReachedLeftEnd =
                        (it.snake.first().x == 0) && it.currentDirection == SnakeDirection.LEFT
                    val hasReachedTopEnd =
                        (it.snake.first().y == 0) && it.currentDirection == SnakeDirection.UP
                    val hasReachedRightEnd =
                        (it.snake.first().x == BOARD_SIZE - 1)
                                && it.currentDirection == SnakeDirection.RIGHT
                    val hasReachedBottomEnd =
                        (it.snake.first().y == BOARD_SIZE - 1)
                                && it.currentDirection == SnakeDirection.DOWN

                    if (hasReachedLeftEnd || hasReachedTopEnd ||
                        hasReachedRightEnd || hasReachedBottomEnd
                    ) {
                        snakeLength = 2
                        onGameEnded.invoke()
                    }

                    if (move.first == 0 && move.second == -1) {
                        currentDirection.value = SnakeDirection.UP
                    } else if (move.first == -1 && move.second == 0) {
                        currentDirection.value = SnakeDirection.LEFT
                    } else if (move.first == 1 && move.second == 0) {
                        currentDirection.value = SnakeDirection.RIGHT
                    } else if (move.first == 0 && move.second == 1) {
                        currentDirection.value = SnakeDirection.DOWN
                    }

                    val newSnakePosition = it.snake.first().let {
                        mutex.withLock {
                            SnakeSegment(
                                (it.x + move.first + BOARD_SIZE) % BOARD_SIZE,
                                (it.y + move.second + BOARD_SIZE) % BOARD_SIZE,
                            )
                        }
                    }

                    if (newSnakePosition.areEqual(it.food)) {
                        onFoodEaten.invoke()
                    }

                    if (it.snake.contains(newSnakePosition)) {
                        snakeLength = 2
                        onGameEnded.invoke()
                    }
                    it.copy(
                        food = getFood(newSnakePosition, it.food),
                        snake = listOf(newSnakePosition) + it.snake.take(snakeLength - 1),
                        currentDirection = currentDirection.value!!,
                    )
                }
            }
        }
    }


    private fun getFood(snakeSegment: SnakeSegment, food: Food): Food {
        return if (snakeSegment.areEqual(food)) Food(
            Random.nextInt(BOARD_SIZE),
            Random.nextInt(BOARD_SIZE)
        )
        else food
    }

    companion object {
        const val BOARD_SIZE = 32
    }

}