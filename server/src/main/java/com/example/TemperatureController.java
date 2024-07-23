package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/temperature")
public class TemperatureController {
    private final Regulator regulator = VirtualRegulator.getInstance();

    @PostMapping("/set")
    public String setTemperature(@RequestParam float temperature) {
        log.info("Set {}", temperature);
        List<Float> outData = new ArrayList<>();
        int result = regulator.adjustTemp((byte) 0b01000000, temperature, outData, 0);
        log.info("result: {}", result);
        return result == 0 ? "Temperature set successfully" : "Error setting temperature";
    }

    @GetMapping("/current")
    public List<Float> getCurrentTemperature() {
        log.info("getCurrentTemperature ");
        List<Float> outData = new ArrayList<>();
        regulator.adjustTemp((byte) 0b00100000, 0, outData, 0);
        log.info("getCurrentTemperature {}", outData);
        return outData;
    }

    @GetMapping("/recent")
    public List<Float> getRecentTemperatures(@RequestParam int offset, @RequestParam int count) {
        log.info("getRecentTemperatures {} {}", offset, count);
        List<Float> outData = new ArrayList<>();
        regulator.adjustTemp((byte) (0b00100000 | count), 0, outData, offset);
        log.info("getRecentTemperatures {}", outData);
        return outData;
    }

    @PostMapping("/clear")
    public String clearTemperatures() {
        regulator.adjustTemp((byte) 0b10000000, 0, new ArrayList<>(), 0);
        return "Temperatures cleared";
    }
}