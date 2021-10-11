package by.petrovlad.dsp.service;

import by.petrovlad.dsp.constants.AudioFormatConstants;
import by.petrovlad.dsp.enums.WaveForm;
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

    public static final float MAX_AMPLITUDE = 1;
    public static final float MIN_AMPLITUDE = -1;
    /**
     * @param carrierWaveForm waveform to be modulated
     * @param carrierFrequency its carrierFrequency
     * @param m modulation coefficient
     * @param modulator information signal
     * @return modulated carrier signal
     */
    public static float[] modulateByAmplitude(WaveForm carrierWaveForm, float carrierFrequency, float m, float[] modulator) {
        float[] result = new float[modulator.length];

        float maxM = getMaxSignalAmplitude(modulator);
        for (int i = 0; i < modulator.length; i++) {
            result[i] = carrierWaveForm.function(MAX_AMPLITUDE, carrierFrequency, 0, i) * (1 + m * modulator[i] / maxM);
        }
        return result;
    }

    /**
     * @param carrierWaveForm waveform to be modulated
     * @param carrierFrequency its carrierFrequency
     * @param m modulation coefficient
     * @param modulator information signal
     * @return modulated carrier signal
     */
    public static float[] modulateByFrequency(WaveForm carrierWaveForm, float carrierFrequency, float m, float[] modulator)  {
        float[] result = new float[modulator.length];

        float f = 0;
        for (int i = 0; i < modulator.length; i++) {
            f += 2 * PI * carrierFrequency * (1 + modulator[i]) / SAMPLING_RATE;
            result[i] = carrierWaveForm.freqModulateFunction(m, f);
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

    public static float[] generateSignal(WaveForm waveForm, int ms, float frequency, float amplitude, float phase) {
        float[] buffer = new float[((int) (ms * AudioFormatConstants.SAMPLING_RATE)) / 1000];

        for (int sample = 0; sample < buffer.length; sample++) {
            buffer[sample] = waveForm.function(amplitude, frequency, phase, sample);
        }

        return buffer;
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
        // collapse only if max amplitude above 1
        float max = max(getMaxSignalAmplitude(signalBuffer), 1);

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
