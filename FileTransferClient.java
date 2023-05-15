import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;

public class FileTransferClient {
    public static void main(String[] args) throws RemoteException, NotBoundException, IOException {
        String filename = "archivotransferir.txt";
        File file = new File(filename);
        long fileSize = file.length();
        long chunkSize = fileSize / 10;
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        FileTransfer stub = (FileTransfer) registry.lookup("FileTransfer");
        
        ArrayList<Integer> fragmentOrder = new ArrayList<Integer>();
        for (int i = 1; i <= fileSize; i++) {
            fragmentOrder.add(i);
        }
        Collections.shuffle(fragmentOrder); // Mezcla el orden de los fragmentos
        

        for (int i = 0; i < fragmentOrder.size(); i++) {
            int fragmentIndex = fragmentOrder.get(i);
            byte[] buffer = new byte[(int) chunkSize];
            long byteOffset = (fragmentIndex - 1) * chunkSize;
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(byteOffset);
                int bytesRead = raf.read(buffer);
                if (bytesRead != -1) {
                    FragmentHeader header = new FragmentHeader(fragmentIndex, fileSize);
                    stub.transferFile(buffer, file.getName(), header.getIndex(), (int) header.getSize());
                }
            }
        }
    }
}