package de.fuballer.mcendgame.component.trial.generation

import de.fuballer.mcendgame.component.trial.generation.world.TrialWorldService
import de.fuballer.mcendgame.framework.annotation.Service
import org.bukkit.Location
import java.util.*

@Service
class TrialGenerationService(
    private val trialWorldService: TrialWorldService,
) {
    fun generateTrial(
        leaveLocation: Location
    ): Location {
        val seed = "${UUID.randomUUID()}".hashCode().toLong()
        val world = trialWorldService.createTrialWorld(seed)

        val startLocation = Location(world, 0.0, 0.0, 0.0)
        return startLocation
    }
}