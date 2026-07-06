package com.asmara.app;

public class Soal {
    private String pertanyaan;
    private String[] opsi;
    private int jawabanBenar; // Index (0, 1, 2)
    private String feedback;
    private String imageName; // Optional: nama file di res/drawable tanpa ekstensi, contoh "soal_1". Null jika tidak ada.

    public Soal(String pertanyaan, String[] opsi, int jawabanBenar, String feedback, String imageName) {
        this.pertanyaan = pertanyaan;
        this.opsi = opsi;
        this.jawabanBenar = jawabanBenar;
        this.feedback = feedback;
        this.imageName = imageName;
    }

    public String getPertanyaan() { return pertanyaan; }
    public String[] getOpsi() { return opsi; }
    public int getJawabanBenar() { return jawabanBenar; }
    public String getFeedback() { return feedback; }
    public String getImageName() { return imageName; }
}
