import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransaksiSelesai {
    private LocalDateTime tanggal;
    private List<TransaksiItem> items;
    private double subtotal;
    private double persenDiskon;
    private double totalDiskon;
    private double totalAkhir;
    private double nominalBayar;
    private double kembalian;
    
    public TransaksiSelesai(List<TransaksiItem> items, double subtotal, double persenDiskon, 
                            double totalDiskon, double totalAkhir, double nominalBayar, double kembalian) {
        this.tanggal = LocalDateTime.now();
        this.items = new ArrayList<>(items); // Copy list
        this.subtotal = subtotal;
        this.persenDiskon = persenDiskon;
        this.totalDiskon = totalDiskon;
        this.totalAkhir = totalAkhir;
        this.nominalBayar = nominalBayar;
        this.kembalian = kembalian;
    }
    
    public LocalDateTime getTanggal() {
        return tanggal;
    }
    
    public void setTanggal(LocalDateTime tanggal) {
        this.tanggal = tanggal;
    }
    
    public String getTanggalFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return tanggal.format(formatter);
    }
    
    public List<TransaksiItem> getItems() {
        return items;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public double getPersenDiskon() {
        return persenDiskon;
    }
    
    public double getTotalDiskon() {
        return totalDiskon;
    }
    
    public double getTotalAkhir() {
        return totalAkhir;
    }
    
    public double getNominalBayar() {
        return nominalBayar;
    }
    
    public double getKembalian() {
        return kembalian;
    }
    
    public int getTotalItem() {
        int total = 0;
        for (TransaksiItem item : items) {
            total += item.getJumlah();
        }
        return total;
    }
}
