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
public class IMMServer extends AbstractNodeMain
{

   @Override
   public GraphName getDefaultNodeName()
   {
      return GraphName.of("vdpgm_srv/imm_server");
   }

   @Override
   public void onStart(ConnectedNode node)
   {
      node.newServiceServer("vdpgm/get_imm", vdpgm_msgs.GetModel._TYPE, new IMMResponder(node.getTopicMessageFactory()));

   } // method onStart

} // class IMMServer
