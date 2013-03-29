package org.soton.vdpgm_srv;

import java.util.List;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.ros.message.MessageFactory;
import vdpgm_msgs.*;

public class InfiniteMixtureModel
{

   /**
    * Apache logger for diagnostics.
    */
   private Log log_i;

   /**
    * MessageFactory.
    */
   private MessageFactory msgFactory_i;

   /**
    * Constructor.
    */
   InfiniteMixtureModel(Log log, MessageFactory factory)
   {
      log_i = log;
      msgFactory_i = factory;
   }

   /**
    * Clear all previously observed data, and reset model to prior.
    */
   synchronized void reset()
   {
      log_i.info("Model has been reset.");
   }

   /**
    * Update model with given data.
    */
   synchronized void observe(vdpgm_msgs.Data data)
   {
      log_i.info("Observed new data.");
   }

   /**
    * Returns expected mixture model, given data observed so far.
    */
   synchronized vdpgm_msgs.GaussianMixture getModel()
   {
      Gaussian c1 = msgFactory_i.newFromType(Gaussian._TYPE);
      Gaussian c2 = msgFactory_i.newFromType(Gaussian._TYPE);

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

      GaussianMixture mixture = msgFactory_i.newFromType(GaussianMixture._TYPE);
      mixture.setComponents(components);
      mixture.setWeights(weights);

      log_i.info("Returning current model state.");

      return mixture;

   } // method getModel

} // class InfinteMixtureModel
