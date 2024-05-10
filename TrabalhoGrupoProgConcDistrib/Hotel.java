import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Hotel {
    private final int NUM_QUARTOS = 10;
    private final int CAPACIDADE_QUARTO = 4;
    private final int NUM_HOSPEDES = 50;
    private final int NUM_CAMAREIRAS = 10;
    private final int NUM_RECEPCIONISTAS = 5;
    private final long TEMPO_PERMANENCIA = 5000;

    private List<Quarto> quartos;
    private List<Thread> hospedes;
    private List<Thread> camareiras;
    private List<Thread> recepcionistas;
    private Semaphore semaforoRecepcao;
    private Semaphore semaforoLimpeza;

    private List<Hospede> filaEspera;
    private Random random;
    
    private List<Grupo> gruposEspera;

    public Hotel() {
        quartos = new ArrayList<>();
        hospedes = new ArrayList<>();
        camareiras = new ArrayList<>();
        recepcionistas = new ArrayList<>();
        semaforoRecepcao = new Semaphore(1);
        semaforoLimpeza = new Semaphore(1);
        filaEspera = new ArrayList<>();
        random = new Random(); // Instanciando a classe Random
        this.gruposEspera = new ArrayList<>();
    
        // Inicializar quartos
        for (int i = 0; i < NUM_QUARTOS; i++) {
            quartos.add(new Quarto(i + 1, CAPACIDADE_QUARTO));
        }
    
        // Inicializar hóspedes
        for (int i = 0; i < NUM_HOSPEDES; i++) {
            Grupo grupo = new Grupo(i + 1, random.nextInt(10) + 1); // Supondo que o número de pessoas varie de 1 a 10
            System.out.println("Grupo " + (i + 1) + " com " + grupo.getNumeroPessoas() + " pessoas chegou ao hotel.");
            
            if (grupo.getNumeroPessoas() > 4) {
                // Se o grupo tiver mais de 4 pessoas, dividir em subgrupos
                int numSubgrupos = (int) Math.ceil((double) grupo.getNumeroPessoas() / 4); // Número de subgrupos necessários
                List<Grupo> subgrupos = new ArrayList<>();
                int numPessoasRestantes = grupo.getNumeroPessoas();
                for (int k = 0; k < numSubgrupos; k++) {
                    int numPessoasNoSubgrupo = Math.min(numPessoasRestantes, 4);
                    subgrupos.add(new Grupo(i + 1, numPessoasNoSubgrupo));
                    numPessoasRestantes -= numPessoasNoSubgrupo;
                }
                
                // Atribuir quartos para o grupo original e seus subgrupos
                for (Grupo subgrupo : subgrupos) {
                    atribuirQuartos(subgrupo);
                }
            } else {
                // Atribuir quartos para o grupo original
                atribuirQuartos(grupo);
            }
        }

    // Inicializar recepcionistas
        for (int i = 0; i < NUM_RECEPCIONISTAS; i++) {
            recepcionistas.add(new Thread(new Recepcionista(i + 1, this)));
        }
        
        //Inicializar camareiras
        for (int i = 0; i < NUM_CAMAREIRAS; i++) {
        	camareiras.add(new Thread(new Camareira(i + 1, this)));
        }
    }

    public void iniciar() {
        // Iniciar threads
        for (Thread hospede : hospedes) {
            hospede.start();
        }
        for (Thread camareira : camareiras) {
            camareira.start();
        }
        for (Thread recepcionista : recepcionistas) {
            recepcionista.start();
        }

        // Aguardar todas as threads terminarem
        for (Thread hospede : hospedes) {
            try {
                hospede.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        for (Thread camareira : camareiras) {
            try {
                camareira.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        for (Thread recepcionista : recepcionistas) {
            try {recepcionista.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
 
        while (!filaEsperaCheckout.isEmpty()) {
            fazerCheckoutAutomatico();
            esperar(1000);
        }
    }

  public Quarto getQuartoDisponivel() {
        for (Quarto quarto : quartos) {
            if (quarto.isDisponivel()) {
                return quarto;
            }
        }
        return null;
    }

    public void esperar(long tempo) {
        try {
            Thread.sleep(tempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getNumHospedes() {
        return NUM_HOSPEDES;
    }

    public Semaphore getSemaforoRecepcao() {
        return semaforoRecepcao;
    }

    public Semaphore getSemaforoLimpeza() {
        return semaforoLimpeza;
    }

    public List<Quarto> getQuartos() {
        return quartos;
    }

    public synchronized void adicionarFilaEspera(Hospede hospede) {
        filaEspera.add(hospede);
    }

    public synchronized boolean removerFilaEspera(Hospede hospede) {
        return filaEspera.remove(hospede);
    }

    public synchronized Hospede proximoFilaEspera() {
        if (!filaEspera.isEmpty()) {
            return filaEspera.remove(0);
        }
        return null;
    }
    
    private void atribuirQuartos(Grupo grupo) {
        int numQuartosNecessarios = (int) Math.ceil((double) grupo.getNumeroPessoas() / CAPACIDADE_QUARTO);
        for (int i = 0; i < numQuartosNecessarios; i++) {
            if (!quartos.isEmpty()) {
                Quarto quarto = quartos.remove(0);
                grupo.adicionarQuarto(quarto);
                System.out.println("Quarto " + quarto.getNumero() + " atribuído ao Grupo " + grupo.getNumero());
            } else {
                System.out.println("Não há quartos disponíveis para o Grupo " + grupo.getNumero());
            }
        }

        for (Hospede hospede : grupo.getHospedes()) {
            Quarto quartoAtribuido = hospede.getQuartoAtribuido();
            if (quartoAtribuido != null) {
                checkoutAutomatico(hospede, TEMPO_PERMANENCIA);
            }
        }
    }
