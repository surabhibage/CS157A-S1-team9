import org.mindrot.jbcrypt.BCrypt;

public class HashGen {
    public static void main(String[] args) {
        System.out.println("PASS123_HASH:" + BCrypt.hashpw("pass123", BCrypt.gensalt()));
        System.out.println("ADMINPASS_HASH:" + BCrypt.hashpw("adminpass", BCrypt.gensalt()));
    }
}
