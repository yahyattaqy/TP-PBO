import java.util.ArrayList;

public class Transaksi {
    private ArrayList<TransaksiItem> daftarItem;

    public Transaksi() {
        this.daftarItem = new ArrayList<>();
    }

    public void tambahItem(TransaksiItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item tidak boleh null!");
        }
        daftarItem.add(item);
    }

    public void hapusItem(int index) {
        if (index < 0 || index >= daftarItem.size()) {
            throw new IllegalArgumentException("Index tidak valid!");
        }
        daftarItem.remove(index);
    }

    public TransaksiItem getItem(int index) {
        if (index < 0 || index >= daftarItem.size()) {
            return null;
        }
        return daftarItem.get(index);
    }

    public double hitungTotal() {
        double total = 0;
        for (TransaksiItem item : daftarItem) {
            total += item.hitungSubtotal();
        }
        return total;
    }

    public ArrayList<TransaksiItem> getDaftarItem() {
        return daftarItem;
    }

    public void tampilkanTransaksi() {
        System.out.println("===== TRANSAKSI =====");
        for (int i = 0; i < daftarItem.size(); i++) {
            System.out.println((i + 1) + ". " + daftarItem.get(i));
        }
        System.out.println("Total: " + hitungTotal());
        System.out.println("====================");
    }
}
