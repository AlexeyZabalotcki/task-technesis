package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VirtualRegulator implements Regulator {
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;
    private static final float MIN_TEMP = -200.0f;
    private static final float MAX_TEMP = 1000.0f;
    private static VirtualRegulator instance;
    private List<Float> temperatures;
    private Random random;

    private VirtualRegulator() {
        temperatures = new ArrayList<>();
        random = new Random();
    }

    public static synchronized VirtualRegulator getInstance() {
        if (instance == null) {
            instance = new VirtualRegulator();
        }
        return instance;
    }

    public static synchronized void shutdown() {
        if (instance != null) {
            instance.temperatures.clear();
            instance = null;
        }
    }

    @Override
    public int adjustTemp(byte operation, float inData, List<Float> outData, int offsetOut) {
        boolean clear = (operation & 0b10000000) != 0;
        boolean set = (operation & 0b01000000) != 0;
        boolean get = (operation & 0b00100000) != 0;
        int readCount = (operation & 0b00011111);

        if (clear) {
            temperatures.clear();
        }

        if (set) {
            float lastTemp = temperatures.isEmpty() ? 0.0f : temperatures.get(temperatures.size() - 1);
            int steps = 3 + random.nextInt(6);
            for (int i = 1; i <= steps; i++) {
                float newTemp = lastTemp + (inData - lastTemp) * (i / (float) steps);
                temperatures.add(newTemp);
            }
        }

        if (get) {
            for (int i = offsetOut; i < offsetOut + readCount && i < temperatures.size(); i++) {
                outData.add(temperatures.get(i));
            }
        }

        if (inData < MIN_TEMP || inData > MAX_TEMP) {
            return ERROR;
        }

        return SUCCESS;
    }

    public List<Float> getTemperatures() {
        return new ArrayList<>(temperatures);
    }
}

