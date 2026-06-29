// ===== NAVIGATION =====
let currentScreen = "splash";
let screenHistory = [];

function gotoScreen(id) {
  const cur = document.getElementById(currentScreen);
  const nxt = document.getElementById(id);
  cur.classList.remove("active");
  nxt.classList.add("active", "slide-in");
  setTimeout(() => nxt.classList.remove("slide-in"), 400);
  screenHistory.push(currentScreen);
  currentScreen = id;
}
function goBack() {
  if (!screenHistory.length) return;
  stopAudio();
  const prev = screenHistory.pop();
  document.getElementById(currentScreen).classList.remove("active");
  document.getElementById(prev).classList.add("active");
  currentScreen = prev;
}

setTimeout(() => {
  gotoScreen("home");
  screenHistory = [];
}, 3000);

// ===== AUDIO =====
let audioPlaying = false;
let myAudio = new Audio("suara_narasi.mp3");

myAudio.onended = () => {
  audioPlaying = false;
  const btn = document.getElementById("audioBtn");
  if (btn) {
    btn.innerHTML = '<i class="ph-bold ph-speaker-high" style="font-size:18px;"></i>';
    btn.classList.remove("playing");
  }
};

function toggleAudio() {
  const btn = document.getElementById("audioBtn");
  if (!audioPlaying) {
    myAudio
      .play()
      .then(() => {
        audioPlaying = true;
        btn.innerHTML = '<i class="ph-bold ph-stop" style="font-size:18px;"></i>';
        btn.classList.add("playing");
        showToast('<i class="ph-bold ph-speaker-high" style="font-size:14px;vertical-align:middle;"></i> Memainkan narasi audio...');
      })
      .catch((err) => {
        showToast('<i class="ph-bold ph-warning" style="font-size:14px;vertical-align:middle;color:#f4a822;"></i> Belum ada file suara_narasi.mp3!');
      });
  } else {
    stopAudio();
    showToast('<i class="ph-bold ph-stop" style="font-size:14px;vertical-align:middle;"></i> Audio dihentikan');
  }
}

function stopAudio() {
  if (audioPlaying) {
    myAudio.pause();
    myAudio.currentTime = 0; // Reset ke awal
    audioPlaying = false;
  }
  const btn = document.getElementById("audioBtn");
  if (btn) {
    btn.innerHTML = '<i class="ph-bold ph-speaker-high" style="font-size:18px;"></i>';
    btn.classList.remove("playing");
  }
}

// ===== CAMERA =====
function simulateDetect() {
  document.getElementById("scanText").textContent =
    "Objek ditemukan! Memuat AR...";
  document.getElementById("scanBar").style.background = "#2ECC71";
  showToast('<i class="ph-bold ph-crosshair" style="font-size:14px;vertical-align:middle;"></i> Kartu terdeteksi!');
  setTimeout(() => gotoScreen("edukasi"), 1200);
}

// ===== TOAST =====
function showToast(msg) {
  const t = document.getElementById("toast");
  t.innerHTML = msg;
  t.classList.add("show");
  setTimeout(() => t.classList.remove("show"), 2500);
}

// ===== KUIS ENGINE =====
const soalList = [
  {
    q: "Saat berkunjung ke Gedung Papak, Dika melihat bagian bawah tiang bendera yang berbentuk ...." /* [cite: 2] */,
    img: "soalno1.png", // Ganti dengan nama file gambarmu
    opts: [
      "Kubus" /* [cite: 3] */,
      "Balok" /* [cite: 4] */,
      "Limas" /* [cite: 5] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Bagian bawah tiang tersebut memiliki panjang, lebar, dan tinggi yang berbeda sehingga membentuk balok.",
  },
  {
    q: "Bagian atap Gedung Papak berbentuk bangun ruang apa?" /* [cite: 6] */,
    img: "soalno2.png", // Ganti dengan nama file gambarmu
    opts: [
      "Balok" /* [cite: 7] */,
      "Kubus" /* [cite: 8] */,
      "Limas" /* [cite: 9] */,
    ],
    ans: 2,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Atap bangunannya mengerucut ke atas sehingga membentuk bangun ruang limas.",
  },
  {
    q: "Gambar dadu di bawah ini, berbentuk bangun ruang apa?" /* [cite: 12] */,
    img: "soalno3.png", // Ganti dengan nama file gambarmu
    opts: [
      "Limas" /* [cite: 13] */,
      "Kubus" /* [cite: 14] */,
      "Balok" /* [cite: 15] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Dadu memiliki panjang sisi yang sama di setiap permukaannya, yang merupakan ciri utama kubus.",
  },
  {
    q: "Balok mempunyai bentuk yang ...." /* [cite: 16] */,
    opts: [
      "Panjang" /* [cite: 17] */,
      "Bulat" /* [cite: 18] */,
      "Runcing" /* [cite: 19] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Betul! Secara umum, balok memiliki bentuk yang memanjang karena perbedaaan ukuran rusuknya.",
  },
  {
    q: "Tiang Gedung Papak pada gambar diatas berbentuk…" /* [cite: 20] */,
    img: "soalno4.png", // Ganti dengan nama file gambarmu
    opts: [
      "Kubus" /* [cite: 21] */,
      "Limas" /* [cite: 22] */,
      "Balok" /* [cite: 23] */,
    ],
    ans: 2,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Hebat! Tiang penyangga tersebut berbentuk balok memanjang yang kokoh.",
  },
  {
    q: "Benda di bawah ini yang berbentuk limas adalah…" /* [cite: 24] */,
    opts: ["Tempat Pensil", "Piramida" /* [cite: 25] */, "Kotak Kado"],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Piramida adalah contoh paling jelas dari bangun ruang limas.",
  },
  {
    q: "Bangun ruang yang memiliki puncak adalah ...." /* [cite: 26] */,
    opts: [
      "Limas" /* [cite: 27] */,
      "Kubus" /* [cite: 28] */,
      "Balok" /* [cite: 29] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Sisi-sisi tegak pada limas bertemu pada satu titik yang disebut puncak.",
  },
  {
    q: "Bentuk limas pada Gedung Papak sering digunakan untuk ...." /* [cite: 30] */,
    opts: [
      "Atap" /* [cite: 31] */,
      "Roda" /* [cite: 32] */,
      "Jendela" /* [cite: 33] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Betul! Bentuk limas sangat umum digunakan sebagai struktur atap bangunan.",
  },
  {
    q: "Limas mempunyai bagian atas yang ...." /* [cite: 34] */,
    opts: [
      "Runcing" /* [cite: 35] */,
      "Bulat" /* [cite: 36] */,
      "Datar" /* [cite: 37] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Titik puncak pada limas membuatnya terlihat runcing di bagian atas.",
  },
  {
    q: "Batu bata yang digunakan membangun Gedung Papak berbentuk ...." /* [cite: 38] */,
    img: "soalno9.png", // Ganti dengan nama file gambarmu
    opts: [
      "Balok" /* [cite: 39] */,
      "Kubus" /* [cite: 40] */,
      "Limas" /* [cite: 41] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Batu bata memiliki struktur bangun ruang balok.",
  },
  {
    q: "Balok membantu membuat miniatur Gedung Papak pada bagian ...." /* [cite: 42] */,
    opts: [
      "Dinding" /* [cite: 43] */,
      "Awan" /* [cite: 44] */,
      "Pohon" /* [cite: 45] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Betul! Bangun ruang balok sangat cocok digunakan untuk merepresentasikan dinding yang lurus dan tegak.",
  },
  {
    q: "Kotak tisu seperti pada gambar diatas berbentuk…" /* [cite: 46] */,
    img: "soalno11.png", // Ganti dengan nama file gambarmu
    opts: [
      "Kubus" /* [cite: 47] */,
      "Balok" /* [cite: 48] */,
      "Prisma" /* [cite: 49] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Kotak tisu tersebut berbentuk balok.",
  },
  {
    q: "Bentuk bangunan pada Gedung Papak diatas adalah.." /* [cite: 50] */,
    img: "soalno12.png", // Ganti dengan nama file gambarmu
    opts: [
      "Kubus" /* [cite: 51] */,
      "Balok" /* [cite: 52] */,
      "Limas" /* [cite: 53] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Struktur utama bangunan tersebut memanjang sehingga menyerupai balok.",
  },
  {
    q: "Benda berbentuk kubus yang ada di sekitar kita adalah ...." /* [cite: 54] */,
    opts: ["Dadu" /* [cite: 55] */, "Penggaris", "Buku Tulis"],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Dadu adalah salah satu contoh benda sehari-hari yang berbentuk kubus sempurna.",
  },
  {
    q: "Ketika kamu melihat atap pada bangunan Gedung Papak, berbentuk apakah atapnya?" /* [cite: 56] */,
    opts: [
      "Limas" /* [cite: 57] */,
      "Balok" /* [cite: 58] */,
      "Kubus" /* [cite: 59] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Betul! Sekali lagi, atap bangunan ini mengerucut menyerupai limas.",
  },
  {
    q: "Berbentuk apakah kotak susu pada gambar diatas?" /* [cite: 60] */,
    img: "soalno15.png", // Ganti dengan nama file gambarmu
    opts: [
      "Balok" /* [cite: 61] */,
      "Kubus" /* [cite: 62] */,
      "Limas" /* [cite: 63] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Kemasan kotak susu memanjang vertikal sehingga berbentuk balok.",
  },
  {
    q: "Gedung Papak berada di Kota?" /* [cite: 64] */,
    opts: [
      "Semarang" /* [cite: 65] */,
      "Bandung" /* [cite: 66] */,
      "Salatiga" /* [cite: 67] */,
    ],
    ans: 2,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Hebat! Gedung Papak adalah warisan arsitektur yang ikonik di Kota Salatiga.",
  },
  {
    q: "Gedung Papak Sekarang menjadi?" /* [cite: 68] */,
    opts: [
      "Kantor Wali Kota" /* [cite: 69] */,
      "Rumah Sakit" /* [cite: 70] */,
      "Kantor Pos" /* [cite: 71] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Bangunan bersejarah ini dialihfungsikan sebagai pusat pemerintahan kota.",
  },
  {
    q: "Gedung Papak memiliki ciri khas bangunan?" /* [cite: 72] */,
    opts: [
      "Modern" /* [cite: 73] */,
      "Kolonial" /* [cite: 74] */,
      "Tradisional" /* [cite: 75] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Arsitekturnya kental dengan gaya kolonial Belanda era Hindia Belanda.",
  },
  {
    q: "Kubus, Balok dan Limas termasuk bangun?" /* [cite: 76] */,
    opts: [
      "Datar" /* [cite: 77] */,
      "Ruang" /* [cite: 78] */,
      "Lengkung" /* [cite: 79] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Ketiganya memiliki volume dan dimensi tiga, sehingga disebut bangun ruang.",
  },
  {
    q: "Gedung Papak dominan berwarna?" /* [cite: 80] */,
    opts: [
      "Putih" /* [cite: 81] */,
      "Merah" /* [cite: 82] */,
      "Hitam" /* [cite: 83] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Betul! Warna putih sangat dominan pada arsitektur gaya kolonial untuk memantulkan panas.",
  },
  {
    q: "Saat mengamati Gedung Papak, Ali melihat bangunan utama yang bentuknya lebih panjang daripada kubus. Bangunan tersebut menyerupai ...." /* [cite: 84] */,
    opts: [
      "Limas" /* [cite: 85] */,
      "Balok" /* [cite: 86] */,
      "Bola" /* [cite: 87] */,
    ],
    ans: 1,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Balok memiliki dimensi yang lebih memanjang dibandingkan kubus.",
  },
  {
    q: "Indah dan teman-temannya membuat miniatur Gedung Papak. Untuk membuat bagian atap yang runcing, mereka menggunakan bangun ruang ...." /* [cite: 88] */,
    opts: [
      "Kubus" /* [cite: 89] */,
      "Balok" /* [cite: 90] */,
      "Limas" /* [cite: 91] */,
    ],
    ans: 2,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Benar! Bangun limas sangat pas merepresentasikan atap.",
  },
  {
    q: "Saat membuat atap miniatur Gedung Papak, kelompok Nurul menggunakan limas. Mereka melihat bagian samping limas berbentuk segitiga. Limas memiliki sisi samping berbentuk ...." /* [cite: 92, 93] */,
    opts: [
      "Lingkaran" /* [cite: 94] */,
      "Persegi" /* [cite: 95] */,
      "Segitiga" /* [cite: 96] */,
    ],
    ans: 2,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Tepat! Semua sisi tegak pada sebuah limas berbentuk segitiga.",
  },
  {
    q: "Saat membuat miniatur Gedung Papak, Bagas memegang sebuah kubus. Ia memperhatikan bahwa setiap sisi kubus berbentuk ..." /* [cite: 97] */,
    opts: [
      "Persegi" /* [cite: 98] */,
      "Segitiga" /* [cite: 99] */,
      "Lingkaran" /* [cite: 100] */,
    ],
    ans: 0,
    fb: "<i class='ph-bold ph-check-circle' style='color:#e85d04;'></i> Sempurna! Sebuah kubus dibentuk oleh enam buah sisi yang semuanya berbentuk persegi.",
  },
];

// ===== QUIZ ENGINE — NAVIGASI BEBAS =====
let qState = []; // { status, chosen, timeUsed } per soal
let curQ = 0;
let timerInterval = null;
let timeLeft = 40;

function initState() {
  qState = soalList.map(() => ({
    status: "unanswered",
    chosen: null,
    timeUsed: 0,
  }));
}

// ── Mulai Kuis ──────────────────────────────
function startKuis() {
  initState();
  curQ = 0;
  document.getElementById("kuis-intro").style.display = "none";
  document.getElementById("kuis-result").classList.remove("visible");
  document.getElementById("kuis-play").classList.add("visible");
  buildDots();
  renderQuestion();
}

// ── Dot navigasi (angka 1-5 yang bisa diklik) ──
function buildDots() {
  const wrap = document.getElementById("kuisDots");
  if (!wrap) return;
  wrap.innerHTML = soalList
    .map(
      (_, i) =>
        `<button class="kuis-dot" id="dot${i}" onclick="jumpToQuestion(${i})">${i + 1}</button>`,
    )
    .join("");
}

function updateDots() {
  soalList.forEach((_, i) => {
    const d = document.getElementById("dot" + i);
    if (!d) return;
    d.className = "kuis-dot";
    if (i === curQ) d.classList.add("active");
    else if (qState[i].status === "correct")
      d.classList.add("answered-correct");
    else if (qState[i].status === "wrong" || qState[i].status === "timeout")
      d.classList.add("answered-wrong");
    else if (qState[i].status === "skipped") d.classList.add("skipped");
  });
}

function jumpToQuestion(idx) {
  clearInterval(timerInterval);
  if (qState[curQ].status === "unanswered")
    qState[curQ].timeUsed += 40 - timeLeft;
  curQ = idx;
  renderQuestion();
}

// ── Render soal ──────────────────────────────
function renderQuestion() {
  const soal = soalList[curQ];
  const state = qState[curQ];
  const isDone = state.status !== "unanswered";
  const L = ["A", "B", "C", "D"];

  document.getElementById("qNum").textContent =
    `Soal ${curQ + 1} dari ${soalList.length}`;
  updateDots();
  updateNavButtons();

  // Pilihan jawaban
  const optHtml = soal.opts
    .map((o, i) => {
      let cls = "kuis-option";
      if (isDone) {
        if (i === soal.ans && i === state.chosen) cls = "kuis-option correct";
        else if (i === soal.ans) cls += " reveal-correct";
        else if (i === state.chosen) cls += " wrong";
      }
      const locked = isDone ? 'style="pointer-events:none"' : "";
      return `<button class="${cls}" id="opt${i}" onclick="pilihJawaban(${i})" ${locked}>
              <div class="option-letter">${L[i]}</div>
              <div class="option-text">${o}</div>
            </button>`;
    })
    .join("");

  // Feedback setelah menjawab
  const fbMap = {
    correct: ["correct-fb", '<i class="ph-bold ph-star" style="color:#f4a822;"></i>', `<i class="ph-bold ph-check-circle" style="color:#e85d04;"></i> Benar! ${soal.fb}`],
    wrong: ["wrong-fb", '<i class="ph-bold ph-lightbulb" style="color:#f4a822;"></i>', `<i class="ph-bold ph-x-circle" style="color:#e74c3c;"></i> Kurang tepat. ${soal.fb}`],
    timeout: ["wrong-fb", '<i class="ph-bold ph-alarm" style="color:#e74c3c;"></i>', `Waktu habis! ${soal.fb}`],
    skipped: ["skip-fb", '<i class="ph-bold ph-skip-forward" style="color:#f4a822;"></i>', "Soal dilewati — kamu bisa kembali kapan saja!"],
  };
  let fbHtml = "";
  if (isDone && fbMap[state.status]) {
    const [cls, icon, txt] = fbMap[state.status];
    fbHtml = `<div class="kuis-feedback show ${cls}">
              <span class="kuis-feedback-icon">${icon}</span><span>${txt}</span></div>`;
  }

  const imgHtml = soal.img
    ? `<img src="${soal.img}" style="width:100%; max-height:250px; object-fit:contain; border-radius:12px; margin-top:12px; border: 1px solid rgba(255,255,255,0.1);" alt="Ilustrasi Soal">`
    : "";

  // Ubah template innerHTML-nya menjadi:
  document.getElementById("kuisQArea").innerHTML = `
            <div class="kuis-q-card">
            <div class="kuis-q-badge"><i class="ph-bold ph-note-pencil" style="font-size:12px;"></i> Pertanyaan ${curQ + 1}</div>
            <div class="kuis-q-text">${soal.q}</div>
            ${imgHtml} </div>
            <div class="kuis-options">${optHtml}</div>
            ${fbHtml}`;

  // Timer — hanya berjalan untuk soal yang belum dijawab
  clearInterval(timerInterval);
  if (!isDone) {
    timeLeft = Math.max(1, 40 - state.timeUsed);
    updateTimerDisplay();
    timerInterval = setInterval(() => {
      timeLeft--;
      qState[curQ].timeUsed++;
      updateTimerDisplay();
      if (timeLeft <= 0) {
        clearInterval(timerInterval);
        handleTimeout();
      }
    }, 1000);
  } else {
    const el = document.getElementById("timerDisplay");
    el.textContent = "—";
    el.style.color = "#777";
  }
}

function updateTimerDisplay() {
  const el = document.getElementById("timerDisplay");
  el.textContent = timeLeft;
  el.style.color = timeLeft <= 5 ? "#FF6B6B" : "var(--orange)";
}

// ── Tombol navigasi ──────────────────────────
function updateNavButtons() {
  const pv = document.getElementById("btnPrev");
  const sk = document.getElementById("btnSkip");
  const nx = document.getElementById("btnNext");
  if (!pv || !sk || !nx) return;

  const isDone = qState[curQ].status !== "unanswered";
  const isLast = curQ === soalList.length - 1;

  pv.disabled = curQ === 0;
  sk.style.display = isDone ? "none" : "inline-block";

  if (isLast) {
    nx.innerHTML = '<i class="ph-bold ph-flag-checkered" style="font-size:14px;"></i> Selesai';
    nx.classList.add("finish");
  } else {
    nx.textContent = "Lanjut →";
    nx.classList.remove("finish");
  }
}

// ── Aksi pengguna ────────────────────────────
function pilihJawaban(idx) {
  if (qState[curQ].status !== "unanswered") return;
  clearInterval(timerInterval);
  qState[curQ].chosen = idx;
  qState[curQ].status = idx === soalList[curQ].ans ? "correct" : "wrong";
  renderQuestion();
}

function skipQuestion() {
  clearInterval(timerInterval);
  if (qState[curQ].status === "unanswered") {
    qState[curQ].status = "skipped";
    qState[curQ].timeUsed += 40 - timeLeft;
  }
  let next = -1;
  for (let i = 1; i <= soalList.length; i++) {
    const idx = (curQ + i) % soalList.length;
    if (qState[idx].status === "unanswered") {
      next = idx;
      break;
    }
  }
  curQ = next !== -1 ? next : curQ;
  renderQuestion();
  if (next === -1) showToast('<i class="ph-bold ph-note-pencil" style="font-size:14px;vertical-align:middle;"></i> Semua soal sudah dikerjakan! Tekan Selesai.');
}

function handleTimeout() {
  qState[curQ].status = "timeout";
  qState[curQ].chosen = null;
  qState[curQ].timeUsed = 30;
  renderQuestion();
  showToast('<i class="ph-bold ph-alarm" style="font-size:14px;vertical-align:middle;color:#e74c3c;"></i> Waktu habis untuk soal ini!');
}

function prevQuestion() {
  if (curQ === 0) return;
  clearInterval(timerInterval);
  if (qState[curQ].status === "unanswered")
    qState[curQ].timeUsed += 40 - timeLeft;
  curQ--;
  renderQuestion();
}

function nextQuestion() {
  clearInterval(timerInterval);
  if (qState[curQ].status === "unanswered")
    qState[curQ].timeUsed += 40 - timeLeft;
  if (curQ === soalList.length - 1) {
    qState.forEach((s) => {
      if (s.status === "unanswered") s.status = "skipped";
    });
    showResult();
  } else {
    curQ++;
    renderQuestion();
  }
}

// ── Hasil ────────────────────────────────────
function showResult() {
  clearInterval(timerInterval);
  document.getElementById("kuis-play").classList.remove("visible");
  document.getElementById("kuis-result").classList.add("visible");

  const correct = qState.filter((s) => s.status === "correct").length;
  const wrong = qState.filter(
    (s) => s.status === "wrong" || s.status === "timeout",
  ).length;
  const skipped = qState.filter((s) => s.status === "skipped").length;
  const totalSec = qState.reduce((a, s) => a + s.timeUsed, 0);
  const pct = Math.round((correct / soalList.length) * 100);

  document.getElementById("resultScore").textContent = pct;
  document.getElementById("statCorrect").textContent = correct;
  document.getElementById("statWrong").innerHTML =
    wrong + (skipped ? ` (+${skipped}<i class='ph-bold ph-skip-forward' style='font-size:12px;'></i>)` : "");
  document.getElementById("statTime").textContent = totalSec + "s";
  document
    .getElementById("resultCircle")
    .style.setProperty("--pct", Math.round(pct * 3.6) + "deg");

  let grade,
    msg,
    badges = [];
  if (pct === 100) {
    grade = '<i class="ph-bold ph-trophy" style="color:#f4a822;"></i> Sempurna!';
    msg = "Luar biasa! Kamu kuasai semua materi!";
    badges = ['<i class="ph-bold ph-star" style="color:#f4a822;"></i> Bintang 5', '<i class="ph-bold ph-medal" style="color:#e85d04;"></i> Juara Kuis', '<i class="ph-bold ph-bird" style="color:#89CFF0;"></i> Sahabat ASMARA'];
  } else if (pct >= 80) {
    grade = '<i class="ph-bold ph-medal" style="color:#f4a822;"></i> Sangat Baik!';
    msg = "Hebat! Pemahamanmu sangat bagus!";
    badges = ['<i class="ph-bold ph-star" style="color:#f4a822;"></i> Bintang 4', '<i class="ph-bold ph-books" style="color:#e85d04;"></i> Pecinta Sejarah'];
  } else if (pct >= 60) {
    grade = '<i class="ph-bold ph-medal" style="color:#b0b0b0;"></i> Cukup Baik';
    msg = "Bagus! Terus semangat belajarnya!";
    badges = ['<i class="ph-bold ph-star" style="color:#f4a822;"></i> Bintang 3', '<i class="ph-bold ph-book-open" style="color:#89CFF0;"></i> Pelajar Semangat'];
  } else if (pct >= 40) {
    grade = '<i class="ph-bold ph-medal" style="color:#cd7f32;"></i> Perlu Latihan';
    msg = "Jangan menyerah! Tonton AR lalu coba lagi!";
    badges = ['<i class="ph-bold ph-hand-fist" style="color:#e85d04;"></i> Tetap Semangat'];
  } else {
    grade = '<i class="ph-bold ph-book-open" style="color:#89CFF0;"></i> Ayo Belajar!';
    msg = "Yuk pelajari lagi lewat mode AR!";
    badges = ['<i class="ph-bold ph-arrow-counter-clockwise" style="color:#e85d04;"></i> Coba Lagi!'];
  }

  document.getElementById("resultGrade").innerHTML = grade;
  document.getElementById("resultMsg").textContent = msg;
  document.getElementById("resultBadges").innerHTML = badges
    .map((b) => `<span class="result-badge">${b}</span>`)
    .join("");
}

// ── Reset ─────────────────────────────────────
function resetKuis() {
  clearInterval(timerInterval);
  initState();
  curQ = 0;
  document.getElementById("kuis-intro").style.display = "flex";
  document.getElementById("kuis-play").classList.remove("visible");
  document.getElementById("kuis-result").classList.remove("visible");
  document.getElementById("kuisQArea").innerHTML = "";
  const dots = document.getElementById("kuisDots");
  if (dots) dots.innerHTML = "";
}

