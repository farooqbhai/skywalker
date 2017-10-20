package org.speechbot.raw;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

public class Synthesizer {

    public static void main(String[] args) throws Exception {
        
        Synthesizer synthesizer = new Synthesizer();
        List<String> results = synthesizer.recordAndGetText();
        
        if (results.size() < 1) {
            System.out.print("Failed to recognize input text");
        } else {
            
            for (String synthesizedResult : results) {
                System.out.println(synthesizedResult);
            }
        }
    }

    public List<String> recordAndGetText() throws Exception {
        for (String envVar : Constants.mandatoryEnv) {
            String envData = System.getenv(envVar);
            
            if (envData == null || envData.trim().isEmpty()) {
                throw new RuntimeException(String.format("Env variable %s is not provided.", envVar));
            }
        }

        // Instantiates a client
        SpeechClient speech = SpeechClient.create();

        // The path to the audio file to transcribe
        String fileName = getRandomFileName();
        
        
        SpeechRecorder sr = new SpeechRecorder();
        sr.recordAndSaveFile(fileName, Constants.RECORD_DURATION_IN_MS);
        
        System.out.println("Reading file: " + fileName);

        // Reads the audio file into memory
        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);
        ByteString audioBytes = ByteString.copyFrom(data);

        // Builds the sync recognize request
        RecognitionConfig config = RecognitionConfig.newBuilder()
            .setEncoding(AudioEncoding.ENCODING_UNSPECIFIED)
            .setSampleRateHertz(16000)
            .setLanguageCode("en-US")
            .build();
        RecognitionAudio audio = RecognitionAudio.newBuilder()
            .setContent(audioBytes)
            .build();

        System.out.println("Requesting google api for speech recognition");
        // Performs speech recognition on the audio file
        RecognizeResponse response = speech.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        System.out.println("Total suggestions came from google api: " + results.size());
        
        List<String> synthesizedResults = new ArrayList<String>();
        for (SpeechRecognitionResult result: results) {
          // There can be several alternative transcripts for a given chunk of speech. Just use the
          // first (most likely) one here.
          SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
          //System.out.printf("Transcription: %s%n", alternative.getTranscript());
          synthesizedResults.add(alternative.getTranscript());
        }

        speech.close();
        
        return synthesizedResults;
    }
    
    public String getRandomFileName() {
        String randomFileName = UUID.randomUUID().toString() + ".wav";
        return Paths.get(System.getenv(Constants.RECORDED_FILES_DIR), randomFileName).toString();
    }
}
