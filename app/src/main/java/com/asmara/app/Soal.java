package com.asmara.app;

public class Soal {
    private String pertanyaan;
    private String[] opsi;
    private int jawabanBenar; // Index (0, 1, 2)
    private String feedback;
    private String imagePath; // Optional: jika null berarti tidak ada gambar

    public Soal(String pertanyaan, String[] opsi, int jawabanBenar, String feedback, String imagePath) {
        this.pertanyaan = pertanyaan;
        this.opsi = opsi;
        this.jawabanBenar = jawabanBenar;
        this.feedback = feedback;
        this.imagePath = imagePath;
    }

    public String getPertanyaan() { return pertanyaan; }
    public String[] getOpsi() { return opsi; }
    public int getJawabanBenar() { return jawabanBenar; }
    public String getFeedback() { return feedback; }
    public String getImagePath() { return imagePath; }
}
