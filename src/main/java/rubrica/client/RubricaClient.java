package rubrica.client;

import java.net.ConnectException;
import java.util.Scanner;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RubricaClient {
    
    private static WebTarget rubrica;
    
    public static void main(String[] args) {
        
        // Collegamento al servizio "rubrica"
        Client c = ClientBuilder.newClient();
        rubrica = c.target("http://localhost:50001/rubrica");
        
        // Menù interattivo per interagire con il servizio "rubrica"
        Scanner tastiera = new Scanner(System.in);
        String scelta = "";
        while(!scelta.equalsIgnoreCase("exit")) {
            System.out.println();
            System.out.print("Cosa vuoi fare? ");
            scelta = tastiera.nextLine();
            if(scelta.equalsIgnoreCase("post")) {
                System.out.print("Cognome: ");
                String cognome = tastiera.nextLine();
                System.out.print("Nome: ");
                String nome = tastiera.nextLine();
                System.out.print("Numero: ");
                String numero = tastiera.nextLine();
                post(cognome,nome,numero);
            } else if(scelta.equalsIgnoreCase("put")) {
                System.out.print("Cognome: ");
                String cognome = tastiera.nextLine();
                System.out.print("Nome: ");
                String nome = tastiera.nextLine();
                System.out.print("Numero: ");
                String numero = tastiera.nextLine();
                put(cognome,nome,numero);
            } else if(scelta.equalsIgnoreCase("get")) {
                System.out.print("Cognome: ");
                String cognome = tastiera.nextLine();
                System.out.print("Nome: ");
                String nome = tastiera.nextLine();
                get(cognome,nome);
            } else if(scelta.equalsIgnoreCase("delete")) {
                System.out.print("Cognome: ");
                String cognome = tastiera.nextLine();
                System.out.print("Nome: ");
                String nome = tastiera.nextLine();
                delete(cognome,nome);
            } else if(scelta.equalsIgnoreCase("exit")) {
                System.out.println("Ciao!");
            } else {
                System.err.println("ERRORE: Operazione non consentita");
            }
        }
    }
    
    private static void post(String cognome, String nome, String numero) {
        Response r;
        try {
            if(nome.isEmpty()) {
                r = rubrica.queryParam("cognome", cognome)
                    .queryParam("numero",numero)
                    .request().post(Entity.entity("", MediaType.TEXT_PLAIN));  
            }
            else {
                r = rubrica.queryParam("cognome", cognome)
                    .queryParam("nome", nome)
                    .queryParam("numero",numero)
                    .request().post(Entity.entity("", MediaType.TEXT_PLAIN));
            }
        } catch(ProcessingException e) {
            if(e.getCause() instanceof ConnectException) {
                System.err.println("ERRORE: Impossibile connettersi al servizio");
                return;
            } else throw e;
        }
        // Se il numero di telefono è stato aggiunto, stampa messaggio di conferma
        if(r.getStatus() == Status.CREATED.getStatusCode()) {
            System.out.println("Numero di telefono aggiunto e reperibile al seguente indirizzo: " + r.getHeaders().get("location"));
        }
        // Se invece non è stato possibile, stampa messaggio di errore
        else {
            System.err.println("Impossibile aggiungere il numero digitato (" + r.getStatusInfo() + ")");
        }
    }
    
    private static void put(String cognome, String nome, String numero) {
        Response r;
        try {
            r = rubrica.path(cognome)
                    .path(nome)
                    .queryParam("numero", numero)
                    .request()
                    .put(Entity.entity("", MediaType.TEXT_PLAIN));
        }  catch(ProcessingException e) {
            if(e.getCause() instanceof ConnectException) {
                System.err.println("ERRORE: Impossibile connettersi al servizio");
                return;
            } else throw e;
        }
        // Se il numero di telefono è stato aggiornato, stampa messaggio di conferma
        if(r.getStatus() == Status.OK.getStatusCode()) {
            System.out.println("Numero di telefono correttamente aggiornato");
        }
        // Altrimenti, stampa messaggio di errore
        else {
            System.err.println("Impossibile aggiornare il numero richiesto (" + r.getStatusInfo() + ")");
        }
    }
    
    private static void get(String cognome, String nome) {
        Response r;
        try {
            r = rubrica.path(cognome).path(nome).request().get();
        } catch(ProcessingException e) {
            if(e.getCause() instanceof ConnectException) {
                System.err.println("ERRORE: Impossibile connettersi al servizio");
                return;
            } else throw e;
        }
        // Se il numero di telefono è stato recuperato, lo stampa a video
        if (r.getStatus() == Status.OK.getStatusCode()) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject body = (JSONObject) parser.parse(r.readEntity(String.class));
                System.out.println("Numero: " + body.get("numero"));
            } catch(Exception e) {
                System.out.println("ERRORE: Impossibile leggere output fornito dal servizio");
            }
        }
        // Altrimenti, stampa messaggio di errore
        else {
            System.err.println("Impossibile recuperare il numero richiesto (" + r.getStatusInfo() + ")");
        }
    }
    
    private static void delete(String cognome, String nome) {
        Response r;
        try {
            r = rubrica.path(cognome).path(nome).request().delete();
        } catch(ProcessingException e) {
            if(e.getCause() instanceof ConnectException) {
                System.err.println("ERRORE: Impossibile connettersi al servizio");
                return;
            } else throw e;
        }
        // Se il numero di telefono è stato eliminato, stampa un messaggio di conferma
        if (r.getStatus() == Status.OK.getStatusCode()) {
            System.out.println("Numero di telefono correttamente eliminato");
        }
        // Altrimenti, stampa un messaggio di errore
        else {
            System.err.println("Impossibile eliminare il numero richiesto (" + r.getStatusInfo() + ")");
        }
    }
}
