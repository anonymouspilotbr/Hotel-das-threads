import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

class Quarto {
    private int numero;
    private int capacidade;
    private boolean disponivel;
    private boolean limpezaNecessaria;
    private List<Hospede> hospedes;
    private final ReentrantLock lock = new ReentrantLock();

    public Quarto(int numero, int capacidade) {
        this.numero = numero;
        this.capacidade = capacidade;
        this.disponivel = true;
        this.hospedes = Collections.synchronizedList(new ArrayList<>());
        this.limpezaNecessaria = false;
    }

    public boolean isDisponivel() {
        lock.lock();
        try {
            return disponivel;
        } finally {
            lock.unlock();
        }
    }

    public void adicionarHospede(Hospede hospede) {
        hospedes.add(hospede);
    }

    public void removerHospede(Hospede hospede) {
        hospedes.remove(hospede); 
    }

    public void checkout() {
        System.out.println("Quarto " + numero + " desocupado.");
        disponivel = true; 
    }

    public void setDisponivel(boolean disponivel) {
        lock.lock();
        try {
            this.disponivel = disponivel;
        } finally {
            lock.unlock();
        }
    }

    public boolean isLimpezaNecessaria() {
        lock.lock();
        try {
            return limpezaNecessaria;
        } finally {
            lock.unlock();
        }
    }

    public void setLimpezaNecessaria(boolean limpezaNecessaria) {
        lock.lock();
        try {
            this.limpezaNecessaria = limpezaNecessaria;
        } finally {
            lock.unlock();
        }
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidade() {
        return capacidade;
    }
}