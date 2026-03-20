import org.springframework.security.crypto.bcrypt.BCrypt;
public class GenBcrypt {
  public static void main(String[] args) {
    String raw = args.length > 0 ? args[0] : "123456";
    System.out.println(BCrypt.hashpw(raw, BCrypt.gensalt()));
  }
}
