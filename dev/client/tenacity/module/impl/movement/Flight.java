package dev.client.tenacity.module.impl.movement;


import dev.event.EventListener;
import dev.event.impl.network.PacketReceiveEvent;
import dev.event.impl.network.PacketSendEvent;
import dev.event.impl.player.MotionEvent;
import dev.client.tenacity.hackerdetector.utils.MovementUtils;
import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.impl.movement.FastFly.FlyStage;
import dev.client.tenacity.module.impl.player.Timerr;
import dev.client.tenacity.ui.notifications.NotificationManager;
import dev.client.tenacity.ui.notifications.NotificationType;
import dev.client.tenacity.utils.player.ThePlayer;
import dev.settings.impl.ModeSetting;
import dev.settings.impl.NumberSetting;
import dev.utils.network.PacketUtils;
import dev.utils.objects.ClientUtils;
import cr.launcher.Config;
import cr.launcher.eb;
import cr.launcher.main.a;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;

import com.craftrise.dR;
import com.craftrise.gy;
import com.craftrise.jS;
import com.craftrise.lE;
import com.craftrise.lv;
import com.craftrise.pN;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public final class Flight extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "AirWalk","Vanilla","Rac","Vulcan-Fast");
    private final NumberSetting speed = new NumberSetting("Speed", 2, 5, 0, 0.1);
    private float stage;
    private int ticks;
    private boolean doFly;
    private double x, y, z;
    private ArrayList<lv> packets = new ArrayList<>();
    private boolean hasClipped;
    private double speedStage;
    public double teleport=0;
    private int tickss = 0;
	private int modifytickss = 0;
	private FlyStage stagee = FlyStage.WAITING;
	private int flags = 0;
	private double groundX = 0.0;
	private double groundY = 0.0;
	private double groundZ = 0.0;
    
    private double firstposX;
    
    private double posX,posY,posZ;
    private ScheduledExecutorService executorService;

    public Flight() {
        super("Flight", Category.MOVEMENT, "Hovers you in the air");
        speed.addParent(mode, m -> m.is("Vanilla"));
        this.addSettings(mode, speed);
    }
    public static void SetflySpeed(float value){
        try {
            Class<?> thePlayerClass = com.craftrise.mg.class;
            Field sField = thePlayerClass.getDeclaredField("S");
            sField.setAccessible(true);
            Object sObject = sField.get(a.q);
            Field hField = sObject.getClass().getDeclaredField("c");
            hField.setAccessible(true);
            hField.set(sObject, new com.craftrise.de(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void resetMotion(boolean y) {
	    a.q.bh = new dR(0.0);
	    a.q.aT = new dR(0.0);
	    if (y) {
	        a.q.bf = new dR(0.0);
	    }
	}
    public static double getY(lE packet) {
    	try {
    		Field field = lE.class.getDeclaredField("a");
    		field.setAccessible(true);
    		return (double)field.getDouble(packet);
    	}catch(Exception e) {
    		return 0;
    		//null
    	}
    }
	public static double getPosX(gy packet) {
    	try {
    		Field field = gy.class.getDeclaredField("e");
    		field.setAccessible(true);
    		return (double)field.getDouble(packet);
    	}catch(Exception e) {
    		return 0;
    		//null
    	}
    }
	public static double getPosY(gy packet) {
    	try {
    		Field field = gy.class.getDeclaredField("d");
    		field.setAccessible(true);
    		return (double)field.getDouble(packet);
    	}catch(Exception e) {
    		return 0;
    		//null
    	}
    }
	public static double getPosZ(gy packet) {
    	try {
    		Field field = gy.class.getDeclaredField("a");
    		field.setAccessible(true);
    		return (double)field.getDouble(packet);
    	}catch(Exception e) {
    		return 0;
    		//null
    	}
    }
	public static void setY(lE packet,double value) {
    	try {
    		Field field = lE.class.getDeclaredField("a");
    		field.setAccessible(true);
    		field.setDouble(packet,value);
    	}catch(Exception e) {
    		//null
    	}
    }
	 public static void setOnground(lE packet,boolean value) {
	    	try {
	    		Field field = lE.class.getDeclaredField("b");
	    		field.setAccessible(true);
	    		field.setBoolean(packet,value);
	    	}catch(Exception e) {
	    		//null
	    	}
	    }
    public static void SetPosition(double x, double y, double z){
        try{
            Class<?> Entity = com.craftrise.m9.class;
            Method setPosition = Entity.getMethod("b",double.class,double.class,double.class,long.class);
            setPosition.invoke(a.q,x,y,z,5L);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void SetisFlying(boolean value) {
        try {
            Class<?> thePlayerClass = com.craftrise.mg.class;
            Field sField = thePlayerClass.getDeclaredField("S");
            sField.setAccessible(true);
            Object sObject = sField.get(a.q);
            Field hField = sObject.getClass().getDeclaredField("h");
            hField.setAccessible(true);
            hField.set(sObject, new eb(value, cr.launcher.main.a.m));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final EventListener<MotionEvent> onMotion = e -> {
        switch (mode.getMode()) {
            case "Watchdog":
            	a.q.aY = y;
                if (a.q.s.a(5L) && stage == 0) {
                    a.q.aT = new dR(0.09);
                }
                stage++;
                if (a.q.s.a(5L) && stage > 2 && !hasClipped) {
                	a.q.z.a(new lE.a(a.q.bE,a.q.aY - 0.15,a.q.bH,false),5L);
                	a.q.z.a(new lE.a(a.q.bE,a.q.aY + 0.15,a.q.bH,true),5L);
                    hasClipped = true;
                }
                if (doFly) {
                	a.q.aT = new dR(0);
                	a.q.s = new eb(true,cr.launcher.main.a.m);
                   // mc.timer.timerSpeed = 2;
                } else {
                    //MovementUtils.setSpeed(0);
                    //mc.timer.timerSpeed = 5;
                }
                break;
            case "AirWalk":
            	a.q.aT = new dR(0);
            	a.q.s = new eb(true,cr.launcher.main.a.m);
                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 0.6);
                break;
            case "Vanilla":
            	SetisFlying(true);
            	SetflySpeed(0.2f);
                break;
                
            case "Rac":
            	SetisFlying(true);
            	SetflySpeed(0.3f);
            	posX = a.q.bE;
            	posY = a.q.aY;
            	posZ = a.q.bH;
            	break;
            case "Vulcan-Fast":
            	tickss++;
        	    modifytickss++;

        	    if (stagee == FlyStage.FLYING || stagee == FlyStage.WAITING) {
        	        if (stagee == FlyStage.FLYING) {
        	        	//Timerr.setTimerSpeed(0.140f);
        	            //mc.timer.timerSpeed = timerValue.get();
        	        } else {
        	        	//Timerr.setTimerSpeed(0.05f);
        	            // mc.timer.timerSpeed = 1.0f;
        	        }

        	        if (tickss == 2 && Keyboard.isKeyDown(Keyboard.KEY_SPACE) && modifytickss >= 6) {
        	            SetPosition(a.q.bE, a.q.aY + 0.5, a.q.bH);
        	            modifytickss = 0;
        	        }

        	        if (!MovementUtils.isMoving(a.q) && tickss == 1 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_SPACE)) && modifytickss >= 5) {
        	            double playerYaw = a.q.bL * Math.PI / 180;
        	            SetPosition(a.q.bE + 0.05 * -Math.sin(playerYaw),
        	                    a.q.aY,
        	                    a.q.bH + 0.05 * Math.cos(playerYaw));
        	        }

        	        if (tickss == 2 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && modifytickss >= 6) {
        	            SetPosition(a.q.bE, a.q.aY - 0.5, a.q.bH);
        	            modifytickss = 0;
        	        } else if (tickss == 2 && Config.gameSettings.a(Config.gameSettings.bM)) {
        	            PacketUtils.sendPacketNoEvent(new lE.a(a.q.bE + 0.05, a.q.aY, a.q.bH, true));
        	            PacketUtils.sendPacketNoEvent(new lE.a(a.q.bE, a.q.aY, a.q.bH, true));
        	            PacketUtils.sendPacketNoEvent(new lE.a(a.q.bE, a.q.aY + 0.42, a.q.bH, true));
        	            PacketUtils.sendPacketNoEvent(new lE.a(a.q.bE, a.q.aY + 0.7532, a.q.bH, true));
        	            PacketUtils.sendPacketNoEvent(new lE.a(a.q.bE, a.q.aY + 1.0, a.q.bH, true));
        	            SetPosition(a.q.bE, a.q.aY + 1.0, a.q.bH);
        	            stagee = FlyStage.WAIT_APPLY;
        	            modifytickss = 0;
        	            groundY = a.q.aY - 1.0;
        	            groundX = a.q.bE;
        	            groundZ = a.q.bH;
        	            ClientUtils.displayChatMessage("§8[§c§lVulcan-Ghost-Flight§8] §aWaiting to land...");
        	        }
        	        a.q.s = new eb(true, cr.launcher.main.a.m);
        	        a.q.aT = new dR(0.0);
        	    } else if (stagee == FlyStage.WAIT_APPLY) {
        	        //mc.timer.timerSpeed = 1.0f;
        	        resetMotion(true);
        	        if (modifytickss >= 10) {
        	            double playerYaw = a.q.bL * Math.PI / 180;
        	            if (modifytickss % 2 != 0) {
        	                SetPosition(a.q.bE + 0.1 * -Math.sin(playerYaw),
        	                        a.q.aY,
        	                        a.q.bH + 0.1 * Math.cos(playerYaw));
        	            } else {
        	                SetPosition(a.q.bE - 0.1 * -Math.sin(playerYaw),
        	                        a.q.aY,
        	                        a.q.bH - 0.1 * Math.cos(playerYaw));
        	                if (modifytickss >= 16 && tickss == 2) {
        	                    modifytickss = 16;
        	                    SetPosition(a.q.bE,
        	                            a.q.aY + 0.5,
        	                            a.q.bH);
        	                }
        	            }
        	        }
        	    }
         	    break;
           
        }
    };

    private final EventListener<PacketSendEvent> onPacketSend = e -> {
    	switch (mode.getMode()) {
   	     case "Rac":
   		 if (e.getPacket() instanceof lE) {
   			 if(cr.launcher.main.a.q.Z % 1 == 0){
                    e.cancel();
                }
   		 }
            break;
   	  case "Vulcan-Fast":
   		if (e.getPacket() instanceof lE) {
			 lE playerPacket = (lE) e.getPacket();
		     if (tickss > 2) {
		         tickss = 0;
		         double packety = getY(playerPacket);
		         packety += 0.5;
		         setY(playerPacket, packety);
		     }
		     setOnground(playerPacket,true);
		 } else if (e.getPacket() instanceof gy) {
			 gy playerPosLookPacket = (gy) e.getPacket();
		     if (stagee == FlyStage.WAITING) {
		         flags++;
		         if (flags >= 2) {
		             flags = 0;
		             stagee = FlyStage.FLYING;
		         }
		     }
		     if (stagee == FlyStage.WAIT_APPLY) {
		         if (Math.sqrt((getPosX(playerPosLookPacket) - groundX) * (getPosX(playerPosLookPacket) - groundX)
		             + (getPosZ(playerPosLookPacket) - groundZ) * (getPosZ(playerPosLookPacket) - groundZ)) < 1.4
		             && getPosY(playerPosLookPacket) >= (groundY - 0.5)) {
		             return;
		         }
		     }
		     e.cancel();
		 } else if (e.getPacket() instanceof pN) {
		     e.cancel();
		 }
		 break;
   	}
    };

   

    @Override
    public void onEnable() {
        doFly = false;
        ticks = 0;
        stage = 0;
        x = a.q.bE;
        y = a.q.aY;
        z = a.q.bH;
        hasClipped = false;
        packets.clear();
        tickss = 0;
		modifytickss = 0;
		flags = 0;
		SetPosition(a.q.bE, Math.round(a.q.aY * 2) / 2.0, a.q.bH);
		if(mode.getMode() == "Vulcan-Fast") {
		this.stagee = FlyStage.WAITING;
		ClientUtils.displayChatMessage("§8[§c§lVulcan-Ghost-Flight§8] §aPlease press Sneak before you land on ground!");
		ClientUtils.displayChatMessage("§8[§c§lVulcan-Ghost-Flight§8] §aYou can go Up/Down by pressing Jump/Sneak;");
		}
		
        super.onEnable();
    }

    @Override
    public void onDisable() {
      //  mc.timer.timerSpeed = 1;
    	if(mode.getMode() =="Vanilla") {
    		SetisFlying(false);
    	}else if(mode.getMode() == "Rac") {
    		NotificationManager.post(NotificationType.INFO, "Flight", "Please Wait A 5 Secons");
    		executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(this::performTeleport, 0, 100, TimeUnit.MILLISECONDS);
    	}else if(mode.getMode() == "AirWalk") {
    		Timerr.setTimerSpeed(0.05f);
    	}
        super.onDisable();
    }
    private void performTeleport() {
    	if(a.q.bE == posX) {
            teleport++;
            if(teleport >= 20) {
            	NotificationManager.post(NotificationType.INFO, "Flight", "Position Updated!!");
            }
		}
        if (teleport <5) {
            //SetPosition(posX, posY, posZ);
            a.q.z.a(new lE.a(posX, posY +1, posZ, true), 5L);
            a.q.z.a(new lE.a(posX, posY, posZ, true), 5L);
            a.q.z.a(new lE.a(posX, posY -1, posZ, false), 5L);
        } else {
        	teleport = 0;
            executorService.shutdown();
        }
    }
    enum FlyStage {
        WAITING,
        FLYING,
        WAIT_APPLY
    }

}
