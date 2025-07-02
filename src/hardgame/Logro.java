package hardgame;

public class Logro {
    private final String nombre;
    private boolean desbloqueado;
    private int progreso;
    private final int objetivo;
    private final String mensajeLogro;
    private boolean mostrarMensajeLogro;

    public Logro(String nombre, int objetivo) {
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.desbloqueado = false;
        this.progreso = 0;
        this.mensajeLogro = "¡Desbloqueaste el logro: " + nombre + "!";
        this.mostrarMensajeLogro = false;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void desbloquear() {
        this.desbloqueado = true;
        this.mostrarMensajeLogro = true;
    }

    public int getProgreso() {
        return progreso;
    }

    public int getObjetivo() {
        return objetivo;
    }

    public void aumentarProgreso(int cantidad) {
        progreso += cantidad;
        if (progreso >= objetivo && !desbloqueado) {
            desbloqueado = true;
            mostrarMensajeLogro = true;
        }
    }

    public String getMensajeLogro() {
        return mensajeLogro;
    }

    public boolean isMostrarMensajeLogro() {
        return mostrarMensajeLogro;
    }

    public void setMostrarMensajeLogro(boolean mostrarMensajeLogro) {
        this.mostrarMensajeLogro = mostrarMensajeLogro;
    }
}