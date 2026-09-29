# Panduan Kompilasi & Publikasi: Kalkulator Zakat & Catatan Keuangan

Aplikasi ini sudah siap 100% dengan Google AdMob Banner:
- **Application ID**: `ca-app-pub-2864823906339269~4890382445`
- **Ad Unit ID**: `ca-app-pub-2864823906339269/2000495225`
- **Target SDK**: 33 | **Min SDK**: 21 (Mendukung Android 5.0 Lollipop hingga Android 13+)

---

## 1. Menjalankan di AIDE (Android IDE di HP)
1. Buka aplikasi **AIDE - IDE for Android Java C++** di HP Android Anda.
2. Ekstrak file zip proyek ini ke folder internal penyimpanan HP, misalnya di `/sdcard/AppProjects/ZakatKeuangan`.
3. Buka AIDE -> Pilih menu **Open Project** -> Cari folder proyek yang diekstrak.
4. Pastikan jaringan internet aktif agar AIDE dapat mengunduh pustaka `play-services-ads:22.6.0`.
5. Tekan tombol **Run (Segitiga Putih/Play)** di pojok kanan atas.
6. AIDE akan melakukan kompilasi file Java dan merakit APK.
7. Pilih **Install** dan buka aplikasinya!

*Tips AIDE:* Jika AIDE versi lama mengalami kendala pustaka AndroidX, pastikan AIDE Anda sudah mendukung Maven Central repository.

---

## 2. Menjalankan di Android Studio (PC / Laptop)
1. Buka **Android Studio**.
2. Pilih **File** > **Open** > Pilih folder proyek ini.
3. Tunggu proses **Gradle Sync** selesai secara otomatis.
4. Sambungkan HP Android dengan kabel USB (aktifkan USB Debugging) atau gunakan Emulator.
5. Klik tombol hijau **Run 'app'** (Shift + F10).
6. Iklan Banner AdMob akan muncul di bagian bawah layar.

---

## 3. Cara Membuat Signed APK untuk Amazon Appstore
Amazon Appstore menerima format **APK** (atau AAB):
1. Di Android Studio, pilih menu **Build** > **Generate Signed Bundle / APK**.
2. Pilih **APK** lalu klik **Next**.
3. Buat KeyStore baru (jika belum punya) atau gunakan KeyStore Anda:
   - Simpan file `.jks` di tempat aman.
   - Masukkan Password, Alias, dan Validity (minimal 25 tahun).
4. Pilih build variant: **release**.
5. Centang tanda tangan **V1 (Jar Signature)** dan **V2 (Full APK Signature)**.
6. Klik **Finish**. Berkas APK rilis siap di folder `app/release/app-release.apk`.

---

## 4. Langkah Upload ke Amazon Appstore Developer Portal
1. Buka situs [Amazon Developer Console](https://developer.amazon.com/).
2. Masuk / Daftar akun gratis Amazon Developer.
3. Masuk ke **Apps & Services** > **Add New App** > Pilih **Android**.
4. Isi informasi aplikasi:
   - **App Title**: Kalkulator Zakat & Catatan Keuangan
   - **App SKU**: `com.zakat.keuangan.amazon`
   - **Default Language**: Indonesian (atau English)
5. Unggah berkas APK rilis (`app-release.apk`).
6. Masukkan Deskripsi, Ikon Aplikasi (512x512 PNG), dan Screenshot (minimal 3 gambar).
7. Konfirmasi bahwa aplikasi memuat iklan AdMob (Centang "Yes, this app displays ads").
8. Tetapkan harga: **Free**.
9. Klik **Submit App**. Tim review Amazon biasanya menyetujui dalam 24–48 jam!

---

## Catatan Penting AdMob:
- Jangan klik iklan Anda sendiri pada perangkat asli saat pengujian (berpotensi terkena invalid traffic policy).
- Iklan baru dari AdMob terkadang memerlukan waktu beberapa jam untuk mulai menampilkan tayangan (impression) pertama kali setelah aplikasi dirilis.