package portal.ocorrencia;

// Calcula a distância entre dois pontos do mapa usando a fórmula de Haversine.
// Não precisa decorar a matemática: o importante é saber que ela considera
// a curvatura da Terra e devolve o resultado em METROS.
public final class CalculadoraDeDistancia {

    private static final double RAIO_DA_TERRA_EM_METROS = 6_371_000;

    // Construtor privado: esta classe só tem métodos "static", ninguém precisa
    // criar um objeto dela (usa assim: CalculadoraDeDistancia.emMetros(...))
    private CalculadoraDeDistancia() {
    }

    public static double emMetros(double lat1, double lng1, double lat2, double lng2) {
        // A fórmula trabalha com ângulos em radianos, não em graus
        double diferencaLat = Math.toRadians(lat2 - lat1);
        double diferencaLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(diferencaLat / 2) * Math.sin(diferencaLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(diferencaLng / 2) * Math.sin(diferencaLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_DA_TERRA_EM_METROS * c;
    }
}