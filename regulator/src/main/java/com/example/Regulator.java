package com.example;

import java.util.List;

public interface Regulator {
    int adjustTemp(byte operation, float inData, List<Float> outData, int offsetOut);
}
