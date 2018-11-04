
package ca.polymtl.inf8480.tp2.ServeurNoms;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServeurNoms implements NomsInterface{
	
	private String repartiteurUsername=null;
	private String repartiteurPassword=null;
	private String[] ipServeurs=null;
	private int[] capaciteServeurs=null;

	public static void main(String[] args) {
        ServeurNoms serveurNoms = new ServeurNoms();
        serveurNoms.run();
    }

    public ServeurNoms() {
        super();
    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServeurNoms stub = (ServeurNoms) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("serveurNoms", stub);
            System.out.println("Serveur de noms est pret.");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
	@Override
	public void ajouterRepartiteur(String username, String password) throws RemoteException {
		// TODO Auto-generated method stub
		repartiteurUsername=username;
		repartiteurPassword=password;
		
	}

	@Override
	public int getNombreServeurs() throws RemoteException {
		// TODO Auto-generated method stub
		return ipServeurs.length;
	}

	@Override
	public String[] getIpServeurs() throws RemoteException {
		// TODO Auto-generated method stub
		return ipServeurs;
	}

	@Override
	public int[] getCapaciteServeurs() throws RemoteException {
		// TODO Auto-generated method stub
		return capaciteServeurs;
	}

	@Override
	public void removeServeur(String IP) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ajouterServeur(String IP, int capacite) throws RemoteException {
		// TODO Auto-generated method stub
		ipServeurs[ipServeurs.length]=IP;
		capaciteServeurs[capaciteServeurs.length]=capacite;
		
	}

}
