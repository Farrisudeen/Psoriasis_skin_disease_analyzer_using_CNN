package com.example.skindetect.dto;

import java.util.List;

public class PredictionResult {
    private final String label;
    private final List<String> dos;
    private final List<String> donts;
    private final boolean normal;

    public PredictionResult(String label, List<String> dos, List<String> donts, boolean normal) {
        this.label = label;
        this.dos = dos;
        this.donts = donts;
        this.normal = normal;
    }

    public String getLabel() { return label; }
    public List<String> getDos() { return dos; }
    public List<String> getDonts() { return donts; }
    public boolean isNormal() { return normal; }
}
