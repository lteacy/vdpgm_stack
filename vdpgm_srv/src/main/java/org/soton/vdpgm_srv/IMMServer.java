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

   private InfiniteMixtureModel model_i;

   @Override
   public GraphName getDefaultNodeName()
   {
      return GraphName.of("vdpgm_srv/imm_server");
   }

   @Override
   public void onStart(ConnectedNode node)
   {
      final Log log = node.getLog();

      model_i = new InfiniteMixtureModel(log,node.getTopicMessageFactory());

      Subscriber<DataStamped> subscriber = node.newSubscriber("vdpgm/data", DataStamped._TYPE);
      subscriber.addMessageListener(new MessageListener<DataStamped>()
         {
            @Override
            public void onNewMessage(DataStamped message)
            {
               log.info("I heard: \"" + message.getData() + "\"");
               model_i.observe(message.getData());
            }
         });

      node.newServiceServer("vdpgm/get_imm", GetModel._TYPE,
         new ServiceResponseBuilder<GetModelRequest, GetModelResponse>()
         {
            @Override
            public void build(GetModelRequest request, GetModelResponse response)
            {
               log.info("I've been asked for the current model");
               response.setModel(model_i.getModel());
            }
         });

      node.newServiceServer("vdpgm/reset", Reset._TYPE,
         new ServiceResponseBuilder<ResetRequest, ResetResponse>()
         {
            @Override
            public void build(ResetRequest request, ResetResponse response)
            {
               log.info("I've been asked to reset");
               model_i.reset();
            }
         });

   } // method onStart

} // class IMMServer
