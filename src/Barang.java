public class Barang {
    private String kode;
    private String nama;
    private double harga;
    private int stok;

    public Barang(String kode, String nama, double harga) {
        this(kode, nama, harga, 0);
    }

    public Barang(String kode, String nama, double harga, int stok) {
        setKode(kode);
        setNama(nama);
        setHarga(harga);
        setStok(stok);
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode barang tidak boleh kosong!");
        }
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong!");
        }
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif!");
        }
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif!");
        }
        this.stok = stok;
    }

    public void kurangiStok(int jumlah) {
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif!");
        }
        if (this.stok < jumlah) {
            throw new IllegalArgumentException("Stok tidak cukup! Stok tersedia: " + this.stok);
        }
        this.stok -= jumlah;
    }

    public void tambahStok(int jumlah) {
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif!");
        }
        this.stok += jumlah;
    }

    @Override
    public String toString() {
        return "Barang{" +
                "kode='" + kode + '\'' +
                ", nama='" + nama + '\'' +
                ", harga=" + harga +
                '}';
    }
}
