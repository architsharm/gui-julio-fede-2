package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.MusicTrackQueue;
import com.jmex.audio.MusicTrackQueue.RepeatType;

public class SoundManager {
	private MusicTrackQueue audioQueue;
	public AudioTrack[] pinDown;
	public AudioTrack ballMoving;
	
	public SoundManager() {
		this.createAudio();
	}

	private void createAudio() {
		audioQueue = AudioSystem.getSystem().getMusicQueue();
		audioQueue.setCrossfadeinTime(0);
		audioQueue.setRepeatType(RepeatType.NONE);
		pinDown = new AudioTrack[10];
		for( int i = 0; i < 10; i++ ) {
			pinDown[i] = getAudioTrack( "resources/Sounds/pinHitLong.wav" );
		}
		ballMoving = getAudioTrack( "resources/Sounds/ballMoving.wav" );
	}
	
	public AudioTrack getAudioTrack(String file) {
		AudioTrack track = null;
		try {
			track = AudioSystem.getSystem().createAudioTrack( new URL( "file:" + file ), false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		track.setLooping( true );
		track.setVolume( 1.0f );
		return track;
	}
	
	public void playSound(AudioTrack track, float volume ) {
		if( !track.isPlaying() ) {
			track.setMinVolume( 0 );
			track.setVolume(volume);
			audioQueue.addTrack(track);
			audioQueue.play();
		}
		AudioSystem.getSystem().update();
		AudioSystem.getSystem().fadeOutAndClear(1.5f);
	}
}
