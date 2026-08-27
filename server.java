import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Uso: java Server <IP> <porta>");
            return;
        }

        String ip = args[0];
        int porta = Integer.parseInt(args[1]);

        try {
            InetAddress endereco = InetAddress.getByName(ip);

            ServerSocket serverSocket =
                    new ServerSocket(porta, 50, endereco);

            System.out.println(
                    "Servidor iniciado em " + ip + ":" + porta
            );

            System.out.println("Aguardando cliente...");

            Socket cliente = serverSocket.accept();

            System.out.println(
                    "Cliente conectado: " + cliente.getInetAddress()
            );

        } catch (IOException e) {
            System.out.println(
                    "Erro no servidor: " + e.getMessage()
            );
        }
    }
}