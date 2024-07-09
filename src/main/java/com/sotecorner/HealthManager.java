package com.sotecorner;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.client.game.NPCManager;

/**
 * HealthManager
 * A helper class dedicated to estimating the current remaining HP of an
 * interactable NPC.
 */
@Slf4j
public class HealthManager {

	private NPCManager npcManager;
    private NpcHealth npcHealth = new NpcHealth();

	public HealthManager(NPCManager npcManager) {
		this.npcManager = npcManager;
	}

    /**
     * Returns an estimation for the current remaining hitpoints for the given NPC.
     * @param actor Actor The non player character who's hitpoints should be estimated.
     * @return int An integer representing the currently remaining hitpoints for the NPC.
     */
    public NpcHealth getNpcHealth(final Actor actor) {
        Integer lastMaxHealth;
        int lastRatio, lastHealthScale;

        if (actor == null) {
			return npcHealth;
		}

        if (!(actor instanceof NPC)) {
            return npcHealth;
        }

        NPC opponent = (NPC) actor;

		if (opponent.getHealthScale() > 0) {
			lastRatio = opponent.getHealthRatio();
			lastHealthScale = opponent.getHealthScale();
			lastMaxHealth = npcManager.getHealth(opponent.getId());
			npcHealth.setTotalHealth(lastMaxHealth);
		} else {
			lastRatio = lastHealthScale = lastMaxHealth = 0;
		}

        // The following calculations were taken from the OpponentInfo official RuneLite plugin.
		if (lastRatio >= 0 && lastHealthScale > 0) {
			if (lastRatio > 0) {
				int minHealth = 1;
				int maxHealth;
				if (lastHealthScale > 1) {
					if (lastRatio > 1) {
						minHealth = (lastMaxHealth * (lastRatio - 1) + lastHealthScale - 2) / (lastHealthScale - 1);
					}
					maxHealth = (lastMaxHealth * lastRatio - 1) / (lastHealthScale - 1);
					if (maxHealth > lastMaxHealth) {
						maxHealth = lastMaxHealth;
					}
				} else {
					maxHealth = lastMaxHealth;
				}
                npcHealth.setCurrentHealth((minHealth + maxHealth + 1) / 2);
			}
		}
        return npcHealth;
    }
}
