
public class VerificationTest {

    public static void main(String[] args) {

        VerificationDAO dao = new VerificationDAO();

        double score = dao.getCredibilityScore(1);

        System.out.println(
                "Credibility score for item 1: " + score + "%");
    }
}
