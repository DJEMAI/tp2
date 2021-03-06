package tp2_cloud;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
public class CommunicationServeurNoms {
	
		public CommunicationServeurNoms () {
		}
		
		public NomsInterface loadServerStub(String hostname) {
			NomsInterface stub = null;

			try {
				Registry registry = LocateRegistry.getRegistry(hostname);
				stub = (NomsInterface) registry.lookup("serveurNoms");
			} catch (NotBoundException e) {
				System.out.println("Erreur: Le nom '" + e.getMessage()
						+ "' n'est pas défini dans le registre.");
			} catch (AccessException e) {
				System.out.println("Erreur: " + e.getMessage());
			} catch (RemoteException e) {
				System.out.println("Erreur: " + e.getMessage());
			}

			return stub;
		}
	}

