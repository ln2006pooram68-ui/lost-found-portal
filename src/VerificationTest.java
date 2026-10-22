
public class VerificationTest {

    public static void main(String[] args) {

        VerificationDAO dao = new VerificationDAO();

        System.out.println("Credibility score test");

        double score = dao.getCredibilityScore(1);

        System.out.println("Item 1 score: " + score + "%");
    }
}
