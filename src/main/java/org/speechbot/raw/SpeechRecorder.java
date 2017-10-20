package org.speechbot.raw;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SpeechRecorder {
	
	
	/*
	 * git status
	 * git pull
	 * git push
	 * git diff
	 * 
	 * git add
	 * git commit
	 */

    public static TargetDataLine targetDataLine = null;
    
    public static AudioFormat audioFormat = null;

    public void recordAndSaveFile(String fileName, int numMiliSeconds) throws Exception {
        
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException("Audio destination filename is invalid!");
        }
        
        if (numMiliSeconds < 1000 || numMiliSeconds > 50000) {
            throw new RuntimeException("Recording should between 1 to 50 seconds");
        }

        audioFormat = new AudioFormat(16000, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException("Audio line not supported!!!");
        }
        
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        
        SpeechRecorder sr = new SpeechRecorder();
        Thread thread = new Thread(sr.new Recorder(fileName, numMiliSeconds));
        thread.start();
        
        Thread.currentThread().sleep(numMiliSeconds);
        targetDataLine.stop();
        targetDataLine.close();
    }
    
    class Recorder implements Runnable {
        
        private String wavFileName;
        
        private int numMiliSeconds;
        
        public Recorder(String fileName, int numMiliSeconds) {
            this.wavFileName = fileName;
            this.numMiliSeconds = numMiliSeconds;
        }
        
        @Override
        public void run() {
            try {
                safeRun();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        
        public void safeRun() throws Exception {
            targetDataLine.open(audioFormat);        
            targetDataLine.start();
            
            System.out.println("Started Recording..." + String.valueOf(this.numMiliSeconds/1000));
            AudioSystem.write(
                    new AudioInputStream(targetDataLine),
                    AudioFileFormat.Type.WAVE,
                    new File(this.wavFileName));
            
            System.out.println("Recording complete.");
        }
    }
}
