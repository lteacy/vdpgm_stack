package vdpgm.client;
import com.mathworks.toolbox.javabuilder.*;
import vdpgm.*;
import java.rmi.*;
import java.rmi.registry.*;

public class TestClient
{
    public static void main(String[] args)
    {
        System.out.println("Running the client application!!\n");
        //Registry reg = null;
        Object[] obj = null;
        MatlabWrapperRemote server = null;

        try
        {
            //reg = LocateRegistry.getRegistry(1099);
            server = (MatlabWrapperRemote)Naming.lookup("MatlabIMMServer");

            // setup fake input data
            double[] inData = {0,0, 1,2, -2,0, -1,6, 2,-1, 100,101, 102,100, 101,99};
            
            obj = server.getIMM(3,inData,2.0,8.0);
            //double[] mu = (double[]) obj[0];
            //double[] Sigma = (double[]) obj[1];
            //double[] weights = (double[]) obj[2];
            System.out.println("Received Component Weights: \n" + (obj[2]).toString());
            obj = server.getIMM(3,inData,1.0,16.0);
            System.out.println("Received Component Weights: \n" + (obj[2]).toString());
        }
        catch(RemoteException remote_ex)
        {
            System.out.println("RemoteException being thrown...\n");
            remote_ex.printStackTrace();
        } 
        catch(NotBoundException nb_ex)
        {
            System.out.println("NotBoundException being thrown...\n");
            nb_ex.printStackTrace();
        }
        catch(java.net.MalformedURLException badurl_ex)
        {
            System.out.println("MalformedURLException being thrown...\n");
            badurl_ex.printStackTrace();
        }
        finally
        {
            MWArray.disposeArray(obj);

            try
            {
                if(server!=null)
                    server.dispose();
            }
            catch(RemoteException ex)
            {
                System.out.println("RemoteException being thrown while disposing off the remote stub instance...\n");
                ex.printStackTrace();
            }
        }
    }
}
