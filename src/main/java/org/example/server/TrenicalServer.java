package org.example.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.persistence.CreaTabelleUtil;
import org.example.persistence.popolaDBUtil;

import java.io.IOException;
import java.sql.SQLException;

public class TrenicalServer {
    private Server server;
    private final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {

        popolaDBUtil.resetDatabase();
        TrenicalServer trenicalServer = new TrenicalServer();
        trenicalServer.start();
        trenicalServer.blockUntilShutdown();
    }

    //Avvia il server gRPC e registra i servizi.
    private void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new TrenicalServiceImpl())
                .build()
                .start();

        System.out.println("Trenical gRPC Server avviato sulla porta: " + PORT);

        // Hook per arresto pulito del server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("\n Arresto del server gRPC Trenical...");
            TrenicalServer.this.stop();
            System.err.println("Server arrestato correttamente.");
        }));
    }

    //Ferma il server gRPC.
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    //Mantiene il server attivo finché non viene chiuso manualmente.
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}