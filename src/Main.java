public class Main {
    public static void main(String[] args) {
        System.out.println("===== APLIKASI KASIR SEDERHANA (POS) =====\n");

        System.out.println("--- Test 1: Barang Biasa ---");
        Barang barang1 = new Barang("B001", "Pensil", 5000);
        Barang barang2 = new Barang("B002", "Buku", 15000);
        System.out.println(barang1);
        System.out.println(barang2);

        System.out.println("\n--- Test 2: Barang Dengan Diskon (Polimorfisme) ---");
        BarangDiskon barangDiskon = new BarangDiskon("B003", "Pulpen", 8000, 20);
        System.out.println(barangDiskon);
        System.out.println(barangDiskon.detailHarga());

        System.out.println("\n--- Test 3: Transaksi ---");
        Transaksi transaksi = new Transaksi();

        TransaksiItem item1 = new TransaksiItem(barang1, 10);
        TransaksiItem item2 = new TransaksiItem(barang2, 5);
        TransaksiItem item3 = new TransaksiItem(barangDiskon, 3);

        transaksi.tambahItem(item1);
        transaksi.tambahItem(item2);
        transaksi.tambahItem(item3);

        transaksi.tampilkanTransaksi();

        System.out.println("\n--- Test 4: Hapus Item ---");
        System.out.println("Menghapus item ke-1...");
        transaksi.hapusItem(0);
        transaksi.tampilkanTransaksi();

        System.out.println("\n--- Test 5: Error Handling (Validasi) ---");
        try {
            Barang barangInvalid = new Barang("", "Barang Invalid", 1000);
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Error tertangkap: " + e.getMessage());
        }

        try {
            Barang barangHargaNegatif = new Barang("B004", "Barang", -5000);
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Error tertangkap: " + e.getMessage());
        }

        try {
            TransaksiItem itemInvalid = new TransaksiItem(barang1, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Error tertangkap: " + e.getMessage());
        }

        System.out.println("\n===== TESTING SELESAI =====");
        System.out.println("Untuk GUI, jalankan: java KasirApp");
    }
}
