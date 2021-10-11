package by.petrovlad.dsp;

import by.petrovlad.dsp.enums.WaveForm;
import lombok.SneakyThrows;

import java.util.*;
import java.util.regex.Pattern;

import static by.petrovlad.dsp.service.DSPService.*;

public class Main {

    private static final int WAVEFORMS_COUNT = 5;
    private static final int BITMASK = 0x0001;

    private static final String OUTPUT_FILEPATH = "./sonic/aboba.wav";
    private static final String AMPLITUDE_MODULATED_FILEPATH = "./sonic/aboba_ampl_modulated.wav";
    private static final String FREQUENCY_MODULATED_FILEPATH = "./sonic/aboba_freq_modulated.wav";

    private static final float CARRIER_FREQUENCY = 440;
    private static final float MODULATION_COEFFICIENT = 0.5f;

    /*
        00001 - синусоида;
        00010 - импульс с различной скважностью;
        00100 - треугольная;
        01000 - пилообразная;
        10000 - шум.
     */
    @SneakyThrows
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.printf("Enter audio length in ms:%n> ");
        int ms = in.nextInt();

        System.out.printf("Enter binary pattern:%n> ");
        Pattern binaryPattern = Pattern.compile("[01]{1," + WAVEFORMS_COUNT + "}");
        while (!in.hasNext(binaryPattern)) {
            System.out.printf("Pls enter binary %d-digit value.%n", WAVEFORMS_COUNT);
            in.next();
        }
        String binaryStr = in.next();
        int binaryValue = Integer.valueOf(binaryStr, 2);

        List<float[]> waveSignals = new ArrayList<>();
        for (int i = 0; i < WAVEFORMS_COUNT; i++) {
            if ((binaryValue >> i & BITMASK) == 1) {
                WaveForm currentWaveForm = WaveForm.values()[i];

                float frequency = 0;
                float phase = 0;

                if (!currentWaveForm.equals(WaveForm.NOISE)) {
                    System.out.printf("Enter frequency for %s signal:%n> ", currentWaveForm);
                    frequency = in.nextFloat();

                    System.out.printf("Enter phase for %s signal:%n> ", currentWaveForm);
                    phase = in.nextFloat();
                }

                System.out.printf("Enter amplitude for %s signal (from -1.0 to 1.0):%n> ", currentWaveForm);
                float amplitude = in.nextFloat();
                while (amplitude > MAX_AMPLITUDE || amplitude < MIN_AMPLITUDE) {
                    System.out.printf("Nope, try again.%n");
                    System.out.printf("Enter amplitude for %s signal (from -1.0 to 1.0):%n> ", currentWaveForm);
                    amplitude = in.nextFloat();
                }

                waveSignals.add(generateSignal(currentWaveForm, ms, frequency, amplitude, phase));
            }
        }

        // save polyharmonic signal
        float[] resultSignal = mixComplexSignal(waveSignals);
        writeSignalToFile(resultSignal, OUTPUT_FILEPATH);
        // save amplitude modulation results
        float[] modulatedByAmplitude = modulateByAmplitude(WaveForm.SINE, CARRIER_FREQUENCY, MODULATION_COEFFICIENT, resultSignal);
        writeSignalToFile(modulatedByAmplitude, AMPLITUDE_MODULATED_FILEPATH);
        // save frequency modulation results
        float[] modulatedByFrequency = modulateByFrequency(WaveForm.SQUARE, CARRIER_FREQUENCY, MODULATION_COEFFICIENT, resultSignal);
        writeSignalToFile(modulatedByFrequency, FREQUENCY_MODULATED_FILEPATH);
    }
}
