package Service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResetEmailService {

    // Remplacez par vos vraies clés API Mailjet
    private static final String API_KEY = "14f4c3028907d2c2e2933b61b732b8f3"; // Remplacez par votre clé publique Mailjet
    private static final String API_SECRET = "d9ea368b908651371879bf1b67255f4c";

    private MailjetClient client;

    public ResetEmailService() {
        // Utilisation des clés API Mailjet
        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(API_SECRET)
                .build();
        client = new MailjetClient(options);
    }

    public void sendResetPasswordEmail(String recipientEmail, String resetCode) throws MailjetException {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            System.out.println("Aucun destinataire spécifié.");
            return; // Si aucun destinataire n'est spécifié, ne tentez pas d'envoyer l'email
        }

        // Créer un corps HTML stylisé pour l'email
        String styledBody = "<html>"
                + "<head>"
                + "<style>"
                + "body {font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333;} "
                + ".container {background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); border: 4px solid #FF66B2;} "
                + ".header {background-color: #FF66B2; color: white; text-align: center; padding: 10px 0; border-radius: 8px 8px 0 0;} "
                + ".footer {font-size: 12px; color: #777; text-align: center; padding: 10px 0;} "
                + ".button {background-color: #FF66B2; color: white; padding: 12px 20px; text-align: center; border-radius: 5px; text-decoration: none; font-weight: bold;} "
                + "h2 {font-weight: bold;} "
                + "p {font-weight: normal;} "
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'><h2>Réinitialisation de votre mot de passe</h2></div>" // Entête
                + "<p><strong>Bonjour,</strong></p>" // Texte en gras
                + "<p>Vous avez demandé à réinitialiser votre mot de passe. Utilisez le code suivant :</p>"
                + "<p><strong>" + resetCode + "</strong></p>" // Code de réinitialisation
                + "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer ce message.</p>"
                + "<div class='footer'>"
                + "<p><strong>Merci de faire partie de notre communauté.</strong></p>"
                + "<p><strong>Nous vous souhaitons une excellente journée !</strong></p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Créer l'objet JSON du message
        JSONObject message = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject()
                        .put("Email", "sirine.benhammouda@esprit.tn")  // Remplacez par votre adresse email
                        .put("Name", "GlowUP"))   // Nom de l'expéditeur
                .put(Emailv31.Message.TO, new JSONArray().put(new JSONObject()
                        .put("Email", recipientEmail)
                        .put("Name", "Utilisateur")))  // Nom du destinataire
                .put(Emailv31.Message.SUBJECT, "Réinitialisation de votre mot de passe")
                .put(Emailv31.Message.TEXTPART, "Utilisez ce code pour réinitialiser votre mot de passe : " + resetCode)
                .put(Emailv31.Message.HTMLPART, styledBody); // Corps HTML de l'email

        // Créer la requête MailjetRequest
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        // Envoyer la requête et récupérer la réponse
        MailjetResponse response = client.post(request);

        // Vérifier si la requête a été réussie
        if (response.getStatus() == 200) {
            System.out.println("Email de réinitialisation envoyé avec succès !");
        } else {
            System.out.println("Erreur lors de l'envoi de l'email : " + response.getStatus());
            // Vous pouvez aussi afficher les données pour diagnostiquer les erreurs
            System.out.println(response.getData());
        }
    }
}
