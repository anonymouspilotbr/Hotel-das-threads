import java.util.Random;

class Recepcionista implements Runnable {
    private int id;
    private Hotel hotel;
    private final int MAX_TENTATIVAS = 2;

    public Recepcionista(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        int hospedesAlocados = 0;
        while (hospedesAlocados < hotel.getNumHospedes()) {
            int tentativas = 0;
            Quarto quarto = null;
            while (tentativas < MAX_TENTATIVAS && quarto == null) {
                try {
                    hotel.getSemaforoRecepcao().acquire();
                    quarto = hotel.getQuartoDisponivel();
                    if (quarto != null) {
                        System.out.println("Recepcionista " + id + " alocou um quarto para um hóspede.");
                        quarto.setDisponivel(false);
                        hotel.getSemaforoRecepcao().release();
                        Thread.sleep(new Random().nextInt(5000)); // Simular processo de check-in
                        quarto.setDisponivel(true);
                        quarto.setLimpezaNecessaria(true);
                        System.out.println("Recepcionista " + id + " liberou o quarto e enviou a chave para a recepção.");
                        hospedesAlocados++;
                    } else {
                        System.out.println("Recepcionista " + id + " não encontrou quartos disponíveis. Tentativa " + (tentativas + 1) + " de " + MAX_TENTATIVAS);
                        hotel.getSemaforoRecepcao().release();
                        Thread.sleep(new Random().nextInt(5000)); // Recepcionista espera um pouco antes de tentar novamente
                    }
                    tentativas++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (tentativas == MAX_TENTATIVAS) {
                // Pausa a execução por um curto período antes de tentar novamente
                try {
	            	Thread.sleep(new Random().nextInt(5000));
	                System.out.println("Recepcionista " + id + " não encontrou quartos disponíveis. O cliente deixou uma reclamação.");
                }catch(InterruptedException e) {
                	e.printStackTrace();
                }
            }
        }
    }
}
