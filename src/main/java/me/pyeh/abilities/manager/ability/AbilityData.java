package me.pyeh.abilities.manager.ability;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AbilityData {

    private String name;
    private String displayName;
    private boolean enabled;
    private boolean glow;
    private String itemData;
    private int itemID;
    private List<String> lore;
    private int cooldown;
}
