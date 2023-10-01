package com.example.snakegame.viewmodel

import android.util.Log
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

    val gameState: StateFlow<State> = gameEngine.state
    // val scoreLiveData: LiveData<Int> = gameEngine.scoreLiveData TODO


    fun onUserInputReceived(direction: SnakeDirection) {
        when (direction) {
            SnakeDirection.UP -> gameEngine.move = Pair(0, -1)
            SnakeDirection.LEFT -> gameEngine.move = Pair(-1, 0)
            SnakeDirection.RIGHT -> gameEngine.move = Pair(1, 0)
            SnakeDirection.DOWN -> gameEngine.move = Pair(0, 1)
        }
    }

    fun startGame() {
        gameEngine.startGame()
    }

    fun pauseGame() {
        //gameEngine.pauseGame()
    }


    fun onGameEnded() {
        Log.d("Kevin", "ended")
    }


    fun startNewGame() {
        //  gameEngine.resetGame()
    }

    fun onFoodEaten() {
        //score++
    }


    override fun onCleared() {
        super.onCleared()
        //  gameEngine.stopGame()
    }
}