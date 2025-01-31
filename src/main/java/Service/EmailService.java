package Service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class EmailService {

    // Remplacez par vos vraies clés API Mailjet
    private static final String API_KEY = "14f4c3028907d2c2e2933b61b732b8f3"; // Remplacez par votre clé publique Mailjet
    private static final String API_SECRET = "d9ea368b908651371879bf1b67255f4c"; // Remplacez par votre clé privée Mailjet

    private MailjetClient client;

    public EmailService() {
        // Utilisation correcte des clés API comme des chaînes de caractères
        ClientOptions options = ClientOptions.builder()
                .apiKey(API_KEY)  // Utilisation de la clé API publique
                .apiSecretKey(API_SECRET)  // Utilisation de la clé API privée
                .build();
        client = new MailjetClient(options);
    }

    public void sendEmail(List<String> recipientEmails, String subject, String body) throws MailjetException {
        if (recipientEmails == null || recipientEmails.isEmpty()) {
            System.out.println("Aucun destinataire spécifié.");
            return; // Si la liste des destinataires est vide, on ne tente pas d'envoyer l'email
        }

        // Créer l'objet JSON avec les destinataires
        JSONArray recipients = new JSONArray();
        for (String email : recipientEmails) {
            recipients.put(new JSONObject()
                    .put("Email", email)
                    .put("Name", "Utilisateur"));  // Vous pouvez ajuster le nom si nécessaire
        }

        // Créer un corps de message HTML stylisé
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
                + "<div class='header'><h2>Bonne Nouvelle!</h2></div>" // Entête
                + "<p><strong>Bonjour notre chére client,</strong></p>" // Texte en gras
                + "<p><strong>" + body + "</strong></p>" // Corps du message en gras
                + "<div class='footer'>"
                + "<p><strong>Merci de faire partie de notre communauté.</strong></p>" // Texte en gras
                + "<p><strong>Nous vous souhaitons une excellente journée!</strong></p>" // Texte en gras
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Créer l'objet JSON du message
        JSONObject message = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject()
                        .put("Email", "sirine.benhammouda@esprit.tn")  // Votre email
                        .put("Name", "GlowUP"))   // Nom de l'expéditeur
                .put(Emailv31.Message.TO, recipients)
                .put(Emailv31.Message.SUBJECT, subject)
                .put(Emailv31.Message.TEXTPART, body)
                .put(Emailv31.Message.HTMLPART, styledBody); // Corps de l'email avec du design HTML

        // Créer la requête MailjetRequest
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        // Envoyer la requête et récupérer la réponse
        MailjetResponse response = client.post(request);

        // Vérifier si la requête a été réussie
        if (response.getStatus() == 200) {
            System.out.println("Email envoyé avec succès !");
        } else {
            System.out.println("Erreur lors de l'envoi de l'email : " + response.getStatus());
            // Vous pouvez aussi afficher les données pour diagnostiquer les erreurs
            System.out.println(response.getData());
        }
    }
}
