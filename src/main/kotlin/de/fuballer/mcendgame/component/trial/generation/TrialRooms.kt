package de.fuballer.mcendgame.component.trial.generation

import de.fuballer.mcendgame.component.dungeon.generation.layout_generator.linear.RoomTypeLoader

object TrialRooms {
    fun init() {}

    val ROOMS = listOf(
        RoomTypeLoader.load("trial/stronghold")
    )
}