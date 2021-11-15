package me.pyeh.abilities;

import lombok.Getter;
import me.pyeh.abilities.manager.time.TimerManager;

public class IAbilitiesAPI {

    @Getter public static IAbilitiesAPI instance;
    @Getter public TimerManager timerManager;

    public IAbilitiesAPI() {
        instance = this;
        timerManager = new TimerManager();
    }

}
