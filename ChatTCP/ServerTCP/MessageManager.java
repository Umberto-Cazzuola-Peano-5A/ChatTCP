/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverTCP;

import java.util.ArrayList;

/**
 *
 * @author Umberto Cazzuola
 */
//L'ultimo messaggio ricevuto e' la risorsa comune condivisa tra i vari Threads
//Con questa Classe ricevo l'ultimo messaggio inviato dai Clients
//e richiedo l'invio a tutti i workers di inviare il messaggio al proprio client
class MessageManager {

    //ultimo messaggio inviato dai Clients
    private String messaggio;
    //lista dei workers che vengono creati, uno per ogni Client connesso
    private ArrayList<SocketWorker> workers = new ArrayList<>();
    
    //aggiungo il client alla lista
    void addClient(SocketWorker worker) {
        this.workers.add(worker);
    }
    
    //rimuovo il client dalla lista
    void removeClient(SocketWorker worker) {
        this.workers.remove(worker);
    }
    
    //chiamata dai vari workers quando ricevono un messaggio dal proprio client.
    //questo metodo e' sycronized per evitare conflitti tra workers
    //che desiderano accedere alla stessa risorsa (cioe' nel caso in cui
    // vengono ricevuti simultaneamente i messaggi da piu' clients)
    synchronized void sendNewMessaggio(String m) {
        //aggiorna l'ultimo messaggio nella variabile dell'oggetto
        this.messaggio = m;
        //chiedi ad ogni worker di inviare il messaggio ricevuto
        for (SocketWorker worker: this.workers) {
            worker.sendMessaggio(this.messaggio);
        }
    }
    
}

//questa interfaccia deve essere implementata da tutti i threads che vogliono
//inviare il nuovo messaggio
interface InviaMessaggio {
    //questo metodo conterra' il codice da eseguire da ogni worker per inviare
    //il messaggio al proprio client
    public void sendMessaggio(String m);
}

//questa classe astratta deve essere estesa da tutti i threads che vogliono
//notificare la ricezione di un messaggio dal client per poi poterlo inviare 
//a tutti i clients  tramite i relativi workers
//NOTA: ho dichiarato la classe "abstract" in modo da indicare che puo' essere
//solo estesa e non avrebbe senso creare un oggetto direttamente da essa.
interface RiceviMessaggio { 

    //L'evento viene generato/pubblicato chiamando il suguente metodo          
    public void messaggioReceived(String m);

}