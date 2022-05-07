package Utils.simplerequest.auth.digestauth;

public enum Qop {
    AUTH("Utils/simplerequest/auth"), AUTH_INT("auth-int");

    String name;

    Qop(String name){this.name = name;}
}
