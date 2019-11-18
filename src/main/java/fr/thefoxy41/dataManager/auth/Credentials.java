package fr.thefoxy41.dataManager.auth;

public abstract class Credentials {
    protected String host;
    protected String user;
    protected String password;
    protected String clientName;
    protected int port;
    protected int poolSize;

    public static final int DEFAULT_POOL_SIZE = 4;

    public Credentials() {}

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

    public int getPoolSize() {
        return Math.max(poolSize, DEFAULT_POOL_SIZE);
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public abstract String toUri();
}
