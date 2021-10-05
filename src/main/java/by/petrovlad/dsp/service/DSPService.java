package by.petrovlad.dsp.service;

import by.petrovlad.dsp.constants.AudioFormatConstants;
import lombok.SneakyThrows;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import static by.petrovlad.dsp.constants.AudioFormatConstants.IS_BIG_ENDIAN;
import static by.petrovlad.dsp.constants.AudioFormatConstants.SAMPLING_RATE;
import static java.lang.Math.*;

public class DSPService {
    /**
     * @param carrier wave to be modulated
     * @param modulator information signal
     * @param m modulation coefficient
     * @return modulated carrier signal
     */
    public static float[] modulateByAmplitude(float[] carrier, float[] modulator, float m) {
        float[] result = new float[modulator.length];

        float maxM = 0;
        for (float val : modulator) {
            float absVal = abs(val);
            if (absVal > maxM) {
                maxM = absVal;
            }
        }

        for (int i = 0; i < modulator.length; i++) {
            result[i] = carrier[i] * (1 + m * modulator[i] / maxM);
        }
        return result;
    }

    /**
     * Carrier is always a SINE.
     *
     * @param modulator information signal
     * @param frequency frequency of SINE carrier signal
     * @param m modulation coefficient
     * @return modulated carrier signal
     */
    public static float[] modulateByFrequency(float[] modulator, float frequency, float m) {
        float[] result = new float[modulator.length];

        float f = 0;
        for (int i = 0; i < modulator.length; i++) {
            f += 2 * PI * frequency * (1 + modulator[i]) / SAMPLING_RATE;
            result[i] = (float) (sin(m * f));
        }

        return result;
    }

    public static float[] mixComplexSignal(List<float[]> waveSignals) {
        // all the signals have the same length, so take first
        float[] resultSignal = new float[waveSignals.get(0).length];
        for (float[] signal : waveSignals) {
            for (int i = 0; i < signal.length; i++) {
                resultSignal[i] += signal[i];
            }
        }
        return resultSignal;
    }

    @SneakyThrows
    public static void writeSignalToFile(float[] buffer, String filepath) {
        writeWAVToFile(convertToDigital(buffer), filepath);
    }

    @SneakyThrows
    private static void writeWAVToFile(byte[] buffer, String filepath) {
        File outFile = new File(filepath);
        AudioFormat format = new AudioFormat(SAMPLING_RATE, AudioFormatConstants.BITS_PER_SAMPLE, AudioFormatConstants.CHANNELS_COUNT, AudioFormatConstants.IS_SIGNED, AudioFormatConstants.IS_BIG_ENDIAN);
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(byteInputStream, format, buffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outFile);
        audioInputStream.close();
    }

    // works with 16-bits-per-sample ONLY
    private static byte[] convertToDigital(float[] signalBuffer) {
        // double-sized because we use 16-bits-per-sample
        // i should rewrite it to generify but i don't give a shit, Antoha
        byte[] byteBuffer = new byte[signalBuffer.length * 2];

        // find max amplitude to collapse signal to [-1..1]
        float max = getMaxSignalAmplitude(signalBuffer);

        int count = 0;
        int higherIndex = IS_BIG_ENDIAN ? 0 : 1;
        int lowerIndex = IS_BIG_ENDIAN ? 1 : 0;
        while (lowerIndex < byteBuffer.length) {
            // 0x1234
            int x = (int) ((signalBuffer[count++] / max) * Short.MAX_VALUE);
            // 0x34
            byteBuffer[lowerIndex] = (byte) x;
            // 0x12
            byteBuffer[higherIndex] = (byte) (x >> 8);

            lowerIndex += 2;
            higherIndex += 2;
        }

        return byteBuffer;
    }

    private static float getMaxSignalAmplitude(float[] signalBuffer) {
        float max = signalBuffer[0];
        for (int i = 1; i < signalBuffer.length; i++) {
            if (signalBuffer[i] > max) {
                max = signalBuffer[i];
            }
        }
        return max;
    }
}
