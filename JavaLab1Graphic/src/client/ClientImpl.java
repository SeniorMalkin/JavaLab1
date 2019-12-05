package client;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.Server;

public class ClientImpl extends UnicastRemoteObject implements Client {
    private GraphicsContext graphicContext;
    private String numberClient;

    public void setWin(boolean win) throws RemoteException {
        System.out.println("Client number  " + numberClient + "  win");
        this.win = win;
    }

    boolean win = false;

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean isBlock() {
        return block;
    }

    private boolean block = false;

    public void setNumberClient(String numberClient) {
        this.numberClient = numberClient;
    }

    public ClientImpl( ) throws RemoteException {
    }

    public void takeBackControl(boolean finished) throws RemoteException {

    }

     public Server connectToServer() throws RemoteException {
         try {
             String RMI_HOSTNAME = "java.rmi.server.hostname";
             String localhost = "127.0.0.1";
             System.setProperty(RMI_HOSTNAME, localhost);
             String objectName = "rmi://localhost/GameServer";
             ;

             Server bs;
             bs = (Server) Naming.lookup(objectName);
             return bs;

         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (RemoteException e) {
             e.printStackTrace();
         } catch (NotBoundException e) {
             System.err.println("NotBoundException : " +
                     e.getMessage());
         }
         return null;
     }

    @Override
    public void makeMove(Integer x, Integer y) throws RemoteException {
        if(Integer.valueOf(numberClient) == 1) {
            graphicContext.setFill(Color.BLACK);
        }else{
            graphicContext.setFill(Color.GRAY);
        }

            graphicContext.fillOval(x, y, 25, 25);
            block = false;

        if(Integer.valueOf(numberClient) == 1) {

            graphicContext.setFill(Color.GRAY);
        }else {
            graphicContext.setFill(Color.BLACK);
        }
    }

    @Override
    public void setCanvas(GraphicsContext gc) {
        graphicContext = gc;
    }

    @Override
    public boolean youWin() throws RemoteException {
        return win;
    }

    @Override
    public void youLose() throws RemoteException {

    }

    public void makeMove() throws RemoteException {
        System.out.println("Hello, it  " + numberClient + "  client!");
    }

    /*public static void main(String[] args) {
        String localhost    = "127.0.0.1";
        String RMI_HOSTNAME = "java.rmi.server.clientname";
        try {
            System.setProperty(RMI_HOSTNAME, localhost);

            ClientImpl client = new ClientImpl();
            String clientName = "Client" + args[0];
            client.setNumberClient(args[0]);
            System.out.println("Initializing " + clientName);

            //Registry registry = LocateRegistry.createRegistry(1099);
            Naming.rebind(clientName, client);
            System.out.println("\nConnect with server ...");
            Server server = client.connectToServer();
            if(server == null){
                System.out.println("\n[ERROR] Error to connect with server. Please, connect with administrator...");
                return;
            }
            System.out.println("\nRegister client ...");
            boolean registered = server.registerClient(Integer.valueOf(args[0]));
            if(registered){
                System.out.println("\nRegistration is successful");
            }else{
                System.out.println("\nRegistration is failed");
            }

            System.out.println("Start " + clientName);
        } catch (RemoteException e) {
            System.err.println("RemoteException : "+e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
            System.exit(2);
        }
    }*/
}
