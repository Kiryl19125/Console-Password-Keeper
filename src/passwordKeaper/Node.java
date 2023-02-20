package passwordKeaper;

public class Node {
    private String password;
    private String login;
    private String webSite;

    public Node(String password, String login, String webSite) {
        this.password = password;
        this.login = login;
        this.webSite = webSite;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getWebSite() {
        return webSite;
    }

}
