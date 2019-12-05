package client;

import javafx.scene.canvas.GraphicsContext;
import server.Server;

import java.rmi.*;

public interface Client extends Remote {
     public void takeBackControl(boolean finished) throws  RemoteException;
     Server connectToServer() throws RemoteException;
     void makeMove(Integer x, Integer y) throws RemoteException;
     void setCanvas(GraphicsContext gc)  throws RemoteException;
     public void setWin(boolean win) throws RemoteException;
     boolean youWin() throws RemoteException;
     void youLose() throws RemoteException;
}
