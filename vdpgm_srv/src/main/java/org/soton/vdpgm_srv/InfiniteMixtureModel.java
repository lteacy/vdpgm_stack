package org.soton.vdpgm_srv;

import org.apache.commons.logging.Log;
import vdpgm_msgs.*;

public class InfiniteMixtureModel
{

   /**
    * Apache logger for diagnostics.
    */
   private Log log_i;

   /**
    * Constructor.
    */
   InfiniteMixtureModel(Log log)
   {
      log_i = log;
   }

   /**
    * Clear all previously observed data, and reset model to prior.
    */
   void reset()
   {
   }

   /**
    * Update model with given data.
    */
   void observe(vdpgm_msgs.Data data)
   {
   }

} // class InfinteMixtureModel
