package org.soton.vdpgm_srv;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;

/**
 * This is a simple {@link ServiceServer} {@link NodeMain}.
 * 
 * @author wtlt@ecs.soton.ac.uk (Luke Teacy)
 */
public class IMMResponder
implements ServiceResponseBuilder<vdpgm_msgs.AddTwoIntsRequest, vdpgm_msgs.AddTwoIntsResponse>
{

   @Override
   public void build
   (
    vdpgm_msgs.AddTwoIntsRequest request,
    vdpgm_msgs.AddTwoIntsResponse response
   )
   {
      response.setSum(request.getA() + request.getB());
   }

} // class IMMResponder
