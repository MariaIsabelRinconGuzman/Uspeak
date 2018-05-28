package co.edu.uco.uspeak.Class;

import java.util.Map;

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

    public MensajeEnviar(String name_audio, String mensaje, String nombre, String fotoPerfil, String type_mensaje, Map hora, String user_creator, String user_receptor) {
        super(name_audio, mensaje, nombre, fotoPerfil, type_mensaje, user_creator, user_receptor);
        this.hora = hora;
    }

    public MensajeEnviar(String nombre, String fotoPerfil, String type_mensaje, Map hora, String audio, String user_creator, String user_receptor) {
        super(nombre, fotoPerfil, type_mensaje, audio, user_creator, user_receptor);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
