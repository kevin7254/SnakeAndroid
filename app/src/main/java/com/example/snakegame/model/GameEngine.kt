package com.example.snakegame.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snakegame.viewmodel.SnakeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

class GameEngine(
    private val scope: CoroutineScope,
    private val snakeViewModel: SnakeViewModel,
) {
    private val mutex = Mutex()
    private val mutableStateFlow: MutableStateFlow<State> = MutableStateFlow(
        State(
            food = Food(5, 5),
            snake = listOf(SnakeSegment(2, 2)),
            currentDirection = SnakeDirection.RIGHT,
        )
    )

    private val currentDirection = MutableLiveData<SnakeDirection>()

    internal val snakePositionLiveData = MutableLiveData<List<SnakeSegment>>()

    internal val foodPositionLiveData = MutableLiveData<Food>()

    val state: Flow<State> = mutableStateFlow

    // internal val scoreLiveData = MutableLiveData<Int>() TODO

    // Function to update the LiveData properties when game state changes
    private fun updateGameState(state: State) {
        snakePositionLiveData.value = state.snake
        // foodPositionLiveData.value = state.food
        // _scoreLiveData.value = state.score
    }


    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }

    fun reset() {
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


    internal fun startGame() {
        var snakeLength = 2
        scope.launch {
            while (true) {
                delay(250)
                mutableStateFlow.update { state ->
                    //Log.d("Kevin", state.toString())
                    val hasReachedLeftEnd =
                        (state.snake.first().x == 0) &&
                                state.currentDirection == SnakeDirection.LEFT
                    val hasReachedTopEnd =
                        (state.snake.first().y == 0) &&
                                state.currentDirection == SnakeDirection.UP
                    val hasReachedRightEnd =
                        (state.snake.first().x == BOARD_SIZE - 1) &&
                                state.currentDirection == SnakeDirection.RIGHT
                    val hasReachedBottomEnd =
                        (state.snake.first().y == BOARD_SIZE - 1) &&
                                state.currentDirection == SnakeDirection.DOWN

                    if (hasReachedLeftEnd || hasReachedTopEnd ||
                        hasReachedRightEnd || hasReachedBottomEnd
                    ) {
                        snakeLength = 2
                        snakeViewModel.onGameEnded()
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

                    if (newSnakePosition.areEqual(state.food)) {
                        foodPositionLiveData.value = state.food
                        snakeViewModel.onFoodEaten()
                        snakeLength++
                    }

                    if (state.snake.contains(newSnakePosition)) {
                        snakeLength = 2
                        snakeViewModel.onGameEnded()
                    }

                    val s = (listOf(newSnakePosition) + state.snake.take(snakeLength - 1))
                    state.copy(
                        food = getFood(newSnakePosition, state.food),
                        snake = listOf(newSnakePosition) + state.snake.take(snakeLength - 1),
                        currentDirection = currentDirection.value!!,
                    ).also { updateGameState(it) }
                }
            }
        }
    }


    private fun getFood(snakeSegment: SnakeSegment, food: Food): Food {
        return if (snakeSegment.areEqual(food)) Food(
            Random.nextInt(BOARD_SIZE), Random.nextInt(BOARD_SIZE)
        )
        else food
    }

    companion object {
        const val BOARD_SIZE = 70
    }

}