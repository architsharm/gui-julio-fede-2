@echo off

cd ..
java -Xmx256m -Djava.library.path=lib/lwjgl/native/windows_i586;lib/jogl/native/windows_i586 -cp lib/jme-colladabinding.jar;lib/jmePhysics.jar;lib/jme/jme-audio.jar;lib/jme/jme-awt.jar;lib/jme/jme-collada.jar;lib/jme/jme-editors.jar;lib/jme/jme-effects.jar;lib/jme/jme-font.jar;lib/jme/jme-gamestates.jar;lib/jme/jme-model.jar;lib/jme/jme-ogrexml.jar;lib/jme/jme-scene.jar;lib/jme/jme-swt.jar;lib/jme/jme-terrain.jar;lib/jme/jme.jar;lib/jogl/gluegen-rt.jar;lib/jogl/jogl.jar;lib/jorbis/jorbis-0.0.17.jar;lib/lwjgl/jinput.jar;lib/lwjgl/lwjgl_test.jar;lib/lwjgl/lwjgl_util_applet.jar;lib/lwjgl/lwjgl_util.jar;lib/lwjgl/lwjgl-debug.jar;lib/lwjgl/lwjgl.jar;lib/jbullet/jbullet.jar;lib/jbullet/stack-alloc.jar;lib/jbullet/vecmath.jar;build ar.edu.itba.cg.Bowling %@
