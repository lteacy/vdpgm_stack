package org.soton.vdpgm_srv;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;
import vdpgm_msgs.*;

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
      final Log log = node.getLog();
      Subscriber<DataStamped> subscriber = node.newSubscriber("vdpgm/data", DataStamped._TYPE);
      subscriber.addMessageListener(new MessageListener<DataStamped>()
         {
            @Override
            public void onNewMessage(DataStamped message)
            {
               log.info("I heard: \"" + message.getData() + "\"");
            }
         });

      node.newServiceServer("vdpgm/get_imm", GetModel._TYPE,
            new IMMResponder(node.getTopicMessageFactory()));

      node.newServiceServer("vdpgm/reset", Reset._TYPE,
         new ServiceResponseBuilder<ResetRequest, ResetResponse>()
         {
            @Override
            public void build(ResetRequest request, ResetResponse response)
            {
               // clear all previously received data
               log.info("I've been asked to reset");
            }
         });

   } // method onStart

} // class IMMServer
