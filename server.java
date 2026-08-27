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

            BufferedReader leitor = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream())
            );

            String requisicao = leitor.readLine();

            System.out.println("Requisição recebida: " + requisicao);

            String[] partes = requisicao.split(" ", 2);

            if (partes.length != 2 || !partes[0].equals("GET")) {
                System.out.println("Requisição inválida.");
                cliente.close();
                serverSocket.close();
                return;
            }

            String nomeArquivo = partes[1];

            File arquivo = new File("arquivos", nomeArquivo);

            if (arquivo.exists() && arquivo.isFile()) {
                System.out.println(
                        "Arquivo encontrado: " + nomeArquivo
                );
            } else {
                System.out.println(
                        "Arquivo não encontrado: " + nomeArquivo
                );
            }

            cliente.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(
                    "Erro no servidor: " + e.getMessage()
            );
        }
    }
}