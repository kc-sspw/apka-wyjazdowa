/**
 * @Author Łukasz Piętka (FUT 2025)
 */
package me.havooc.poligon.model;

public enum TrainingPath {
    WSPOLPRACA("Zerówka Centralna"),
    LIDERSKA("Kompetencje liderskie"),
    DYDAKTYKA("Dydaktyka i Socjal"),
    PROMOCJA("Promocja i Komunikacja"),
    PROJEKTY("Zarządzanie Projektami"),
    WSZYSTKIE("Wszystkie Ścieżki");
    private final String label;
    TrainingPath(String label) { this.label = label; }
    public String getLabel() { return label; }
}