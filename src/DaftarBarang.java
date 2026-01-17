import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Class untuk menyimpan daftar barang yang tersedia secara statis
 */
public class DaftarBarang {
    private static final List<Barang> daftarBarang = new ArrayList<>();
    private static final String FILE_PATH = "barang.txt";

    // Inisialisasi daftar barang secara statis
    static {
        // Coba muat data dari file terlebih dahulu
        if (!muatDariFile()) {
            // Jika file tidak ada atau gagal, gunakan data default
            daftarBarang.add(new Barang("B001", "Pensil", 5000, 50));
            daftarBarang.add(new Barang("B002", "Buku Tulis", 15000, 30));
            daftarBarang.add(new Barang("B003", "Pulpen", 8000, 40));
            daftarBarang.add(new Barang("B004", "Penghapus", 3000, 60));
            daftarBarang.add(new Barang("B005", "Penggaris", 4000, 25));
            daftarBarang.add(new Barang("B006", "Spidol Besar", 12000, 20));
            daftarBarang.add(new Barang("B007", "Tinta Printer", 45000, 10));
            daftarBarang.add(new Barang("B008", "Kertas HVS A4", 50000, 15));
            daftarBarang.add(new Barang("B009", "Map Belanko", 2000, 100));
            daftarBarang.add(new Barang("B010", "Stik Lem", 6000, 35));
            // Simpan data default ke file
            simpanKeFile();
        }
    }

    /**
     * Mendapatkan semua daftar barang
     */
    public static List<Barang> getDaftarBarang() {
        return new ArrayList<>(daftarBarang);
    }

    /**
     * Mendapatkan barang berdasarkan index
     */
    public static Barang getBarangByIndex(int index) {
        if (index >= 0 && index < daftarBarang.size()) {
            return daftarBarang.get(index);
        }
        return null;
    }

    /**
     * Mendapatkan nama barang berdasarkan index (untuk menampilkan di ComboBox)
     */
    public static String getNamaBarangByIndex(int index) {
        Barang barang = getBarangByIndex(index);
        if (barang != null) {
            return barang.getKode() + " - " + barang.getNama() + " (Rp " + barang.getHarga() + ")";
        }
        return null;
    }

    /**
     * Mendapatkan array semua nama barang untuk ComboBox
     */
    public static String[] getNamaBarangArray() {
        String[] result = new String[daftarBarang.size()];
        for (int i = 0; i < daftarBarang.size(); i++) {
            Barang barang = daftarBarang.get(i);
            result[i] = barang.getKode() + " - " + barang.getNama() + " (Rp " + (int)barang.getHarga() + ")";
        }
        return result;
    }

    /**
     * Mendapatkan total jumlah barang
     */
    public static int getTotalBarang() {
        return daftarBarang.size();
    }

    /**
     * Mendapatkan string stok barang berdasarkan index
     */
    public static String getStokBarangByIndex(int index) {
        Barang barang = getBarangByIndex(index);
        if (barang != null) {
            return "Stok: " + barang.getStok();
        }
        return "Stok: 0";
    }

    /**
     * Tambah barang baru (CREATE)
     */
    public static boolean tambahBarang(Barang barang) {
        // Cek apakah kode sudah ada
        for (Barang b : daftarBarang) {
            if (b.getKode().equalsIgnoreCase(barang.getKode())) {
                return false; // Kode sudah ada
            }
        }
        daftarBarang.add(barang);
        simpanKeFile();
        return true;
    }

    /**
     * Update barang berdasarkan kode (UPDATE)
     */
    public static boolean updateBarang(String kode, String namaBaru, double hargaBaru, int stokBaru) {
        for (Barang b : daftarBarang) {
            if (b.getKode().equalsIgnoreCase(kode)) {
                b.setNama(namaBaru);
                b.setHarga(hargaBaru);
                b.setStok(stokBaru);
                simpanKeFile();
                return true;
            }
        }
        return false; // Barang tidak ditemukan
    }

    /**
     * Hapus barang berdasarkan kode (DELETE)
     */
    public static boolean hapusBarang(String kode) {
        for (int i = 0; i < daftarBarang.size(); i++) {
            if (daftarBarang.get(i).getKode().equalsIgnoreCase(kode)) {
                daftarBarang.remove(i);
                simpanKeFile();
                return true;
            }
        }
        return false; // Barang tidak ditemukan
    }

    /**
     * Cari barang berdasarkan kode (READ)
     */
    public static Barang cariBarangByKode(String kode) {
        for (Barang b : daftarBarang) {
            if (b.getKode().equalsIgnoreCase(kode)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Simpan data barang ke file txt
     */
    public static boolean simpanKeFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Barang barang : daftarBarang) {
                // Format: KODE|NAMA|HARGA|STOK
                String line = String.format("%s|%s|%.2f|%d",
                    barang.getKode(),
                    barang.getNama(),
                    barang.getHarga(),
                    barang.getStok());
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Gagal menyimpan data ke file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Muat data barang dari file txt
     */
    public static boolean muatDariFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            daftarBarang.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String kode = parts[0];
                    String nama = parts[1];
                    double harga = Double.parseDouble(parts[2]);
                    int stok = Integer.parseInt(parts[3]);
                    daftarBarang.add(new Barang(kode, nama, harga, stok));
                }
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Gagal memuat data dari file: " + e.getMessage());
            return false;
        }
    }
}
