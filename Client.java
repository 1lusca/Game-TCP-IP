import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String argv[]) throws Exception {
        
        // controle que verifica se o jogador ja jogou
        boolean[] settings = new boolean[2];
        settings[0] = false;
        settings[1] = false;
        
        // le o teclado
        BufferedReader readerTeclado = new BufferedReader(new InputStreamReader(System.in));
        
        // socket de cliente
        Socket socket = new Socket("127.0.0.1", 6789);
        
        // stream que envia para o server
        DataOutputStream sender = new DataOutputStream(socket.getOutputStream()); 
        
        // strea que le o server
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

        // cria e inicia uma thread apenas para ficar ouvindo o servidor
        // isso permite receber mensagens do server sem precisar fazer um request
        new Thread(() -> {
            try {
                
                // le o que o server envia
                String retornoServidor;
                while ((retornoServidor = reader.readLine()) != null) {
                    System.out.println("Resultado da rodada: " + retornoServidor);
                    synchronized(settings) {
                        
                        // reseta para uma nova rodada
                        settings[0] = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // processo principal que  envia as mensagens para o server
        while (true) { 
           
            synchronized(settings) {
                
                // verifica se o jogador ja jogou
                if(settings[0] == false) {
                    
                    // inicia a aposta e envia a posta de fichas
                    if(settings[1] == false) {

                        System.out.print("\n----- Nova rodada -----\n");

                        // atualiza o controle de jogadas
                        settings[1] = true;

                        System.out.print("Quantas fichas: ");

                        String fichas = readerTeclado.readLine();

                        // envia a quantidade de fichas apostadas
                        sender.writeBytes(fichas +" fichas"+ '\n');

                    } else { // envia a aposta (numero)

                        settings[1] = false;

                        System.out.print("Qual a sua jogada (numero): ");

                        String jogada = readerTeclado.readLine();

                        // envia a jogada 
                        sender.writeBytes(jogada + '\n');

                        // atualiza o controle de jogadas
                        settings[0] = true;

                        System.out.print("Aguardando o giro da roleta...\n");
                        
                    }
                }
            }
        }
    }
}
