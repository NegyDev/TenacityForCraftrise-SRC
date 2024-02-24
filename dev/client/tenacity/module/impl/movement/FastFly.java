package dev.client.tenacity.module.impl.movement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.craftrise.ah;
import com.craftrise.dR;
import com.craftrise.de;
import com.craftrise.gR;
import com.craftrise.gy;
import com.craftrise.iE;
import com.craftrise.lE;
import com.craftrise.m9;
import com.craftrise.pN;

import cr.launcher.Config;
import cr.launcher.eb;
import cr.launcher.main.a;
import dev.client.tenacity.hackerdetector.utils.MovementUtils;
import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.event.EventListener;
import dev.event.impl.network.PacketSendEvent;
import dev.event.impl.player.MotionEvent;
import dev.utils.network.PacketUtils;
import dev.utils.objects.ClientUtils;

public class FastFly extends Module{
	private int tickss = 0;
	private int modifytickss = 0;
	private FlyStage stage = FlyStage.WAITING;
	private int flags = 0;
	private double groundX = 0.0;
	private double groundY = 0.0;
	private double groundZ = 0.0;
	
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
	public void resetMotion(boolean y) {
	    a.q.bh = new dR(0.0);
	    a.q.aT = new dR(0.0);
	    if (y) {
	        a.q.bf = new dR(0.0);
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
	 public static void setPressed(com.craftrise.client.gR bM,boolean value) {
	    	try {
	    		Field field = gR.class.getDeclaredField("b");
	    		field.setAccessible(true);
	    		field.setBoolean(bM,value);
	    	}catch(Exception e) {
	    		//null
	    	}
	    }
	 public static List getCollisionBoxes(iE box,ah entity) {
	    	try {
	    		Method method = ah.class.getDeclaredMethod("a",Long.class);
	    		method.setAccessible(true);
	    		return (List)method.invoke(box,entity,5L);
	    	}catch(Exception e) {
	    		return null;
	    		//null
	    	}
	  }
	 
	 public static ah getBoundingBox(m9 packet) {
	    	try {
	    		Field field = m9.class.getDeclaredField("aW");
	    		field.setAccessible(true);
	    		return (ah) field.get(packet);
	    	}catch(Exception e) {
	    		return null;
	    		//null
	    	}
	    }


	@Override
    public void onEnable() {
		tickss = 0;
		modifytickss = 0;
		flags = 0;
		SetPosition(a.q.bE, Math.round(a.q.aY * 2) / 2.0, a.q.bH);
		this.stage = FlyStage.WAITING;
		ClientUtils.displayChatMessage("§8[§c§lVulcan-Ghost-Flight§8] §aPlease press Sneak before you land on ground!");
		ClientUtils.displayChatMessage("§8[§c§lVulcan-Ghost-Flight§8] §aYou can go Up/Down by pressing Jump/Sneak;");

	}
	
	private final EventListener<MotionEvent> onMotion = e -> {
	    tickss++;
	    modifytickss++;

	    if (stage == FlyStage.FLYING || stage == FlyStage.WAITING) {
	        if (stage == FlyStage.FLYING) {
	            //mc.timer.timerSpeed = timerValue.get();
	        } else {
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
	            stage = FlyStage.WAIT_APPLY;
	            modifytickss = 0;
	            groundY = a.q.aY - 1.0;
	            groundX = a.q.bE;
	            groundZ = a.q.bH;
	            ClientUtils.displayChatMessage("§8[§c§lVulcan-Ghost-Flight§8] §aWaiting to land...");
	        }
	        a.q.s = new eb(true, cr.launcher.main.a.m);
	        a.q.aT = new dR(0.0);
	    } else if (stage == FlyStage.WAIT_APPLY) {
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
	};

	 private final EventListener<PacketSendEvent> onPacketSend = e -> {
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
		     if (stage == FlyStage.WAITING) {
		         flags++;
		         if (flags >= 2) {
		             flags = 0;
		             stage = FlyStage.FLYING;
		         }
		     }
		     if (stage == FlyStage.WAIT_APPLY) {
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
		 e.cancel();

	 };
	
	public FastFly() {
        super("FastFly", Category.MOVEMENT, "Hovers you in the air");
        this.setKey(Keyboard.KEY_F);
    }
	
	enum FlyStage {
        WAITING,
        FLYING,
        WAIT_APPLY
    }

}
