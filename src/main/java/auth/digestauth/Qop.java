package auth.digestauth;

public enum Qop {
    AUTH("auth"), AUTH_INT("auth-int");

    String name;

    Qop(String name){this.name = name;}
}
