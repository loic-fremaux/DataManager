package fr.thefoxy41.dataManager.auth;

public abstract class Credentials {
    protected String host;
    protected String user;
    protected String password;
    protected String clientName;
    protected int port;

    public Credentials(String host, String user, String password, String clientName, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.clientName = clientName;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getClientName() {
        return clientName;
    }

    public abstract String toUri();
}
