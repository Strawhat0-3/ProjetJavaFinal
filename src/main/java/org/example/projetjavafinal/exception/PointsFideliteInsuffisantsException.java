package org.example.projetjavafinal.exception;

public class PointsFideliteInsuffisantsException extends ClientException {
    private final int pointsDisponibles;
    private final int pointsNecessaires;

    public PointsFideliteInsuffisantsException(int disponibles, int necessaires) {
        super(String.format("Points fidélité insuffisants : %d disponibles, %d nécessaires", 
              disponibles, necessaires));
        this.pointsDisponibles = disponibles;
        this.pointsNecessaires = necessaires;
    }
}
