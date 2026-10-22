public class VerificationTest {
    public static void main(String[] args) {
 
        VerificationDAO dao = new VerificationDAO();

<<<<<<< HEAD
        int itemId = 1;

        int votes = dao.getVoteCount(itemId);
        double score = dao.getCredibilityScore(itemId);
        String status = dao.getVerificationStatus(itemId);

        System.out.println("Item ID: " + itemId);
        System.out.println("Total votes: " + votes);

        if (votes == 0) {
            System.out.println("Credibility score: N/A (no votes yet)");
        } else {
            System.out.printf("Credibility score: %.2f%%%n", score);
        }

        System.out.println("Verification status: " + status);
=======
        System.out.println("Credibility score test");

        double score = dao.getCredibilityScore(1);

        System.out.println("Item 1 score: " + score + "%");
>>>>>>> 25fb50cae658b06511bf4ad977747208ac7f97a9
    }
}
