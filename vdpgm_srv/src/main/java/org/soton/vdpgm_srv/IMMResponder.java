package org.soton.vdpgm_srv;

import java.util.List;
import java.util.LinkedList;
import org.ros.message.MessageFactory;
import org.ros.node.service.ServiceResponseBuilder;
import vdpgm_msgs.*;

/**
 * This is a simple {@link ServiceServer} {@link NodeMain}.
 * 
 * @author wtlt@ecs.soton.ac.uk (Luke Teacy)
 */
public class IMMResponder
implements ServiceResponseBuilder<GetModelRequest, GetModelResponse>
{

   private MessageFactory factory_i;

   public IMMResponder(MessageFactory factory)
   {
      factory_i = factory;
   }

   @Override
   public void build
   (
    GetModelRequest request,
    GetModelResponse response
   )
   {
      Gaussian c1 = factory_i.newFromType(Gaussian._TYPE);
      Gaussian c2 = factory_i.newFromType(Gaussian._TYPE);

      double[] m1 = {1,2};
      double[] m2 = {-10,30};
      double[] v1 = {1,0,0,1};
      double[] v2 = {2,1,1,2};

      c1.setMean(m1);
      c1.setCovar(v1);
      c2.setMean(m2);
      c2.setCovar(v2);

      java.util.List<Gaussian> components = new java.util.LinkedList<Gaussian>();
      components.add(c1);
      components.add(c2);
      
      double[] weights = {0.3,0.7};

      GaussianMixture mixture = factory_i.newFromType(GaussianMixture._TYPE);
      mixture.setComponents(components);
      mixture.setWeights(weights);

      response.setModel(mixture);

   }

} // class IMMResponder
