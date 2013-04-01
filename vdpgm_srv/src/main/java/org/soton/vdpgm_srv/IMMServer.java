package org.soton.vdpgm_srv;

import java.io.StringWriter;
import java.io.PrintWriter;
import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.exception.ServiceException;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;
import org.ros.node.parameter.ParameterTree;
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

      try
      {
         ParameterTree params = node.getParameterTree();
         int nDims = params.getInteger(node.getName().toString() + "/dims",2);
         log.info("Number of dimensions set to " + nDims);
         model_i = new InfiniteMixtureModel(nDims,log,node.getTopicMessageFactory());
      }
      catch(Exception e)
      {
         java.io.StringWriter errors = new StringWriter();
         e.printStackTrace(new PrintWriter(errors));
         log.error("Exception thrown while constructing matlab model:\n" + errors.toString());
         onError(node,e);
      }

      Subscriber<DataStamped> subscriber = node.newSubscriber("vdpgm/data", DataStamped._TYPE);
      subscriber.addMessageListener(new MessageListener<DataStamped>()
         {
            @Override
            public void onNewMessage(DataStamped message)
            {
               log.info("I heard: \"" + message.getData() + "\"");
               try
               {
                  model_i.observe(message.getData());
               }
               catch(Exception e)
               {
                  java.io.StringWriter errors = new StringWriter();
                  e.printStackTrace(new PrintWriter(errors));
                  log.error("Exception thrown while observing data:\n" + errors.toString());
                  log.error("Failed to observe data due to previous error.");
               }
            }
         });

      node.newServiceServer("vdpgm/get_imm", GetModel._TYPE,
         new ServiceResponseBuilder<GetModelRequest, GetModelResponse>()
         {
            @Override
            public void build(GetModelRequest request, GetModelResponse response) throws ServiceException
            {
               log.info("I've been asked for the current model");
               try
               {
                  response.setModel(model_i.getModel());
               }
               catch(Exception e)
               {
                  java.io.StringWriter errors = new StringWriter();
                  e.printStackTrace(new PrintWriter(errors));
                  log.error("Exception thrown while servicing model request:\n" + errors.toString());
                  throw new org.ros.exception.ServiceException(e);
               }
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
