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
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
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

	private HealthManager healthManager;
	private NpcHealth npcHealth;
	private Robot r;
	private boolean shouted = false;

	@Provides
	public SoteCornerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoteCornerConfig.class);
	}
	
	@Override
	protected void startUp() throws Exception {
		log.info("Starting Sote Shouter");
		healthManager = new HealthManager(npcManager);
		try {
			r = new Robot();
		} catch (AWTException e) {
			log.error("AWTException thrown while attempting to shout the sote corner.");
			e.printStackTrace();
		}
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("Shutting Down Sote Shouter");
		healthManager = null;
		r = null;
		shouted = false;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (lastOpponent == null) {
			return;
		}

		// The player is actively interacting with an opponent
		npcHealth = healthManager.getNpcHealth(lastOpponent);
		log.info("Shouted: {}", shouted);
		if (npcHealth.asPercent() <= config.shoutPercent() && npcHealth.getTotalHealth() != 0 && !shouted) {
			log.info("Sending Sote corner: Quadrant={}, Phase={}, Health={}%", config.quadrant(), config.phaseSpec(), config.shoutPercent());

			if(config.chatEnter()) {
				log.info("Pre-enter enabled");
				r.keyPress(KeyEvent.VK_ENTER);
				r.delay(10);
				r.keyRelease(KeyEvent.VK_ENTER);
			}

			ArrayList<Integer> quadrantKeys = quadrantToKeys();
			quadrantKeys.add(KeyEvent.VK_SPACE);

			ArrayList<Integer> phaseSpecKeys = phaseSpecToKeys();
			phaseSpecKeys.add(KeyEvent.VK_ENTER);

			quadrantKeys.addAll(phaseSpecKeys);

			for (Integer key : quadrantKeys) {
				r.keyPress(key);
				r.delay(10);
				r.keyRelease(key);
			}
			shouted = true;
		}
		log.info("{} Health: {}/{} ({}%)", lastOpponent.getName(), npcHealth.getCurrentHealth(), npcHealth.getTotalHealth(), npcHealth.asPercent());
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


	private ArrayList<Integer> quadrantToKeys() {
		SoteCornerConfig.Quadrant quadrant = config.quadrant();
		String name = quadrant.name();
		ArrayList<Integer> keys = new ArrayList<>();
		if (quadrant == SoteCornerConfig.Quadrant.FRONT) {
			Collections.addAll(keys, KeyEvent.VK_F, KeyEvent.VK_R, KeyEvent.VK_O, KeyEvent.VK_N, KeyEvent.VK_T);
			return keys;
		}

		if(name.startsWith("NORTH")) {
			keys.add(KeyEvent.VK_N);
		}

		if(name.startsWith("SOUTH")) {
			keys.add(KeyEvent.VK_S);
		}

		String secondDirection = name.substring(name.indexOf("_") + 1);

		if(secondDirection.startsWith("EAST")) {
			keys.add(KeyEvent.VK_E);
		}

		if(secondDirection.startsWith("WEST")) {
			keys.add(KeyEvent.VK_W);
		}

		return keys;
	}

	private ArrayList<Integer> phaseSpecToKeys() {
		ArrayList<Integer> keys = new ArrayList<>();
		String name = config.phaseSpec().name();

		if(config.phaseSpec() == SoteCornerConfig.PhaseSpec.FILL) {
			Collections.addAll(keys, KeyEvent.VK_F, KeyEvent.VK_I, KeyEvent.VK_L, KeyEvent.VK_L);
			return keys;
		}

		// Specs are PX_PY this simply grabs the x and y values from any phase spec enum.
		char[] c = name.toCharArray();
		char specOne = c[1];
		char specTwo = c[c.length - 1];

		if(specOne == '1') {
			keys.add(KeyEvent.VK_1);
		}

		if(specOne == '2') {
			keys.add(KeyEvent.VK_2);
		}

		if(specTwo == '2') {
			keys.add(KeyEvent.VK_2);
		}

		if(specTwo == '3') {
			keys.add(KeyEvent.VK_3);
		}

		return keys;
	}

}
