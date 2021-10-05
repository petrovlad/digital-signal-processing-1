package by.petrovlad.dsp.enums;

import by.petrovlad.dsp.constants.AudioFormatConstants;

import static java.lang.Math.*;

public enum WaveForm {
    SINE {
        @Override
        public float[] generateSignal(int ms, float frequency, float amplitude, float phase) {
            phase = (float) (phase % (2 * PI));
            float[] buffer = new float[((int) (ms * AudioFormatConstants.SAMPLING_RATE)) / 1000];

            for (int sample = 0; sample < buffer.length; sample++) {
                double cycle = sample / AudioFormatConstants.SAMPLING_RATE;
                buffer[sample] = (float) (amplitude * sin(2 * PI * frequency * cycle + phase));
            }

            return buffer;
        }
    },
    SQUARE {
        @Override
        public float[] generateSignal(int ms, float frequency, float amplitude, float phase) {
            phase = (float) (phase % (2 * PI));
            float[] buffer = new float[((int) (ms * AudioFormatConstants.SAMPLING_RATE)) / 1000];

            for (int sample = 0; sample < buffer.length; sample++) {
                double cycle = sample / AudioFormatConstants.SAMPLING_RATE;
                double sinValue = (float) (amplitude * sin(2 * PI * frequency * cycle + phase));
                buffer[sample] = sinValue > 0 ? amplitude : -amplitude;
            }

            return buffer;
        }
    },
    TRIANGLE {
        @Override
        public float[] generateSignal(int ms, float frequency, float amplitude, float phase) {
            phase = (float) (phase % (2 * PI));
            float[] buffer = new float[((int) (ms * AudioFormatConstants.SAMPLING_RATE)) / 1000];

            for (int sample = 0; sample < buffer.length; sample++) {
                double cycle = sample / AudioFormatConstants.SAMPLING_RATE;
                buffer[sample] = (float) (2 * amplitude/PI * asin(sin((PI * frequency * cycle) + phase)));
            }

            return buffer;
        }
    },
    SAWTOOTH {
        @Override
        public float[] generateSignal(int ms, float frequency, float amplitude, float phase) {
            phase = (float) (phase % (2 * PI));
            float[] buffer = new float[((int) (ms * AudioFormatConstants.SAMPLING_RATE)) / 1000];

            for (int sample = 0; sample < buffer.length; sample++) {
                double cycle = sample / AudioFormatConstants.SAMPLING_RATE;
                buffer[sample] = (float) (2 * amplitude / PI * atan(tan((PI * frequency * cycle) + phase)));
            }

            return buffer;
        }
    },
    NOISE {
        @Override
        public float[] generateSignal(int ms, float frequency, float amplitude, float phase) {
            float[] buffer = new float[((int) (ms * AudioFormatConstants.SAMPLING_RATE)) / 1000];

            for (int sample = 0; sample < buffer.length; sample++) {
                buffer[sample] = (float) (amplitude * (random() * 2 - 1));
            }

            return buffer;
        }
    };

    public abstract float[] generateSignal(int ms, float frequency, float amplitude, float phase);
}
