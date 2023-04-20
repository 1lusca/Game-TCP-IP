import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String argv[]) throws Exception {
        
        // verifica se o jogador ja jogou
        boolean[] settings = new boolean[2];
        settings[0] = false;
        settings[1] = false;

        // int[] saldo = new int[1];
        // saldo[0] =  1000;

        BufferedReader readerTeclado = new BufferedReader(new InputStreamReader(System.in)); // le o teclado
        Socket socket = new Socket("127.0.0.1", 6789); // socket do jogador
        DataOutputStream sender = new DataOutputStream(socket.getOutputStream()); // envia pro server
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // recebe do server

        // cria e inicia uma thread apenas para ficar ouvindo o servidor
        // isso permite receber mensagens do server sem precisar fazer um request
        new Thread(() -> {
            try {
                String retornoServidor;
                while ((retornoServidor = reader.readLine()) != null) {
                    System.out.println("Resultado da rodada: " + retornoServidor);

                    // synchronized(saldo) {
                    //     if(retornoServidor.contains("ganhou")) {
                    //         saldo[0] = saldo[0] + Integer.parseInt(retornoServidor.replace("Voce ganhou ", ""));
                    //     }
                    //     System.out.println("Seu saldo de fichas: " + saldo[0]);
                    // }

                    synchronized(settings) {
                        settings[0] = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // envia as mensagens para o server
        while (true) { 
            synchronized(settings) { // garante o controle de concorrencia
                if(settings[0] == false) { // verifica se o jogador ja jogou
                    


                    if(settings[1] == false) {

                        System.out.print("\n----- Nova rodada -----\n");

                        settings[1] = true;

                        System.out.print("Quantas fichas: ");

                        String fichas = readerTeclado.readLine();

                        //saldo[0] = saldo[0] - Integer.parseInt(fichas);

                        sender.writeBytes(fichas +" fichas"+ '\n');

                    } else {

                        settings[1] = false;

                        System.out.print("Qual a sua jogada (numero): ");

                        String jogada = readerTeclado.readLine(); // le a jogada do teclado

                        sender.writeBytes(jogada + '\n'); // envia a jogada para o servidor

                        settings[0] = true; // atualiza o controle de jogadas

                        System.out.print("Aguardando o giro da roleta...\n");
                    }
                }
            }
        }
    }
}
