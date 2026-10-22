
public class VerificationService {

    private VerificationDAO dao = new VerificationDAO();

    public String getVerificationStatus(int itemId) {

        int totalVotes = dao.getTotalVotes(itemId);

        if (totalVotes == 0) {
            return "UNVERIFIED";
        }

        double score = dao.getCredibilityScore(itemId);

        if (score >= 60.0) {
            return "TRUSTWORTHY";
        } else {
            return "NEEDS_REVIEW";
        }
    }
}

