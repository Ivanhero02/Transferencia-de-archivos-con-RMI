import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileTransfer extends Remote {
    public void transferFile(byte[] data,String fileName, int index, int totalFragments) throws RemoteException;
}
