package ar.edu.itba.cg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import com.jme.animation.AnimationController;
import com.jme.animation.Bone;
import com.jme.animation.BoneAnimation;
import com.jme.animation.SkinNode;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jmex.model.collada.ColladaImporter;
import com.jmex.physics.DynamicPhysicsNode;

public class ColladaModelLoader {

	public Spatial getModel(String filename) {
		// TODO Auto-generated method stub
		//this stream points to the model itself.
        InputStream mobboss = null;
		try {
			mobboss = new FileInputStream( new File( filename ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
        //this stream points to the animation file. Note: You don't necessarily
        //have to split animations out into seperate files, this just helps.
        InputStream animation = null;
		try {
			animation = new FileInputStream( new File( "resources/man_walk.dae" ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
        //tell the importer to load the mob boss
        ColladaImporter.load(mobboss, "model");
        //we can then retrieve the skin from the importer as well as the skeleton
        SkinNode sn = ColladaImporter.getSkinNode(ColladaImporter.getSkinNodeNames().get(0));
        Bone skel = ColladaImporter.getSkeleton(ColladaImporter.getSkeletonNames().get(0));
        //clean up the importer as we are about to use it again.
        ColladaImporter.cleanUp();
        
        //load the animation file.
        ColladaImporter.load(animation, "anim");
        //this file might contain multiple animations, (in our case it's one)
        ArrayList<String> animations = ColladaImporter.getControllerNames();
        if(animations != null) {
	        //Obtain the animation from the file by name
	        BoneAnimation anim1 = ColladaImporter.getAnimationController(animations.get(0));
	        
	        //set up a new animation controller with our BoneAnimation
	        AnimationController ac = new AnimationController();
	        ac.addAnimation(anim1);
	        ac.setRepeatType(Controller.RT_WRAP);
	        ac.setActive(true);
	        ac.setActiveAnimation(anim1);
	        
	        //assign the animation controller to our skeleton
	        skel.addController(ac);
        }
        
        //attach the skeleton and the skin to the rootnode. Skeletons could possibly
        //be used to update multiple skins, so they are seperate objects.
        //rootNode.attachChild(skel);
        sn.attachChild(skel);
        
        //all done clean up.
        ColladaImporter.cleanUp();
		return sn;
	}
}
