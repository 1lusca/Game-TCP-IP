import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Server {
    public static void main(String argv[]) throws Exception {
        
        ServerSocket socket = new ServerSocket(6789); // cria um socket que escuta por novas conexoes
        
        List<Socket> conexoes = new ArrayList<>(); // lista que vai armazenas as conexoes

        int[] settings = new int[2];
        settings[0] = 0; //numero de conexoes
        settings[1] = 0; //jogadas feitas

        int[] jogadas = new int[2]; // armazena as jogadas 
  
        while (true) {

            Socket socketConexao = socket.accept(); // cria um socket toda vez que uma nova conexoes e feita
            
            conexoes.add(socketConexao); // adiciona esse socket de uma nova conexao em uma lista

            settings[0] = settings[0] + 1; // incrementa o numero de conexoes
            
            // cria uma nova thread para cada jogador
            // isso vai garantir que cada conexao tenha uma thread para si propria, garantindo multiplas conexoes
            new Thread(() -> {
                try {
                    
                    BufferedReader doCliente = new BufferedReader(new InputStreamReader(socketConexao.getInputStream())); // stream que le a jogada recebida

                    while (true) { // loop que fica ouvindo novas jogadas

                        String jogada = doCliente.readLine(); // le a jogada do jogador
                        
                        synchronized(settings) { // garante o controle de concorrencia entre threads (todo synchronized faz isso com variaves globais) 
                            
                            synchronized(jogadas) {
                                jogadas[settings[0] - 1] = Integer.parseInt(jogada); // armazena a jogada no array de jogadas
                            }

                            settings[1] = settings[1] + 1; // incrementa o numero de jogadas feitas
                           
                            if(settings[0] == settings[1]) { // verifica se todos os jogadores ja jogaram
                                
                                // gira a roleta
                                Random random = new Random();
                                int numeroRoleta = random.nextInt(36) + 1;

                                synchronized (conexoes) {
                                    
                                    int aux = 0;
                                    
                                    for (Socket conexao : conexoes) { // varre a lista de conexoes para verificar se os jogadores ganharam e enviar o resultado
                                        
                                        String resultado;
                                        
                                        // verifica se o jogador atual ganhou
                                        if(numeroRoleta == jogadas[aux]){
                                            resultado = "Voce ganhou!\n";
                                        } else {
                                            resultado = "Voce perdeu :c | Numero ganhador: "+numeroRoleta+"\n";
                                        }
                                        
                                        // reseta as jogadas para uma nova rodada
                                        settings[1] = 0;
                                        jogadas[0] = 0;
                                        jogadas[1] = 0;

                                        // envia o resultado da rodada para os jogadores
                                        DataOutputStream sender = new DataOutputStream(conexao.getOutputStream());
                                        sender.writeBytes(resultado);
                                    }
                                }
                            }

                        }

                            
                        
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
