package vdpgm.server;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.remoting.*;
import vdpgm.*;
import java.rmi.*;
import java.rmi.registry.*;

public class MatlabIMMServer
{       
    private static Registry reg = null;
    private static MatlabWrapper cls = null;
    private static MatlabWrapperRemote clsRem = null;
    public static void main(String[] args)
    {
        System.out.println("\nPlease wait for the server registration notification.");

        try
        {
            reg = LocateRegistry.getRegistry(1099);
            cls = new MatlabWrapper();
            clsRem =
                 (MatlabWrapperRemote )RemoteProxy.newProxyFor(cls,/*object that handles remote method invocations*/
                                                      MatlabWrapperRemote.class,/*remote interface for the proxy object*/
                                                      false/*flag to decide whether or not MWArray-derived method outputs
                                                             should be converted to their corresponding Java types. Setting
                                                             it to false will retun values as MWArray derived class*/);
            reg.rebind("MatlabIMMServer", clsRem);
            System.out.println("MatlabIMMServer registered and running successfully!!\n");
        }
        //catch(java.net.MalformedURLException badurl_ex)
        //{
            //System.out.println("MalformedURLException being thrown...\n");
            //badurl_ex.printStackTrace();
        //}
        catch(RemoteException remote_ex)
        {
            System.out.println("RemoteException being thrown...\n");
            remote_ex.printStackTrace();
        }
        catch(MWException mw_ex)
        {
            System.out.println("MWException being thrown...\n");
            mw_ex.printStackTrace();
        }
    }
}
