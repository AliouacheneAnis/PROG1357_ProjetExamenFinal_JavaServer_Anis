package project;

// Importation des bibliothèques
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpServer;

public class ServerApp {

    public static void main(String[] args) throws Exception {

        // Création d'un serveur HTTP sur le port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Attribution d'un gestionnaire d'URL ("/data") pour traiter les requêtes
        server.createContext("/data", new DataHandler());

        // Démarrage du serveur
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();

        System.out.println("Server started on port 8080");
    }
}