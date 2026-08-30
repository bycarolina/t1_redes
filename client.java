import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println(
                    "Uso: java Client <IP> <porta> <arquivo>");
            return;
        }

        String ip = args[0];
        int porta;
        String nomeArquivo = args[2];

        try {
            porta = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Erro: a porta deve ser um numero inteiro.");
            return;
        }

        try {
            Socket socket = new Socket(ip, porta);

            InputStream entrada = socket.getInputStream();
            OutputStream saida = socket.getOutputStream();

            String requisicao = "GET " + nomeArquivo + "\n";

            System.out.println(
                    "Conectado a " + ip + ":" + porta);
            System.out.println("> GET " + nomeArquivo);
            System.out.println();

            saida.write(
                    requisicao.getBytes(StandardCharsets.UTF_8));
            saida.flush();

            ByteArrayOutputStream cabecalhoBytes = new ByteArrayOutputStream();

            int byteRecebido;

            while ((byteRecebido = entrada.read()) != -1) {

                if (byteRecebido == '\n') {
                    break;
                }

                cabecalhoBytes.write(byteRecebido);
            }

            String cabecalho = cabecalhoBytes.toString(StandardCharsets.UTF_8);

            if (cabecalho.startsWith("ERROR")) {

                if (cabecalho.equals(
                        "ERROR FILE_NOT_FOUND")) {

                    System.out.println(
                            "Arquivo nao encontrado.");

                } else if (cabecalho.equals(
                        "ERROR INVALID_REQUEST")) {

                    System.out.println(
                            "Requisicao invalida.");

                } else {

                    System.out.println(
                            "Erro do servidor: "
                                    + cabecalho);
                }

                socket.close();
                return;
            }

            if (!cabecalho.startsWith("OK ")) {

                System.out.println(
                        "Resposta invalida do servidor.");

                socket.close();
                return;
            }

            long tamanhoArquivo;

            try {
                tamanhoArquivo = Long.parseLong(
                        cabecalho.substring(3));

            } catch (NumberFormatException e) {

                System.out.println(
                        "Tamanho de arquivo invalido.");

                socket.close();
                return;
            }

            File pasta = new File("downloads");

            if (!pasta.exists()) {
                pasta.mkdir();
            }

            File arquivoRecebido = new File(pasta, nomeArquivo);

            File pastaPai = arquivoRecebido.getParentFile();

            if (pastaPai != null
                    && !pastaPai.exists()) {

                pastaPai.mkdirs();
            }

            FileOutputStream arquivoSaida = new FileOutputStream(arquivoRecebido);

            byte[] buffer = new byte[8192];

            long totalRecebido = 0;
            int numeroLeituras = 0;

            while (totalRecebido < tamanhoArquivo) {

                int quantidade = (int) Math.min(
                        buffer.length,
                        tamanhoArquivo - totalRecebido);

                int bytesLidos = entrada.read(
                        buffer,
                        0,
                        quantidade);

                if (bytesLidos == -1) {
                    break;
                }

                numeroLeituras++;

                arquivoSaida.write(
                        buffer,
                        0,
                        bytesLidos);

                totalRecebido += bytesLidos;
            }

            arquivoSaida.close();
            socket.close();

            if (totalRecebido == tamanhoArquivo) {

                System.out.println("Arquivo recebido.");
                System.out.println(
                        "  Nome:     " + nomeArquivo);
                System.out.println(
                        "  Tamanho:  " + totalRecebido + " bytes");
                System.out.println(
                        "  Destino:  " + arquivoRecebido.getPath());
                System.out.println(
                        "  Leituras: " + numeroLeituras);

            } else {

                System.out.println("Transferencia incompleta.");
                System.out.println(
                        "  Esperado: " + tamanhoArquivo + " bytes");
                System.out.println(
                        "  Recebido: " + totalRecebido + " bytes");
            }

        } catch (UnknownHostException e) {

            System.out.println(
                    "Servidor nao encontrado: " + ip);

        } catch (ConnectException e) {

            System.out.println(
                    "Nao foi possivel conectar a "
                            + ip + ":" + porta);

        } catch (IOException e) {

            System.out.println(
                    "Erro no cliente: "
                            + e.getMessage());
        }
    }
}