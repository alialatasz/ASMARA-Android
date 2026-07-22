<div align="center">
# 🦉 ASMARA 🦉
**Aplikasi Semarak Matematika dan Sejarah Nusantara**
[![Android Version](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![UI/UX](https://img.shields.io/badge/UI-Claymorphism-FF69B4?style=for-the-badge)](https://dribbble.com/)
_Membawa pesona Sejarah Lokal Salatiga ke dalam asyiknya belajar Geometri Ruang!_ 🚀
---
</div>
## Tentang ASMARA
**ASMARA** diciptakan untuk menjawab tantangan dunia pendidikan dasar: _Bagaimana membuat anak kelas 5 SD jatuh cinta pada Matematika dan Sejarah?_
Alih-alih diberi teks membosankan, ASMARA mengajak siswa dalam sebuah "Game Petualangan". Aplikasi ini menggabungkan materi Bangun Ruang (Kubus, Balok, Limas) dengan representasi nyata dari bangunan bersejarah peninggalan kolonial di Kota Salatiga, seperti **Gedung Papak**, **Gedung Pakuwon**, dan **Rumah Dinas Wali Kota**.
<br/>
## Fitur Unggulan
### 1. Desain _Claymorphism_ yang Ramah Anak 🧊
Selamat tinggal desain kotak yang kaku! Antarmuka ASMARA dibangun 100% menggunakan tren desain modern **Claymorphism**. Kombinasi _drop shadow_ 3D, _inner glow_, dan animasi _bouncy overshoot_ membuat setiap tombol di aplikasi ini terasa seperti "malam plastisin" yang empuk dan interaktif.
### 2. Penampil 3D Interaktif (Universal) 📐
Siswa dapat memutar, memperbesar, dan melihat jaring-jaring bangun ruang secara 360 derajat. Mesin 3D kami dirancang untuk berjalan super mulus di semua jenis _smartphone_ (bahkan HP _entry-level_ sekalipun) tanpa mewajibkan sensor ARCore!
### 3. Kuis Interaktif & Drag-and-Drop 🎮
Ujian tidak lagi menakutkan! Kuis ASMARA dilengkapi timer berdetak, penanda warna, fitur ragu-ragu, dan **soal praktik spesial (Susun Balok Drag-and-Drop)** yang melatih motorik anak secara langsung.
### 4. Sistem Gamifikasi (Leaderboard & XP) 🏆
- Siapa yang paling pintar di kelas? Fitur **Papan Peringkat (Leaderboard)** _real-time_ yang ditenagai oleh Firebase siap menumbuhkan semangat kompetitif!
- Kumpulkan poin (_XP_), dapatkan bintang, dan koleksi **Lencana Kehormatan** (_Badges_) rahasia di halaman Profil Penjelajah Anda!
### 5. Maskot & Panduan Audio 🦉
Ditemani oleh Maskot Rubah pintar yang akan menyoraki siswa saat menjawab benar (lengkap dengan hujan partikel Konfeti 🎉) dan memberi penjelasan ramah saat jawaban salah. Didukung pula narasi _audio voice-over_ untuk setiap materi sejarah!
<br/>
## Teknologi yang Digunakan 🛠️
- **Bahasa Pemrograman:** Native Java
- **Database & Backend:** Firebase Realtime Database
- **UI Komponen:** Kustom `BlurGlassView` via Android Canvas API
- **Animasi:** `ObjectAnimator` dengan `OvershootInterpolator`
- **3D Rendering:** Integrasi WebView dengan Google `model-viewer` & GLB Assets
<br/>
## Cara Menjalankan Project 🚀
Bagi Anda yang ingin mengembangkan atau melihat langsung _source code_ ASMARA, ikuti langkah berikut:
1. **Clone repositori ini**
   ```bash
   git clone https://github.com/username/ASMARA.git
   ```
2. **Buka di Android Studio**
   Buka aplikasi Android Studio, pilih `File > Open`, lalu arahkan ke folder `ASMARA` yang baru saja diunduh.
3. **Konfigurasi Firebase** (Opsional jika ingin menggunakan database sendiri)
   Ganti file `google-services.json` di dalam folder `app/` dengan milik Anda dari [Firebase Console](https://console.firebase.google.com/).
4. **Build & Run**
   Klik logo ▶️ **Run 'app'** di Android Studio atau tekan `Shift + F10`.
<br/>
## Galeri Petualangan (Screenshots) 📷
_Intip keseruan belajar bersama ASMARA melalui cuplikan layar di bawah ini:_
<table align="center" style="border:none;">
  <tr>
    <td align="center" valign="top" width="33%">
      <img src="1.jpg" width="200" alt="Splash Screen" style="border-radius: 12px;"/><br/><br/>
      <b>✨ Pintu Masuk ASMARA</b><br/>
      <i>Splash screen ceria yang siap menyambut siswa dengan energi dan semangat belajar tinggi!</i>
    </td>
    <td align="center" valign="top" width="33%">
      <img src="2.jpg" width="200" alt="Onboarding" style="border-radius: 12px;"/><br/><br/>
      <b>🗺️ Petunjuk Petualangan</b><br/>
      <i>Slide sambutan interaktif yang memandu siswa untuk mengenal berbagai fitur ajaib di dalam ASMARA.</i>
    </td>
    <td align="center" valign="top" width="33%">
      <img src="3.jpg" width="200" alt="Home" style="border-radius: 12px;"/><br/><br/>
      <b>🏠 Markas Utama</b><br/>
      <i>Halaman beranda bergaya Claymorphism! Menu-menunya empuk, lucu, dan sangat menggugah rasa penasaran.</i>
    </td>
  </tr>
  <tr>
    <td align="center" valign="top">
      <img src="4.jpg" width="200" alt="Profil" style="border-radius: 12px;"/><br/><br/>
      <b>👤 Profil & Gamifikasi</b><br/>
      <i>Tingkatkan levelmu! Dapatkan XP dan koleksi lencana rahasia yang tersinkronisasi aman di cloud Firebase.</i>
    </td>
    <td align="center" valign="top">
      <img src="5.jpg" width="200" alt="Sejarah" style="border-radius: 12px;"/><br/><br/>
      <b>🏛️ Mesin Waktu Sejarah</b><br/>
      <i>Menjelajahi histori Gedung Papak dan peninggalan Salatiga untuk merawat kecintaan pada budaya lokal.</i>
    </td>
    <td align="center" valign="top">
      <img src="6.jpg" width="200" alt="Materi" style="border-radius: 12px;"/><br/><br/>
      <b>📐 Laboratorium Geometri</b><br/>
      <i>Belajar matematika tak lagi kaku. Dilengkapi narasi suara pendamping dan visualisasi 3D bangun ruang.</i>
    </td>
  </tr>
  <tr>
    <td align="center" valign="top">
      <img src="7.jpg" width="200" alt="Kuis" style="border-radius: 12px;"/><br/><br/>
      <b>🎮 Arena Kuis Pintar</b><br/>
      <i>Evaluasi interaktif anti-bosan! Jawaban siswa akan direkap otomatis ke database agar mudah dipantau oleh Guru.</i>
    </td>
    <td align="center" valign="top">
      <img src="8.jpg" width="200" alt="Leaderboard" style="border-radius: 12px;"/><br/><br/>
      <b>🏆 Papan Peringkat</b><br/>
      <i>Persaingan sehat pembakar semangat! Tersambung secara real-time ke Firebase untuk memperebutkan takhta juara kelas.</i>
    </td>
    <td align="center" valign="top">
      <img src="9.jpg" width="200" alt="3D AR" style="border-radius: 12px;"/><br/><br/>
      <b>🌟 Keajaiban Scanner 3D</b><br/>
      <i>Fitur pamungkas! Hadirkan kemegahan Gedung Papak dan Balok dalam bentuk 3D tepat di layar genggamanmu melalui pemindai QR cerdas.</i>
    </td>
  </tr>
</table>
<br/>
## Lisensi 📜
Aplikasi ini dikembangkan untuk tujuan edukasi dan peningkatan mutu pendidikan dasar di Indonesia.
Dibuat dengan ❤️ untuk anak-anak Nusantara.
Tim ASMARA. PKM PIMNAS 2026. Universitas Negeri Semarang.
---
<div align="center">
  <i>"Belajar Matematika itu Menyenangkan, Mengenal Sejarah itu Membanggakan!"</i>
</div>
