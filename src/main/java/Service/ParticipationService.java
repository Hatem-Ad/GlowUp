package Service;

import Entite.Categorie;
import Entite.Challenge;
import Entite.Participation;
import Entite.Step;
import Utils.Test;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class ParticipationService {
    private Connection con = Test.getInstance().getCon();



    public boolean addParticipation(int userId, int challengeId) throws SQLException {
        Connection con = Test.getInstance().getConnection();

        // Vérifier si l'utilisateur participe déjà au challenge
        String checkQuery = "SELECT COUNT(*) FROM participations WHERE user_id = ? AND challenge_id = ?";
        PreparedStatement checkStmt = con.prepareStatement(checkQuery);
        checkStmt.setInt(1, userId);
        checkStmt.setInt(2, challengeId);
        ResultSet checkResult = checkStmt.executeQuery();

        if (checkResult.next() && checkResult.getInt(1) > 0) {
            // L'utilisateur participe déjà à ce challenge
            return false;
        }

        // Ajouter la participation
        String insertQuery = "INSERT INTO participations (user_id, challenge_id, progression) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = con.prepareStatement(insertQuery);
        insertStmt.setInt(1, userId);
        insertStmt.setInt(2, challengeId);
        insertStmt.setInt(3, 0); // Initialiser la progression à 0

        int rowsInserted = insertStmt.executeUpdate();
        return rowsInserted > 0;
    }

    public int getUserParticipationCount(int userId, int categoryId) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        String query = """
        SELECT COUNT(*) 
        FROM participations p 
        JOIN challenges c ON p.challenge_id = c.id 
        WHERE p.user_id = ? 
        AND c.category_id = ?
    """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, categoryId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("Nombre de participations pour l'utilisateur ID " + userId + " dans la catégorie ID " + categoryId + " : " + count);
                return count;
            }
        }
        return 0;
    }
    public List<String> getEtapes(Challenge challenge) throws SQLException {
        List<String> etapes = new ArrayList<>();
        String sql = "SELECT name, description FROM steps WHERE challenge_id = ? ";  // Sélectionner uniquement name et description

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, challenge.getId());
        //stmt.setInt(2, challenge.getCategoryId());
        ResultSet rs = stmt.executeQuery();

        // Ajouter des logs pour vérifier si des étapes sont récupérées
        int i = 0;
        while (rs.next()) {
            String name = rs.getString("name");
            String description = rs.getString("description");

            // Debug : Affichage des données récupérées
            System.out.println("Etape récupérée : " + name + " - " + description);

            String etape = name + ": " + description;
            etapes.add(etape);
            i++;
        }

        // Si aucune étape n'est récupérée, afficher un message de log
        if (i == 0) {
            System.out.println("Aucune étape trouvée pour le challenge avec l'ID : " + challenge.getId());
        }

        return etapes;
    }


    public boolean updateProgression(Participation participation) throws SQLException {
        String sql = "UPDATE participations SET progression = ? WHERE user_id = ? AND challenge_id = ?";
        PreparedStatement pre = con.prepareStatement(sql);
        pre.setInt(1, participation.getProgression());
        pre.setInt(2, participation.getUserId());
        pre.setInt(3, participation.getChallengeId());

        return pre.executeUpdate() > 0;
    }




    public boolean terminerChallenge(Participation participation)  throws SQLException {

        // Requête SQL pour mettre à jour

        String query = "UPDATE participations SET status = 'terminé' WHERE user_id = ? AND challenge_id = ?";

        try (PreparedStatement pre = con.prepareStatement(query)) {

            // Remplir les paramètres de la requête

            pre.setInt(1, participation.getUserId());
            pre.setInt(2, participation.getChallengeId());

            // Exécuter la mise à jour

            int result = pre.executeUpdate();


            return result > 0;

        }

    }
    public void generatePDF(int userId, int challengeId) {
        try {
            // Requête pour récupérer les informations nécessaires
            String query = """
                SELECT u.email, c.name AS challenge_name, cat.name AS category_name 
                FROM users u 
                JOIN participations p ON u.id = p.user_id 
                JOIN challenges c ON p.challenge_id = c.id 
                JOIN categories cat ON c.category_id = cat.id 
                WHERE u.id = ? AND c.id = ?
            """;
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, challengeId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Récupération des données
                String email = rs.getString("email");
                String challengeName = rs.getString("challenge_name");
                String categoryName = rs.getString("category_name");

                // Génération du chemin du fichier PDF
                String pdfPath = "Challenge_" + challengeName.replaceAll(" ", "_") + ".pdf";

                // Création du PDF
                try (PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
                     Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

                    document.add(new Paragraph("Félicitations !"));
                    document.add(new Paragraph("Vous avez terminé le challenge : " + challengeName));
                    document.add(new Paragraph("Catégorie : " + categoryName));
                    document.add(new Paragraph("Votre score est : 100%"));
                    document.add(new Paragraph("Coordonnées : " + email));

                    System.out.println("PDF généré avec succès : " + pdfPath);
                }
            } else {
                System.out.println("Aucune donnée trouvée pour cet utilisateur et challenge.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


