package com.example.snakegame.model

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import com.example.snakegame.viewmodel.SnakeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

/**
 * The Game Engine, taking care of all the game logic such as moving each [SnakeSegment],
 * keeping track of the [Food] and so on.
 *
 * Uses an internal [State] for keeping track of everything.
 *
 * @param scope the scope tied to the [SnakeViewModel]
 * @param snakeViewModel the ViewModel.
 */
class GameEngine(
    private val scope: CoroutineScope,
    private val snakeViewModel: SnakeViewModel,
) {
    private var isGameRunning = false
    private val mutex = Mutex()
    private val currentDirection = MutableLiveData<SnakeDirection>()
    private val mutableStateFlow: MutableStateFlow<State> by lazy {
        MutableStateFlow(
            State(
                food = Food(5, 5),
                snake = listOf(SnakeSegment(2, 2)),
                currentDirection = SnakeDirection.RIGHT,
            )
        )
    }

    /**
     * The state of the game that can be collected by subscribers.
     */
    internal val state: StateFlow<State> = mutableStateFlow.asStateFlow()

    /**
     * Used to keep track of which way the Snake is moving.
     *
     * Ex: (0,-1) - the Snake moves upwards. (y is decreasing)
     */
    internal var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }

    /**
     * Resets the game to it's original state.
     */
    @MainThread
    internal fun reset() {
        mutableStateFlow.update {
            it.copy(
                food = Food(5, 5),
                snake = listOf(SnakeSegment(7, 7)),
                currentDirection = SnakeDirection.RIGHT
            )
        }
        currentDirection.value = SnakeDirection.RIGHT
        move = Pair(1, 0)
    }

    /**
     * Starts the the main loop - which performs all the game logic.
     *
     * Uses a [CoroutineScope] which is thread safe - it will always run on the Main Thread.
     */
    @MainThread
    internal fun startGame() {
        var snakeLength = 2
        isGameRunning = true
        scope.launch {
            while (isGameRunning) {
                delay(250)
                mutableStateFlow.update { state ->
                    val hasReachedLeftEnd =
                        (state.snake.first().x == 0) &&
                                state.currentDirection == SnakeDirection.LEFT
                    val hasReachedTopEnd =
                        (state.snake.first().y == 0) &&
                                state.currentDirection == SnakeDirection.UP
                    val hasReachedRightEnd =
                        (state.snake.first().x == 15 - 1) &&
                                state.currentDirection == SnakeDirection.RIGHT
                    val hasReachedBottomEnd =
                        (state.snake.first().y == 30 - 1) &&
                                state.currentDirection == SnakeDirection.DOWN

                    if (hasReachedLeftEnd || hasReachedTopEnd ||
                        hasReachedRightEnd || hasReachedBottomEnd
                    ) {
                        snakeLength = 2
                        snakeViewModel.onGameEnded()
                        isGameRunning = false
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

                    val newSnakePosition = state.snake.first().let {
                        mutex.withLock {
                            SnakeSegment(
                                (it.x + move.first),
                                (it.y + move.second),
                            )
                        }
                    }

                    if (isSamePosition(newSnakePosition, state.food)) {
                        snakeViewModel.onFoodEaten()
                        snakeLength++
                    }

                    if (state.snake.contains(newSnakePosition)) {
                        snakeLength = 2
                        snakeViewModel.onGameEnded()
                        isGameRunning = false

                    }
                    state.copy(
                        food = getFood(newSnakePosition, state.food),
                        snake = listOf(newSnakePosition) + state.snake.take(snakeLength - 1),
                        currentDirection = currentDirection.value!!,
                    )
                }
            }
        }
    }

    private fun getFood(snakeSegment: SnakeSegment, food: Food): Food {
        return if (isSamePosition(snakeSegment, food)) Food(
            Random.nextInt(15 - 1), Random.nextInt(30 - 1)
        )
        else food
    }
}
