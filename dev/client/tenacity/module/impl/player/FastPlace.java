package dev.client.tenacity.module.impl.player;

import java.lang.reflect.Field;

import cr.launcher.Config;
import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.utils.Wrapper;
import dev.event.EventListener;
import dev.event.impl.player.MotionEvent;
import dev.settings.impl.BooleanSetting;
import dev.settings.impl.NumberSetting;

public final class FastPlace extends Module {

    private final NumberSetting ticks = new NumberSetting("Ticks", 0, 4, 0, 1);
    private final BooleanSetting blocks = new BooleanSetting("Blocks", true);
    private final BooleanSetting projectiles = new BooleanSetting("Projectiles", true);
    
    public static void setRightClickDelayTimer(float value) {
        try {
            Class MinecraftClazz = Class.forName("com.craftrise.client.S");

            for(Field f : MinecraftClazz.getDeclaredFields()) {
                if(f.getName().equals("cB")) {
                    f.setAccessible(true);
                    f.set((Object) Config.getMinecraft(), value);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            
        }
    }

    private final EventListener<MotionEvent> motionEventEventListener = event -> {
    	setRightClickDelayTimer(Math.min(0, ticks.getValue().intValue()));
        
    };

    @Override
    public void onDisable() {
    	setRightClickDelayTimer(4);
        super.onDisable();
    }
    public FastPlace() {
        super("FastPlace", Category.PLAYER, "place blocks fast");
        this.addSettings(ticks, blocks, projectiles);
    }
    
}
