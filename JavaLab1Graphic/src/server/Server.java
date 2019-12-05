package server;

import java.net.MalformedURLException;
import java.rmi.*;

public interface Server  extends Remote{
    public boolean registerClient(Integer clientId ) throws RemoteException, MalformedURLException, NotBoundException;
    public boolean moove(Integer x,Integer y,Integer numClient) throws RemoteException;
    boolean checkDataAndEndGame( int x, int y) throws RemoteException;
}
