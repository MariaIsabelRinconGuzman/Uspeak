package co.edu.uco.uspeak;

import java.util.Map;

import co.edu.uco.uspeak.Class.Mensaje;

/**
 * Created by user on 05/09/2017. 05
 */

public class MensajeEnviar extends Mensaje {
    private Map hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Map hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora) {
        super(mensaje, nombre, fotoPerfil, type_mensaje);
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String urlFoto, String nombre, String fotoPerfil, String type_mensaje, Map hora, String audio) {
        super(mensaje, urlFoto, nombre, fotoPerfil, type_mensaje, audio);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
