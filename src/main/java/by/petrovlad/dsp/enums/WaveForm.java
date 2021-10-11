package by.petrovlad.dsp.enums;

import by.petrovlad.dsp.constants.AudioFormatConstants;

import static java.lang.Math.*;

public enum WaveForm {
    SINE {
        @Override
        public float function(float amplitude, float frequency,  float phase, int n) {
            float cycle = n / AudioFormatConstants.SAMPLING_RATE;
            return (float) (amplitude * sin(2 * PI * frequency * cycle + phase));
        }

        @Override
        public float freqModulateFunction(float m, float f) {
            return (float) (sin(m * f));
        }
    },
    SQUARE {
        @Override
        public float function(float amplitude, float frequency, float phase, int n) {
            float cycle = n / AudioFormatConstants.SAMPLING_RATE;
            double sinValue = (float) (amplitude * sin(2 * PI * frequency * cycle + phase));
            return sinValue > 0 ? amplitude : -amplitude;
        }

        @Override
        public float freqModulateFunction(float m, float f) {
            return (sin(m * f)) > 0 ? 1 : -1;
        }
    },
    TRIANGLE {
        @Override
        public float function(float amplitude, float frequency, float phase, int n) {
            float cycle = n / AudioFormatConstants.SAMPLING_RATE;
            return (float) (2 * amplitude/PI * asin(sin((PI * frequency * cycle) + phase)));
        }

        @Override
        public float freqModulateFunction(float m, float f) {
            return (float) (asin(sin(m * f)));
        }
    },
    SAWTOOTH {
        @Override
        public float function(float amplitude, float frequency, float phase, int n) {
            double cycle = n / AudioFormatConstants.SAMPLING_RATE;
            return (float) (2 * amplitude / PI * atan(tan((PI * frequency * cycle) + phase)));
        }

        @Override
        public float freqModulateFunction(float m, float f) {
            return (float) (atan(tan(m * f)));
        }
    },
    NOISE {
        @Override
        public float function(float amplitude, float frequency, float phase, int n) {
            return (float) (amplitude * (random() * 2 - 1));
        }

        @Override
        public float freqModulateFunction(float m, float f) {
            return 0;
        }
    };

    public abstract float function(float amplitude, float frequency, float phase, int n);
    public abstract float freqModulateFunction(float m, float f);
}
