package de.fuballer.mcendgame.component.dungeon.generation.layout_generator.linear

import de.fuballer.mcendgame.util.random.RandomOption

object RoomTypes {
    fun init() {}

    val STRONGHOLD_START_ROOM = RoomTypeLoader.load("dungeon/stronghold/start")
    val STRONGHOLD_BOSS_ROOM = RoomTypeLoader.load("dungeon/stronghold/boss")

    val STRONGHOLD_ROOMS = listOf(
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/arches_side-stairs")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/arena")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/around-bars_workshop-corner_curve")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/bridge_dive_plantrooms")),
        RandomOption(3, RoomTypeLoader.load("dungeon/stronghold/copper-sewers")),
        RandomOption(1, RoomTypeLoader.load("dungeon/stronghold/decayed-staircase_branching")),
        RandomOption(7, RoomTypeLoader.load("dungeon/stronghold/flat_chandelier_branching")),
        RandomOption(1, RoomTypeLoader.load("dungeon/stronghold/giant_tower")),
        RandomOption(3, RoomTypeLoader.load("dungeon/stronghold/inverted-pyramid_curve")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/loop-around-chandelier_curve")),
        RandomOption(1, RoomTypeLoader.load("dungeon/stronghold/parkour_curve")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/slimebounce")),
        RandomOption(7, RoomTypeLoader.load("dungeon/stronghold/small_connector_curve")),
        RandomOption(7, RoomTypeLoader.load("dungeon/stronghold/small_connector_sloped")),
        RandomOption(3, RoomTypeLoader.load("dungeon/stronghold/small_sewers")),
        RandomOption(5, RoomTypeLoader.load("dungeon/stronghold/small_stair-between-barrels_curve")),
        RandomOption(5, RoomTypeLoader.load("dungeon/stronghold/stair_chandelier_sidedrop_curve")),
        RandomOption(7, RoomTypeLoader.load("dungeon/stronghold/stair_elevated_branching")),
        RandomOption(3, RoomTypeLoader.load("dungeon/stronghold/stairs_statue")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/tight_hanging-lamps_curve")),
        RandomOption(7, RoomTypeLoader.load("dungeon/stronghold/tiny_flat_connector")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/tunnelbridge_curve")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/zigzag-stairs_ponds")),
        RandomOption(4, RoomTypeLoader.load("dungeon/stronghold/zigzag_waterfalls_head_curve")),
    )
}