package com.example.snakegame.model

/**
 * Represents a State of the game.
 *
 * @param food A [Food] object which can have a x and y coordinate.
 * @param snake A List containing multiple [SnakeSegment]s in order.
 * @param currentDirection The [SnakeDirection] the snake is currently traveling.
 */
internal data class State(
    val food: Food,
    val snake: List<SnakeSegment>,
    val currentDirection: SnakeDirection,
)

/**
 * Represent each part of the Snake's body.
 *
 * @param x The x-coordinate of the segment.
 * @param y The y-coordinate of the segment.
 */
internal data class SnakeSegment(
    val x: Int,
    val y: Int,
)

/**
 * Represent the food/apple.
 *
 * @param x The x-coordinate of the food.
 * @param y The y-coordinate of the food.
 */
internal data class Food(
    val x: Int,
    val y: Int,
)

/**
 * Convince function for checking whether [SnakeSegment] and [Food] are at the same position.
 */
internal val isSamePosition: (SnakeSegment, Food) -> Boolean = { segment, food ->
    segment.x == food.x && segment.y == food.y
}