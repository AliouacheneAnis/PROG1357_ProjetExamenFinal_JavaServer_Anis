package project;

// Importation des bibliothèques
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.faunadb.client.query.Language.*;
import com.faunadb.client.FaunaClient;

// class DataHandler POUR L'implémentation de l'interface HttpHandler afin de gérer les requêtes HTTP reçues par le serveur
public class DataHandler implements HttpHandler {

    private int WaterLevel; // Variable pour stocker le niveau d'eau
    private String Quality; // Variable pour stocker la qualité de l'eau

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Récupérer la méthode de requête HTTP (POST, GET, etc.)
        String requestMethod = exchange.getRequestMethod();

        // Vérifier si la méthode de requête est POST
        if (requestMethod.equalsIgnoreCase("POST")) {

            // Lire le corps de la requête en tant que JSON
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String requestBody = reader.readLine();
            reader.close();

            // Parser les données JSON en Map et Récupérer la valeur des données depuis la Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(requestBody, new TypeReference<Map<String, Object>>() {
            });

            // Vérifier si la clé data existe dans la Map , data c'est la cle que j'envoi
            // depuis mon arduino avec la valeur lier
            if (jsonMap.containsKey("data")) {

                try {

                    // Convertir la valeur de data en entier et la stocker dans la variable
                    // WaterLevel
                    WaterLevel = Integer.parseInt(jsonMap.get("data").toString());
                    System.out.println("Received data: " + WaterLevel);

                    // Vérifier le niveau d'eau et Définir sa qualité selon les conditions
                    if (WaterLevel >= 5) {
                        Quality = "GOOD";
                    } else {
                        Quality = "NOT GOOD";
                    }

                    // Se connecter au serveur de base de données FaunaDB et envoyer les données
                    FaunaClient client = FaunaClient.builder()
                        .withSecret("fnAE_mGgjoACTTxwwwhPkM7akuitNGHl-aZHIRMV") // clé d'accès à la base de donnéesFaunaDB
                        .withEndpoint("https://db.fauna.com/") // l'URL de l'endpoint Fauna
                        .build();

                    // Envoi vers serveur fauna et affichage des resultats en meme temps
                    System.out.println(
                            client.query(
                                    Create(
                                            Collection("water"),
                                            Obj(
                                                    "data", Obj(
                                                            "level", Value(WaterLevel),
                                                            "quality", Value(Quality)))))
                                    .get());

                } catch (NumberFormatException e) {
                    System.err.println("Invalid data format: " + requestBody);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}