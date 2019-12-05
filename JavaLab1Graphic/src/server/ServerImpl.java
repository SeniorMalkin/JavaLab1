package server;

import client.Client;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements Server {
    static private String localhost    = "127.0.0.1";
    private Client client1;
    private Client client2;
    private Integer[][] gameField;

    protected ServerImpl() throws RemoteException {
        gameField = new Integer[19][19];
        for(int i=0; i<19 ;i++){
            for(int j=0; j<19; j++){
                gameField[i][j] = 0;
            }
        }

    }


    public boolean registerClient(Integer clientId)  {
        String RMI_CLIENTNAME = "java.rmi.server.clientname";
        System.setProperty(RMI_CLIENTNAME, localhost);
        String objectName;
        try{
                if(clientId == 1){
                    objectName = "rmi://localhost/Client1";
                    client1 = (Client) Naming.lookup(objectName);
                    return true;
                }else{
                    if(clientId == 2) {
                        objectName = "rmi://localhost/Client2";
                        client2 = (Client) Naming.lookup(objectName);
                        return true;
                    }
                }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println("NotBoundException : " + e.getMessage());
        }

        return false;
    };

    @Override
    public boolean moove(Integer x, Integer y, Integer numClient) {
        int index_x = (x-100)/30;
        int index_y = (y-60)/30;
        try {
            if(!(index_x >=0 && index_x<19 && index_y >=0 && index_y<19)){
                throw new RemoteException("Invalid index: x" + index_x +"  y  " + y);
            }
           if(!controlMove(index_x,index_y)){
               return false;
           }
            System.out.println("Moove: x "+ index_x + "  y  " + index_y);
            if (numClient == 1) {
                gameField[index_x][index_y] = 1;
                client2.makeMove(x, y);
                if(checkDataAndEndGame(index_x,index_y)){
                    client1.setWin(true);
                    client2.youLose();
                }
                return true;
            }else{
                if (numClient == 2) {
                    gameField[index_x][index_y] = 2;
                    client1.makeMove(x, y);
                    if(checkDataAndEndGame(index_x,index_y)){
                        client2.setWin(true);
                        client1.youLose();
                    }
                    return true;
                }
            }
        }catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkDataAndEndGame(int x, int y) throws RemoteException {
        int value = gameField[x][y];
        int counter = 1;
        int i=x,j=y;
        /***
         * 1
         * 1
         * 0
         * 1
         ***/
        while(j+1<19 && gameField[i][j+1] == value){
            counter++;
            j++;
        }
        j=y;
        while(j-1>=0 && gameField[i][j-1] == value){
            counter++;
            j--;
        }
        if(counter == 6){
            return true;
        }
        /********
         * 111011
         ********/
        i=x;j=y;counter=1;
        while(i+1<19 && gameField[i+1][j] == value){
            counter++;
            i++;
        }
        i=x;
        while(i-1>=0 && gameField[i-1][j] == value){
            counter++;
            i--;
        }
        if(counter == 6){
            return true;
        }
        /***********
         *       1
         *     0
         *   1
         * 1
         ***********/
        i=x;j=y;counter=1;
        while(i+1<19 && j+1<19 && gameField[i+1][j+1] == value){
            counter++;
            i++;j++;
        }
        i=x;j=y;
        while(i-1>=0 && j-1>=0 && gameField[i-1][j-1] == value){
            counter++;
            i--;j--;
        }
        if(counter == 6){
            return true;
        }
        /*********
         * 1
         *   1
         *     0
         *       1
         *********/
        i=x;j=y;counter=1;
        while(i-1>=0 && j+1<19 && gameField[i-1][j+1] == value){
            counter++;
            i--;j++;
        }
        i=x;j=y;
        while(i+1<19 && j-1>=0 && gameField[i+1][j-1] == value){
            counter++;
            i++;j--;
        }
        if(counter == 6){
            return true;
        }
        return false;
    }

    private boolean controlMove(int x,int y){
        System.out.println("X:  " + x + "   Y: " + y);
       int value = gameField[x][y];
       if(value != 0){
           return false;
       }
       return true;
    }

    public static void main(String[] args) {
        String RMI_HOSTNAME = "java.rmi.server.hostname";
        try {
            System.setProperty(RMI_HOSTNAME, localhost);

            ServerImpl server = new ServerImpl();
            String serviceName = "GameServer";
            System.out.println("Initializing " + serviceName);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(serviceName, server);
            System.out.println("Start " + serviceName);
        } catch (RemoteException e) {
            System.err.println("RemoteException : "+e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
            System.exit(2);
        }
    }

}
