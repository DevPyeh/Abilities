package me.pyeh.abilities.manager.time;

import lombok.Getter;
import me.pyeh.abilities.IAbilities;
import me.pyeh.abilities.manager.ability.type.*;
import me.pyeh.abilities.utils.ColorUtils;
import me.pyeh.abilities.utils.CooldownUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TimerManager {

    private final List<Listener> timersHandlers;
    public final TeleportAbility teleportAbility;
    public final BlockAntiTrapperAbility blockAntiTrapperAbility;
    public final AntiBuildAbility antiBuildAbility;
    public final BrewingRefillAbility brewingRefillAbility;
    public final CocaineAbility cocaineAbility;
    public final DistortionAbility distortionAbility;
    public final FakeLogoutAbility fakeLogoutAbility;
    public final FakePearlAbility fakePearlAbility;
    public final GrapplingHookAbility grapplingHookAbility;
    public final CounterPotionAbility counterPotionAbility;

    public final CooldownUtils globalCooldown;

    public TimerManager() {
        this.timersHandlers = new ArrayList<>();

        this.teleportAbility = new TeleportAbility();
        this.blockAntiTrapperAbility = new BlockAntiTrapperAbility();
        this.antiBuildAbility = new AntiBuildAbility();
        this.brewingRefillAbility = new BrewingRefillAbility();
        this.cocaineAbility = new CocaineAbility();
        this.distortionAbility = new DistortionAbility();
        this.fakeLogoutAbility = new FakeLogoutAbility();
        this.fakePearlAbility = new FakePearlAbility();
        this.grapplingHookAbility = new GrapplingHookAbility();
        this.counterPotionAbility = new CounterPotionAbility();

        this.globalCooldown = new CooldownUtils();
    }

    private void registerTimerAbilities() {
        this.timersHandlers.add(new TeleportAbility());
        this.timersHandlers.add(new BlockAntiTrapperAbility());
        this.timersHandlers.add(new AntiBuildAbility());
        this.timersHandlers.add(new BrewingRefillAbility());
        this.timersHandlers.add(new CocaineAbility());
        this.timersHandlers.add(new DistortionAbility());
        this.timersHandlers.add(new FakeLogoutAbility());
        this.timersHandlers.add(new FakePearlAbility());
        this.timersHandlers.add(new GrapplingHookAbility());
        this.timersHandlers.add(new CounterPotionAbility());
    }

    public void enabled() {
        this.registerTimerAbilities();
        ColorUtils.log("  &6&l♦ &eThe global cooldown is currently " + (this.globalCooldown() ? "&aEnabled&e!" : "&cDisabled&e!"));
        ColorUtils.log("  &6&l♦ &f" + this.timersHandlers.size() + "&e abilities timers registered successfully.");
    }

    public void disable() {
        this.timersHandlers.clear();
        ColorUtils.log("  &6&l♦ &eThe abilities cooldown was successfully saved.");
        ColorUtils.log("&6===&e=============================================&6===");
    }

    private boolean globalCooldown() {
        return IAbilities.getInstance().getConfigurationFile().getBoolean("GLOBAL_COOLDOWN");
    }


    public void executeCooldown(Player player, String abilityName, int value) {
        switch (abilityName) {
            case "TELEPORT": {
                this.getTeleportAbility().getCooldown().remove(player, abilityName);
                this.getTeleportAbility().getCooldown().apply(player, value * 1000L, abilityName);
                break;
            }
            case "BLOCK_ANTI_TRAPPER": {
                this.getBlockAntiTrapperAbility().getCooldown().remove(player, abilityName);
                this.getBlockAntiTrapperAbility().getCooldown().apply(player, value * 1000L, abilityName);
                break;
            }
            case "ANTI_BUILD": {
                this.getAntiBuildAbility().getCooldown().remove(player, abilityName);
                this.getAntiBuildAbility().getCooldown().apply(player, value * 1000L, abilityName);
                break;
            }
            case "BREWING_REFILL": {
                this.getBrewingRefillAbility().getCooldown().remove(player, abilityName);
                this.getBrewingRefillAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
            case "COCAINE": {
                this.getCocaineAbility().getCooldown().remove(player, abilityName);
                this.getCocaineAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
            case "DISTORTION": {
                this.getDistortionAbility().getCooldown().remove(player, abilityName);
                this.getDistortionAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
            case "FAKE_LOGOUT": {
                this.getFakeLogoutAbility().getCooldown().remove(player, abilityName);
                this.getFakeLogoutAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
            case "FAKE_PEARL": {
                this.getFakePearlAbility().getCooldown().remove(player, abilityName);
                this.getFakePearlAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
            case "GRAPPLING_HOOK": {
                this.getGrapplingHookAbility().getCooldown().remove(player, abilityName);
                this.getGrapplingHookAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
            case "COUNTER_POTION": {
                this.getCounterPotionAbility().getCooldown().remove(player, abilityName);
                this.getCounterPotionAbility().getCooldown().apply(player, value * 1000L, abilityName);
            }
        }
    }



}
