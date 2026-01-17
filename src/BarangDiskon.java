public class BarangDiskon extends Barang {
    private double diskon; 
    
    public BarangDiskon(String kode, String nama, double harga, double diskon) {
        super(kode, nama, harga);
        setDiskon(diskon);
    }

    public BarangDiskon(String kode, String nama, double harga, double diskon, int stok) {
        super(kode, nama, harga, stok);
        setDiskon(diskon);
    }

    public double getDiskon() {
        return diskon;
    }

    public void setDiskon(double diskon) {
        if (diskon < 0 || diskon > 100) {
            throw new IllegalArgumentException("Diskon harus antara 0-100%!");
        }
        this.diskon = diskon;
    }

    @Override
    public double getHarga() {
        double hargaAsli = super.getHarga();
        double hargaDiskon = hargaAsli * (diskon / 100.0);
        return hargaAsli - hargaDiskon;
    }

    public double getHargaAsli() {
        return super.getHarga();
    }

    public String detailHarga() {
        return "Harga Asli: " + getHargaAsli() +
                ", Diskon: " + diskon + "%" +
                ", Harga Akhir: " + getHarga();
    }

    @Override
    public String toString() {
        return "BarangDiskon{" +
                "kode='" + getKode() + '\'' +
                ", nama='" + getNama() + '\'' +
                ", hargaAsli=" + getHargaAsli() +
                ", diskon=" + diskon + "%" +
                ", hargaAkhir=" + getHarga() +
                '}';
    }
}
