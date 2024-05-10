import java.util.Date;

class Hospede implements Runnable {
    private int id;
    private Hotel hotel;
    private Grupo grupo;
    private Quarto quartoAtribuido;
    private static final long TEMPO_PERMANENCIA = 5000;
    private long tempoChegada;

    public Hospede(int id, Hotel hotel, Grupo grupo) {
        this.id = id;
        this.hotel = hotel;
        this.grupo = grupo;
        this.tempoChegada = new Date().getTime();
    }
    
    public void fazerCheckout() {
        hotel.removerFilaEsperaCheckout(this);
        Quarto quartoAtribuido = this.quartoAtribuido;
        if (quartoAtribuido != null) {
            hotel.removerHospede(quartoAtribuido, this);
        }
    }

    public Quarto getQuartoAtribuido() {
        return quartoAtribuido;
    }

    public void setQuartoAtribuido(Quarto quartoAtribuido) {
        this.quartoAtribuido = quartoAtribuido;
    }
    
    public long getTempoMaximoDePermanencia(){
    	return TEMPO_PERMANENCIA;
    }

    @Override
    public void run() {
        System.out.println("Hóspede " + id + " do Grupo " + grupo.getNumero() + " chegou ao hotel.");
        Quarto quarto = null;
        int tentativas = 0;
        while (quarto == null && tentativas < 2) { // adicionado loop com contagem de tentativas
            try {
                hotel.getSemaforoRecepcao().acquire();
                quarto = hotel.getQuartoDisponivel();
                if (quarto != null) {
                    quarto.setDisponivel(false); 
                    quarto.adicionarHospede(this); 
                    System.out.println("Hóspede " + id + " do Grupo " + grupo.getNumero() + " atribuído ao Quarto " + quarto.getNumero());

                    hotel.esperar(TEMPO_PERMANENCIA);

                    quarto.removerHospede(this); 
                    quarto.checkout(); 

                    long tempoDePermanencia = new Date().getTime() - tempoChegada;
                    if (tempoDePermanencia >= TEMPO_PERMANENCIA) {
                        fazerCheckout();
                        hotel.sairDoHotel(this);
                    }
                } else {
                    System.out.println("Hóspede " + id + " do Grupo " + grupo.getNumero() + " não encontrou quartos disponíveis e foi adicionado à fila de espera.");
                    hotel.adicionarFilaEspera(this);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                tentativas++; // incrementar contagem de tentativas
                hotel.getSemaforoRecepcao().release();
            }
        }
        if (tentativas == 2) { 
            System.out.println("Hóspede " + id + " do Grupo " + grupo.getNumero() + " reclamou e saiu da fila de espera.");
            Thread.currentThread().interrupt();
        }

        if (quartoAtribuido != null) {
            hotel.adicionarFilaEsperaCheckout(this);
        }

        hotel.sairDoHotel(this);
    }
}
