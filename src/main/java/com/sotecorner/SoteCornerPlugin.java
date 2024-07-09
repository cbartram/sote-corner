package com.sotecorner;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Sote Shouter"
)
public class SoteCornerPlugin extends Plugin
{
	private static final Set<Integer> nyloIds = ImmutableSet.of(
			NpcID.NYLOCAS_VASILIAS,
			NpcID.NYLOCAS_VASILIAS_8355,
			NpcID.NYLOCAS_VASILIAS_8356,
			NpcID.NYLOCAS_VASILIAS_8357,
			NpcID.NYLOCAS_VASILIAS_11185,
			NpcID.NYLOCAS_VASILIAS_10786,
			NpcID.NYLOCAS_VASILIAS_10787,
			NpcID.NYLOCAS_VASILIAS_10788,
			NpcID.NYLOCAS_VASILIAS_10789,
			NpcID.NYLOCAS_VASILIAS_10810,
			NpcID.NYLOCAS_VASILIAS_10807,
			NpcID.NYLOCAS_VASILIAS_10808,
			NpcID.NYLOCAS_VASILIAS_10809
	);

	@Inject
	private Client client;

	@Inject
	private SoteCornerConfig config;

	@Inject
	private NPCManager npcManager;

	@Getter(AccessLevel.PACKAGE)
	private Actor lastOpponent;

	@Getter(AccessLevel.PACKAGE)
	private HealthManager healthManager;

	@Getter
	private NpcHealth npcHealth;

	@Provides
	public SoteCornerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoteCornerConfig.class);
	}
	
	@Override
	protected void startUp() throws Exception {
		log.info("Starting Sote Shouter");
		healthManager = new HealthManager(npcManager);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Shutting Down Sote Shouter");
		healthManager = null;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (lastOpponent == null) {
			return;
		}

		// The player is actively interacting with an opponent
		npcHealth = healthManager.getNpcHealth(lastOpponent);
		log.info("{} Health: {}/{} ({})", lastOpponent.getName(), npcHealth.getCurrentHealth(), npcHealth.getTotalHealth(), healthManager.asPercent());
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		if (event.getSource() != client.getLocalPlayer()) {
			return;
		}

		NPC opponent = (NPC) event.getTarget();
		if (opponent == null) {
			return;
		}

		// Only set the opponent if it is the Nylo Boss. We don't care
		// about other NPC's
		if (nyloIds.contains(opponent.getId())) {
			log.info("NPC is a Nylocas.");
			lastOpponent = opponent;
		} else {
			// TODO remove this else when ready to actually test in TOB
			lastOpponent = opponent;
		}
	}

}
