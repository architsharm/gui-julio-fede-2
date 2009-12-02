package ar.edu.itba.cg.actions;

import ar.edu.itba.cg.Bowling;
import ar.edu.itba.cg.SceneParameters;
import ar.edu.itba.cg.SoundManager;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.contact.ContactInfo;
import com.jmex.physics.material.Material;

public class BallCollisionAction extends InputAction {
	
	private SceneParameters params;
	private SoundManager soundManager;
	
    public BallCollisionAction(SceneParameters params, SoundManager soundManager) {
    	this.params = params;
    	this.soundManager = soundManager;
	}

	public void performAction( InputActionEvent evt ) {
        // something collided with th lower floor
        // we want to put everything that collides with the lower floor up again

        // as we know this action is handling collision we can cast the data to ContactInfo
        final ContactInfo contactInfo = ( (ContactInfo) evt.getTriggerData() );
        
        String name1 = contactInfo.getNode1().getName();
		String name2 = contactInfo.getNode2().getName();
		if( name1 == null || name2 == null ) {
			return;
		}		
		if( name1.startsWith( "pin" ) || name2.startsWith( "pin" ) ) {
			int pin = Integer.valueOf(name1.startsWith( "pin" ) ? name1.substring(4) : name2.substring(4)); 
			Vector3f v1 = new Vector3f();
        	Vector3f v2 = new Vector3f();
			((DynamicPhysicsNode)contactInfo.getNode1()).getLinearVelocity( v1 );
			((DynamicPhysicsNode)contactInfo.getNode2()).getLinearVelocity( v2 );
			v1.subtract( v2 );
			soundManager.playSound( soundManager.pinDown[ pin ], Math.abs(v1.length()/2) );
        }
		if( name1.startsWith( "lane" ) || name2.startsWith( "lane" ) ) {
			DynamicPhysicsNode ball;
			StaticPhysicsNode lane;
			if( name1.startsWith( "lane" ) ) {
				lane = (StaticPhysicsNode)contactInfo.getNode1();
				ball = (DynamicPhysicsNode)contactInfo.getNode2();
			}else{
				lane = (StaticPhysicsNode)contactInfo.getNode2();
				ball = (DynamicPhysicsNode)contactInfo.getNode1();
			}
			Vector3f v = new Vector3f();
			ball.getLinearVelocity(v);
			float length = v.lengthSquared();
			if( length > 1F ) {
				soundManager.playSound( soundManager.ballMoving, length / 4096 );
				if (lane.getMaterial() == Material.ICE && ball.getLocalTranslation().z < - params.LANE_LENGTH * 0.7f) {
					System.out.println("changed material");
					lane.setMaterial(Material.WOOD);
				}
			}
		}
    }
}