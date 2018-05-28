package co.edu.uco.uspeak.Class;

/**
 * Created by user on 04/09/2017. 04
 */

public class Mensaje {

    private String mensaje;
    private String nombre;
    private String fotoPerfil;
    private String type_mensaje;
    private String name_audio;
    private String user_creator;
    private String user_receptor;

    public Mensaje() {
    }

    public Mensaje(String mensaje, String nombre, String fotoPerfil, String type_mensaje, String user_creator, String user_receptor) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.type_mensaje = type_mensaje;
        this.user_creator = user_creator;
        this.user_receptor = user_receptor;
    }

    public Mensaje(String name_audio, String mensaje, String nombre, String fotoPerfil, String type_mensaje, String user_creator, String user_receptor) {
        this.name_audio = name_audio;
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
        this.type_mensaje = type_mensaje;
        this.user_creator = user_creator;
        this.user_receptor = user_receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getType_mensaje() {
        return type_mensaje;
    }

    public void setType_mensaje(String type_mensaje) {
        this.type_mensaje = type_mensaje;
    }

    public String getUser_creator() {
        return user_creator;
    }

    public void setUser_creator(String user_creator) {
        this.user_creator = user_creator;
    }

    public String getUser_receptor() {
        return user_receptor;
    }

    public void setUser_receptor(String user_receptor) {
        this.user_receptor = user_receptor;
    }

    public String getName_audio() {
        return name_audio;
    }

    public void setName_audio(String name_audio) {
        this.name_audio = name_audio;
    }
}
