package co.edu.uco.uspeak.Class;

/**
 * Created by Isabel on 21/03/2018.
 */

public class Chat {
    private String keyFirebase;
    private String user_creator;
    private String user_receptor;
    private int audios_counter;

    public Chat(){}

    public Chat(String keyFirebase, String user_creator, String user_receptor, int audios_counter) {
        this.keyFirebase = keyFirebase;
        this.user_creator = user_creator;
        this.user_receptor = user_receptor;
        this.audios_counter = audios_counter;
    }

    public String getKeyFirebase() {
        return keyFirebase;
    }

    public void setKeyFirebase(String keyFirebase) {
        this.keyFirebase = keyFirebase;
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

    public int getAudios_counter() {
        return audios_counter;
    }

    public void setAudios_counter(int audios_counter) {
        this.audios_counter = audios_counter;
    }
}
