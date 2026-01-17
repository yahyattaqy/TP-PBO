public class TransaksiItem {
    private Barang barang;
    private int jumlah;

    public TransaksiItem(Barang barang, int jumlah) {
        setBarang(barang);
        setJumlah(jumlah);
    }

    public Barang getBarang() {
        return barang;
    }

    public void setBarang(Barang barang) {
        if (barang == null) {
            throw new IllegalArgumentException("Barang tidak boleh null!");
        }
        this.barang = barang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah barang harus lebih dari 0!");
        }
        this.jumlah = jumlah;
    }

    public double hitungSubtotal() {
        return jumlah * barang.getHarga();
    }

    @Override
    public String toString() {
        return "TransaksiItem{" +
                "barang=" + barang.getNama() +
                ", jumlah=" + jumlah +
                ", subtotal=" + hitungSubtotal() +
                '}';
    }
}
