import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RiwayatTransaksi {
    private static List<TransaksiSelesai> riwayat = new ArrayList<>();
    private static final String FILE_PATH = "riwayat_transaksi.txt";
    
    // Load riwayat saat class pertama kali dimuat
    static {
        muatDariFile();
    }
    
    public static void tambahTransaksi(TransaksiSelesai transaksi) {
        riwayat.add(transaksi);
        simpanKeFile();
    }
    
    public static List<TransaksiSelesai> getRiwayat() {
        return riwayat;
    }
    
    public static int getJumlahTransaksi() {
        return riwayat.size();
    }
    
    public static TransaksiSelesai getTransaksi(int index) {
        if (index >= 0 && index < riwayat.size()) {
            return riwayat.get(index);
        }
        return null;
    }
    
    public static void clearRiwayat() {
        riwayat.clear();
        simpanKeFile();
    }
    
    /**
     * Simpan riwayat transaksi ke file
     */
    public static boolean simpanKeFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (TransaksiSelesai ts : riwayat) {
                // Format: TANGGAL|SUBTOTAL|PERSEN_DISKON|TOTAL_DISKON|TOTAL_AKHIR|NOMINAL_BAYAR|KEMBALIAN|JUMLAH_ITEM
                writer.write(ts.getTanggal().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                writer.write("|");
                writer.write(String.valueOf(ts.getSubtotal()));
                writer.write("|");
                writer.write(String.valueOf(ts.getPersenDiskon()));
                writer.write("|");
                writer.write(String.valueOf(ts.getTotalDiskon()));
                writer.write("|");
                writer.write(String.valueOf(ts.getTotalAkhir()));
                writer.write("|");
                writer.write(String.valueOf(ts.getNominalBayar()));
                writer.write("|");
                writer.write(String.valueOf(ts.getKembalian()));
                writer.write("|");
                writer.write(String.valueOf(ts.getItems().size()));
                writer.newLine();
                
                // Tulis detail item
                for (TransaksiItem item : ts.getItems()) {
                    writer.write("ITEM|");
                    writer.write(item.getBarang().getKode());
                    writer.write("|");
                    writer.write(item.getBarang().getNama());
                    writer.write("|");
                    writer.write(String.valueOf(item.getBarang().getHarga()));
                    writer.write("|");
                    writer.write(String.valueOf(item.getJumlah()));
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Gagal menyimpan riwayat: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Muat riwayat transaksi dari file
     */
    public static boolean muatDariFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            riwayat.clear();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("ITEM")) {
                    // Baca header transaksi
                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        LocalDateTime tanggal = LocalDateTime.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        double subtotal = Double.parseDouble(parts[1]);
                        double persenDiskon = Double.parseDouble(parts[2]);
                        double totalDiskon = Double.parseDouble(parts[3]);
                        double totalAkhir = Double.parseDouble(parts[4]);
                        double nominalBayar = Double.parseDouble(parts[5]);
                        double kembalian = Double.parseDouble(parts[6]);
                        int jumlahItem = Integer.parseInt(parts[7]);
                        
                        // Baca items
                        List<TransaksiItem> items = new ArrayList<>();
                        for (int i = 0; i < jumlahItem; i++) {
                            String itemLine = reader.readLine();
                            if (itemLine != null && itemLine.startsWith("ITEM")) {
                                String[] itemParts = itemLine.split("\\|");
                                if (itemParts.length >= 5) {
                                    String kode = itemParts[1];
                                    String nama = itemParts[2];
                                    double harga = Double.parseDouble(itemParts[3]);
                                    int jumlah = Integer.parseInt(itemParts[4]);
                                    
                                    Barang barang = new Barang(kode, nama, harga);
                                    TransaksiItem item = new TransaksiItem(barang, jumlah);
                                    items.add(item);
                                }
                            }
                        }
                        
                        // Buat TransaksiSelesai dengan tanggal custom
                        TransaksiSelesai ts = new TransaksiSelesai(items, subtotal, persenDiskon, 
                                                                   totalDiskon, totalAkhir, nominalBayar, kembalian);
                        ts.setTanggal(tanggal);
                        riwayat.add(ts);
                    }
                }
            }
            return true;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Gagal memuat riwayat: " + e.getMessage());
            return false;
        }
    }
}
