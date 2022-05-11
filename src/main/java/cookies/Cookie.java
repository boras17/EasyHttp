package cookies;

import java.time.LocalDateTime;

public class Cookie {
    private String cookieName;
    private String cookieValue;
    private LocalDateTime expiresAt;
    private int maxAge;
    private String domain;
    private boolean httpOnly;
    private boolean secured;

    public Cookie() {

    }

    public Cookie(String cookieName,
                  LocalDateTime expiresAt,
                  int maxAge,
                  String domain,
                  boolean httpOnly,
                  String cookieValue,
                  boolean secured) {

        this.cookieName = cookieName;
        this.expiresAt = expiresAt;
        this.maxAge = maxAge;
        this.domain = domain;
        this.httpOnly = httpOnly;
        this.cookieValue = cookieValue;
        this.secured = secured;
    }



    public static class CookieBuilder{
        private String cookieName;
        private LocalDateTime expiresAt;
        private int maxAge;
        private String domain;
        private boolean httpOnly;
        private String cookieValue;
        private boolean secured;

        public CookieBuilder setCookieName(String cookieName){
            this.cookieName = cookieName;
            return this;
        }
        public CookieBuilder setExpiresAt(LocalDateTime expiresAt){
            this.expiresAt = expiresAt;
            return this;
        }
        public CookieBuilder setMaxAge(int maxAge){
            this.maxAge = maxAge;
            return this;
        }
        public CookieBuilder setDomain(String domain){
            this.domain = domain;
            return this;
        }
        public CookieBuilder setHttpOnly(boolean httpOnly){
            this.httpOnly = httpOnly;
            return this;
        }
        public CookieBuilder setSecured(boolean secured){
            this.secured = secured;
            return this;
        }
        public CookieBuilder setCookieValue(String cookieValue){
            this.cookieValue = cookieValue;
            return this;
        }

        public Cookie build(){
            return new Cookie(this.cookieName,
                    this.expiresAt,
                    this.maxAge,
                    this.domain,
                    this.httpOnly,
                    this.cookieValue,
                    this.secured);
        }
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getDomain() {
        return domain;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "cookieName='" + cookieName + '\'' +
                ", cookieValue='" + cookieValue + '\'' +
                ", expiresAt=" + expiresAt +
                ", maxAge=" + maxAge +
                ", domain='" + domain + '\'' +
                ", httpOnly=" + httpOnly +
                ", secured=" + secured +
                '}';
    }
}
