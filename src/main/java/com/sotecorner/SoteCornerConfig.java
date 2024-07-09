package com.sotecorner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("General")
public interface SoteCornerConfig extends Config
{

	enum Quadrant {
		NORTH_EAST,
		SOUTH_EAST,
		NORTH_WEST,
		SOUTH_WEST,
		FRONT, // For 5 man raids
	}

	enum PhaseSpec {
		P1_P2,
		P2_P3,
		P1_P3,
		FILL,
	}

	@ConfigItem(
            keyName = "quadrant",
            name = "Quadrant",
            description = "The quadrant of the room you want to call out to team-mates.",
            position = 1
    )
    default Quadrant quadrant() {
		return Quadrant.NORTH_EAST;
	}

	@ConfigItem(
			keyName = "phaseSpec",
			name = "Special Attack Phase",
			description = "The phases that you will be dropping your Dragon Warhammer special attacks.",
			position = 2
	)
	default PhaseSpec phaseSpec() {
		return PhaseSpec.P1_P2;
	}

	@Range(min = 5, max = 99)
	@ConfigItem(
		keyName = "shoutPercent",
		name = "Shout Percent",
		description = "The percentage of Nylo HP remaining to shout your Soteseg corner.",
		position = 3
	)
	default int shoutPercent() {
		return 20;
	}

	@ConfigItem(
			keyName = "chatEnter",
			name = "Enter to Chat",
			description = "Check this box if you need to press the \"Enter\" key before chatting.",
			position = 4
	)
	default boolean chatEnter() {
		return false;
	}
}
