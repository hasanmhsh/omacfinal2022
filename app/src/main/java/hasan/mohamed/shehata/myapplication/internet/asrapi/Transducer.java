/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hasan.mohamed.shehata.myapplication.internet.asrapi;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.annotation.NonNull;
public class Transducer {



    private static final int PEAK_LIMIT = 50 * 30;

    private static final int TIME_ASR_THRESHOLD = 4 * 500;
    private static final int ASR_TIME_THRESHOLD_UPPER = 30000;


    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{4 * 4000, 10000+(25*41), 2*(10000+(25*41)), 44100};

    private static final int MEDIUM = AudioFormat.CHANNEL_IN_MONO;
    private static final int COMPRESSION = AudioFormat.ENCODING_PCM_16BIT;

    public static abstract class Provider {

        public void beginSpeaking() {
        }

        public void speaking(byte[] data, int size) {
        }

        public void speakingStopped() {
        }
    }

    private final Provider mProvider;

    private AudioRecord cassette;

    private Thread backgroundWorker;

    private AudioRecord initMicrophone() {
        for (int bytesPerSec : SAMPLE_RATE_CANDIDATES) {
            final int bufferLength = AudioRecord.getMinBufferSize(bytesPerSec, MEDIUM, COMPRESSION);
            if (bufferLength == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }
            final AudioRecord microphone = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    bytesPerSec, MEDIUM, COMPRESSION, bufferLength);
            if (microphone.getState() == AudioRecord.STATE_INITIALIZED) {
                tempdata = new byte[bufferLength];
                return microphone;
            } else {
                microphone.release();
            }
        }
        return null;
    }
    private byte[] tempdata;

    private final Object hardner = new Object();

    private long maxValue = Long.MAX_VALUE;

    private long minValue;

    public Transducer(@NonNull Provider provider) {
        mProvider = provider;
    }


    public void begin() {
        end();
        cassette = initMicrophone();
        if (cassette == null) {
            throw new RuntimeException("Failed to create asr");
        }
        cassette.startRecording();
        backgroundWorker = new Thread(new startOperationOverASR());
        backgroundWorker.start();
    }

    public void end() {
        synchronized (hardner) {
            ignore();
            if (backgroundWorker != null) {
                backgroundWorker.interrupt();
                backgroundWorker = null;
            }
            if (cassette != null) {
                cassette.stop();
                cassette.release();
                cassette = null;
            }
            tempdata = null;
        }
    }

    public void ignore() {
        if (maxValue != Long.MAX_VALUE) {
            maxValue = Long.MAX_VALUE;
            mProvider.speakingStopped();
        }
    }
    private class startOperationOverASR implements Runnable {
        private void end() {
            maxValue = Long.MAX_VALUE;
            mProvider.speakingStopped();
        }


        @Override
        public void run() {
            while (true) {
                synchronized (hardner) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    final int length = cassette.read(tempdata, 0, tempdata.length);
                    final long current = System.currentTimeMillis();
                    if (utteranceDetectionResult(tempdata, length)) {
                        if (maxValue == Long.MAX_VALUE) {
                            minValue = current;
                            mProvider.beginSpeaking();
                        }
                        mProvider.speaking(tempdata, length);
                        maxValue = current;
                        if (current - minValue > ASR_TIME_THRESHOLD_UPPER) {
                            end();
                        }
                    } else if (maxValue != Long.MAX_VALUE) {
                        mProvider.speaking(tempdata, length);
                        if (current - maxValue > TIME_ASR_THRESHOLD) {
                            end();
                        }
                    }
                }
            }
        }

        private boolean utteranceDetectionResult(byte[] buffer, int size) {
            for (int j = 0; j < size - 1; j += 2) {
                int dataOffset = buffer[j + 1];
                if (dataOffset < 0) dataOffset *= -1;
                dataOffset <<= 8;
                dataOffset += Math.abs(buffer[j]);
                if (dataOffset > PEAK_LIMIT) {
                    return true;
                }
            }
            return false;
        }

    }
    public int getPcmPerSecond() {
        if (cassette != null) {
            return cassette.getSampleRate();
        }
        return 0;
    }






}
