package ca.polymtl.inf8480.tp2.ServeurCalcul;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
public class CalculationServer implements ServerInterface{
    private int capacity;
    private String mode;
    private int errorRate;
    private NomsInterface distantServerStub = null;
    public static void main(String[] args) {
    	
    	CalculationServer calculationServer = new CalculationServer(Integer.parseInt(args[1]), args[2],args[0], Integer.parseInt(args[3]), args[4]);
        calculationServer.run();
    }


    @Override
    public boolean isAuthenticated(String username, String password){

        return true;
    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServerInterface stub = (ServerInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("server", stub);
            System.out.println("Server ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lance ?");
            System.err.println();
            System.err.println("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    public CalculationServer(int capacity, String ipServeurCalcul, String mode, int errorRate, String ipServeurNoms){
    	
    	super();
    	
    	 distantServerStub = loadServerStub(ipServeurNoms);
    	 try {

    		 distantServerStub.ajouterServeur(ipServeurCalcul, capacity); 
    	 }catch(Exception e) {
    	 }
        this.capacity = capacity;
        this.mode = mode;
        this.errorRate = errorRate;
        
    }

    @Override
    public boolean acceptRequest(int nOperations) throws RemoteException{
        int refusalRate = calculateRefusalRate(nOperations);
        int generatedRefusalRate = generateRandomRate();
        return generatedRefusalRate >= refusalRate;
    }


    private int calculateRefusalRate(int nOperations) throws RemoteException{
        return (nOperations - capacity)*100 / (4 * capacity);
    }

    private int generateRandomRate() {
        Random random = new Random(Constants.MAX_REFUSAL_RATE);
        return random.nextInt();
    }

    

    private boolean isMaliciousServer() {
        return (!mode.equals(Constants.MODE_SECURISE)) && computeMaliciousness();
    }

    private boolean computeMaliciousness(){
        int generatedErrorRate = generateRandomRate();
        return generatedErrorRate >= errorRate;
    }


    public int getCapacity() throws RemoteException{
        return capacity;
    }

    public void setCapacity(int capacity) throws RemoteException{
        this.capacity = capacity;
    }


    public String getMode() throws RemoteException{
        return mode;
    }

    public void setMode(String mode) throws RemoteException{
        this.mode = mode;
    }

    public int getErrorRate() throws RemoteException{
        return errorRate;
    }

    public void setErroneousAnswerRate(int errorRate) throws RemoteException{
        this.errorRate = errorRate;
    }

	@Override
	public int calculate(Map<String, String> operations) throws RemoteException {
		if(isMaliciousServer())
            return -1;
        int result = 0;
        Iterator it = operations.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            if(pair.getKey().equals(Constants.PELL)){
                result += Operations.pell(Integer.parseInt((String) pair.getValue())) % Constants.MOD_VALUE;
            }
            else{
                result += Operations.prime(Integer.parseInt((String) pair.getValue())) % Constants.MOD_VALUE;
            }
        }
        return result;
	}
	
	private NomsInterface loadServerStub(String hostname) {
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
