package com.example.snakegame.viewmodel

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snakegame.model.GameEngine
import com.example.snakegame.model.SnakeDirection
import com.example.snakegame.model.State
import kotlinx.coroutines.flow.StateFlow

class SnakeViewModel : ViewModel() {

    private val gameEngine = GameEngine(
        scope = viewModelScope,
        snakeViewModel = this,
    )

    /**
     * The state of the game that can be collected by subscribers.
     */
    internal val gameState: StateFlow<State> = gameEngine.state
    // val scoreLiveData: LiveData<Int> = gameEngine.scoreLiveData TODO

    /**
     * Transforms the [SnakeDirection] so the [GameEngine] understands it.
     */
    @MainThread
    internal fun onUserInputReceived(direction: SnakeDirection) {
        when (direction) {
            SnakeDirection.UP -> gameEngine.move = Pair(0, -1)
            SnakeDirection.LEFT -> gameEngine.move = Pair(-1, 0)
            SnakeDirection.RIGHT -> gameEngine.move = Pair(1, 0)
            SnakeDirection.DOWN -> gameEngine.move = Pair(0, 1)
        }
    }

    /**
     * Starts the game.
     *
     * This function should always be used to start the game.
     */
    @MainThread
    fun startGame() {
        gameEngine.startGame()
    }

    /**
     * Pauses the game.
     *
     * This function should always be used to start the game.
     */
    @MainThread
    fun pauseGame() {
        //gameEngine.pauseGame()
    }

    /**
     * Tells observers that the game has ended.
     */
    @MainThread
    fun onGameEnded() {
        Log.d("Kevin", "ended")
    }


    @MainThread
    fun startNewGame() {
        //  gameEngine.resetGame()
    }

    /**
     * Tells observers that the food has been eaten.
     */
    @MainThread
    fun onFoodEaten() {
        //score++
    }


    override fun onCleared() {
        super.onCleared()
        //  gameEngine.stopGame()
    }
}
