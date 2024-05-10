import java.util.ArrayList;
import java.util.List;

class Grupo {
    private int numero;
    private int numeroPessoas;
    private List<Quarto> quartos;
    private List<Hospede> hospedes;

    public Grupo(int numero, int numeroPessoas) {
        this.numero = numero;
        this.numeroPessoas = numeroPessoas;
        this.quartos = new ArrayList<>();
        this.hospedes = new ArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public int getNumeroPessoas() {
        return numeroPessoas;
    }
    
    public List<Quarto> getQuartos() {
        return quartos;
    }

    public void adicionarQuarto(Quarto quarto) {
        quartos.add(quarto);
    }
    
    public void adicionarHospede(Hospede hospede) {
        hospedes.add(hospede);
    }
    
    public List<Hospede> getHospedes() {
        return hospedes;
    }

    public void setNumeroPessoas(int numeroPessoas) {
        this.numeroPessoas = numeroPessoas;
    }
    
    public void procurarQuarto() {
        Executavel.hotel.adicionarGrupoEspera(this);
    }
}
