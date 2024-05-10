import java.util.Random;

class Camareira implements Runnable {
    private int id;
    private Hotel hotel;

    public Camareira(int id, Hotel hotel) {
        this.id = id;
        this.hotel = hotel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                hotel.getSemaforoLimpeza().acquire();
                for (Quarto quarto : hotel.getQuartos()) {
                    if (quarto.isLimpezaNecessaria()) {
                        System.out.println("Camareira " + id + " entrou no Quarto " + quarto.getNumero() + " para limpeza.");
                        hotel.esperar(new Random().nextInt(5000)); // Simular limpeza
                        quarto.setLimpezaNecessaria(false);
                        System.out.println("Camareira " + id + " terminou a limpeza do Quarto " + quarto.getNumero());
                    }
                }
                hotel.getSemaforoLimpeza().release();
                hotel.esperar(new Random().nextInt(5000)); // Camareira espera um pouco antes de verificar novamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
