package tp2_cloud;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NomsInterface extends Remote {
	void ajouterRepartiteur(String username, String password) throws RemoteException;
	int getNombreServeurs() throws RemoteException;
	String[] getIpServeurs() throws RemoteException;
	int[] getCapaciteServeurs() throws RemoteException;
	void removeServeur(String IP) throws RemoteException;
	void ajouterServeur(String IP, int capacite) throws RemoteException;
}
