package ar.edu.itba.cg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.model.collada.ColladaImporter;

public class ColladaModelLoader {

	public Node getModel(String filename) {
		// TODO Auto-generated method stub
		//this stream points to the model itself.
        InputStream mobboss = null;
		try {
			mobboss = new FileInputStream( new File( filename ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
        //tell the importer to load the mob boss
        ColladaImporter.load(mobboss, "model");
        //we can then retrieve the skin from the importer as well as the skeleton
        Spatial sp = ColladaImporter.getModel();
                
        //all done clean up.
        ColladaImporter.cleanUp();
		return ((Node)sp);
	}
}
