package com.sotecorner;

import lombok.Data;

@Data
public class NpcHealth {
    private int currentHealth = 0;
    private int totalHealth = 0;

     /**
     * Returns the current NPC health remaining as a percentage of its total health.
     * i.e. if an NPC has 50/100 HP remaining it will return 50%.
     * @return An integer representing the remaining HP percentage.
     */
    public int asPercent() {
		double percent = 100.0 * currentHealth / totalHealth;
		return (int) Math.round(percent);
	}
}
