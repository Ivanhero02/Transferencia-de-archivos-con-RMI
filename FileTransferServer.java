import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class FileTransferServer extends UnicastRemoteObject implements FileTransfer {

    private byte[] fileContent; // Arreglo de bytes que representa el archivo completo
    private int numFragmentsReceived; // Contador de fragmentos recibidos

    public FileTransferServer(long fileSize) throws RemoteException {
        super();
        fileContent = new byte[(int) fileSize];
        numFragmentsReceived = 0;
    }

    @Override
    public synchronized void transferFile(byte[] data, String filename, int index, int totalFragments)
            throws RemoteException {
        try {
            int fragmentSize = data.length;
            long byteOffset = (index - 1) * fragmentSize;
            //Imprimir porcentajes
            double percentage = (double) index / (double) totalFragments * 100.0;
            System.out.println("Recibí el fragmento " + index + " de " + totalFragments + " fragmentos totales, para el archivo " + filename + " ("
                    + String.format("%.2f", 100-percentage) + "% faltante)");
            // Copiar el fragmento recibido en su posición correspondiente del arreglo de
            // bytes
            System.arraycopy(data, 0, fileContent, (int) byteOffset, fragmentSize);

            numFragmentsReceived++;

            // Verificar si ya se recibieron todos los fragmentos
            if (numFragmentsReceived == totalFragments) {
                try (FileOutputStream fos = new FileOutputStream(filename)) {
                    fos.write(fileContent);
                    System.out.println("Archivo " + filename + " recibido completamente y guardado.");
                } catch (IOException e) {
                    System.err.println("Error writing file: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error receiving file fragment: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws RemoteException {
        long fileSize = 12345; // Tamaño del archivo en bytes (ejemplo)

        try {
            FileTransferServer server = new FileTransferServer(fileSize);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("FileTransfer", server);
            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
