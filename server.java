import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Uso: java Server <IP> <porta>");
            return;
        }

        String ip = args[0];
        int porta;

        try {
            porta = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Erro: a porta deve ser um numero inteiro.");
            return;
        }

        try {
            InetAddress endereco = InetAddress.getByName(ip);
            ServerSocket servidor = new ServerSocket(porta, 50, endereco);

            System.out.println(
                    "Servidor TCP em " + ip + ":" + porta);
            System.out.println("Aguardando conexoes...\n");

            while (true) {

                Socket cliente = servidor.accept();

                System.out.println(
                        "Cliente: "
                                + cliente.getInetAddress().getHostAddress()
                                + ":"
                                + cliente.getPort());

                try {
                    BufferedReader leitor = new BufferedReader(
                            new InputStreamReader(
                                    cliente.getInputStream(),
                                    StandardCharsets.UTF_8));

                    OutputStream saida = cliente.getOutputStream();

                    String requisicao = leitor.readLine();

                    if (requisicao == null) {
                        cliente.close();
                        System.out.println("Conexao encerrada.\n");
                        System.out.println("Aguardando conexoes...\n");
                        continue;
                    }

                    System.out.println("> " + requisicao);

                    String[] partes = requisicao.split(" ", 2);

                    if (partes.length != 2
                            || !partes[0].equals("GET")
                            || partes[1].isBlank()) {

                        String resposta = "ERROR INVALID_REQUEST\n";

                        saida.write(
                                resposta.getBytes(StandardCharsets.UTF_8));
                        saida.flush();

                        System.out.println("Requisicao invalida.");

                        cliente.close();

                        System.out.println();
                        System.out.println("Aguardando conexoes...\n");
                        continue;
                    }

                    String nomeArquivo = partes[1];

                    File pasta = new File("arquivos");
                    File arquivo = new File(pasta, nomeArquivo);

                    File pastaCanonica = pasta.getCanonicalFile();
                    File arquivoCanonico = arquivo.getCanonicalFile();

                    if (!arquivoCanonico.toPath().startsWith(
                            pastaCanonica.toPath())) {

                        String resposta = "ERROR INVALID_REQUEST\n";

                        saida.write(
                                resposta.getBytes(StandardCharsets.UTF_8));
                        saida.flush();

                        System.out.println(
                                "Acesso fora da pasta bloqueado.");

                        cliente.close();

                        System.out.println();
                        System.out.println("Aguardando conexoes...\n");
                        continue;
                    }

                    if (!arquivoCanonico.exists()
                            || !arquivoCanonico.isFile()) {

                        String resposta = "ERROR FILE_NOT_FOUND\n";

                        saida.write(
                                resposta.getBytes(StandardCharsets.UTF_8));
                        saida.flush();

                        System.out.println(
                                "Arquivo nao encontrado: "
                                        + nomeArquivo);

                    } else {

                        long tamanho = arquivoCanonico.length();

                        String cabecalho = "OK " + tamanho + "\n";

                        saida.write(
                                cabecalho.getBytes(StandardCharsets.UTF_8));
                        saida.flush();

                        FileInputStream arquivoEntrada = new FileInputStream(arquivoCanonico);

                        byte[] buffer = new byte[8192];
                        int bytesLidos;

                        while ((bytesLidos = arquivoEntrada.read(buffer)) != -1) {

                            saida.write(
                                    buffer,
                                    0,
                                    bytesLidos);
                        }

                        saida.flush();
                        arquivoEntrada.close();

                        System.out.println(
                                "Arquivo enviado: "
                                        + nomeArquivo
                                        + " ("
                                        + tamanho
                                        + " bytes)");
                    }

                } catch (IOException e) {

                    System.out.println(
                            "Erro na comunicacao: "
                                    + e.getMessage());
                }

                cliente.close();

                System.out.println();
                System.out.println("Aguardando conexoes...\n");
            }

        } catch (IOException e) {

            System.out.println(
                    "Erro no servidor: "
                            + e.getMessage());
        }
    }
}