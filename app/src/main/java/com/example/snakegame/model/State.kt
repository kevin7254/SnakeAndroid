package com.example.snakegame.model

data class State(
    val food: Food,
    val snake: List<SnakeSegment>,
    val currentDirection: SnakeDirection
)

data class SnakeSegment(
    val x: Int,
    val y: Int,
)

data class Food(
    val x: Int,
    val y: Int,
)

fun SnakeSegment.areEqual(food: Food) : Boolean {
    return (this.x == food.x) && (this.y == food.y)
}