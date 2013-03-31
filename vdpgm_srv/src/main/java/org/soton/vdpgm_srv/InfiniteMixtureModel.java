package org.soton.vdpgm_srv;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.ros.message.MessageFactory;
import vdpgm_msgs.*;

import com.mathworks.toolbox.javabuilder.*;
import vdpgm.*;

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
    * Handle to Matlab.
    */
   private MatlabWrapper matlab_i;

   /**
    * Array observed data.
    */
   ArrayList<Double> dataList_i;

   /**
    * Number of dimensions in observed data.
    */
   int nDims_i;

   /**
    * Number of observed data.
    */
   int nPoints_i;

   /**
    * Constructor.
    */
   public InfiniteMixtureModel(Log log, MessageFactory factory) throws MWException
   {
      log_i = log;
      msgFactory_i = factory;
      log_i.info("Setting up Matlab");
      matlab_i = new MatlabWrapper();
      nDims_i = 2;
      nPoints_i = 0;
      dataList_i = new ArrayList<Double>(2000);
      double[] inData = {0,0, 1,2, -2,0, -1,6, 2,-1, 100,101, 102,100, 101,99};
      log_i.info("Observing Initial data");
      observe(inData);
   }

   /**
    * Clear all previously observed data, and reset model to prior.
    */
   public synchronized void reset()
   {
      dataList_i.clear();
      nPoints_i = 0;
      log_i.info("Model has been reset.");
   }

   /**
    * Update model with given data in ROS Data message.
    */
   public synchronized void observe(vdpgm_msgs.Data data)
   {
      log_i.info("Observed new data.");
      double[] values = data.getVals();

      if(data.getNDims()!=nDims_i)
      {
         //TODO throw exception
      }

      if(values.length != nDims_i*data.getNPoints())
      {
         //TODO throw exception
      }

      observe(values);

   } // method observe 

   /**
    * Update model with given data in primitive array.
    */
   public synchronized void observe(double[] data)
   {
      //TODO throw exception if data length is not divisable by nDims_i
      for(double datum : data)
      {
         dataList_i.add(datum);
      }
      nPoints_i = dataList_i.size() / nDims_i;

   } // method observe

   /**
    * Returns expected mixture model, given data observed so far.
    */
   public synchronized vdpgm_msgs.GaussianMixture getModel() throws MWException
   {
      Object[] results = null;
      try
      {
         //*********************************************************************
         // Try to call matlab to get model
         //*********************************************************************
         log_i.info("matlab: " + matlab_i);
         log_i.info("data  : " + dataList_i);
         log_i.info("array : " + dataList_i.toArray());
         results = matlab_i.getIMM(3,dataList_i.toArray(),nDims_i,nPoints_i);

         //*********************************************************************
         // Extract data from matlab result
         //*********************************************************************
         double[] means   = ((MWNumericArray) results[0]).getDoubleData();
         double[] covar   = ((MWNumericArray) results[1]).getDoubleData();
         double[] weights = ((MWNumericArray) results[2]).getDoubleData();

         //*********************************************************************
         // Check that number of components and dimensions are consistent
         //*********************************************************************
         int nComponents = weights.length;
         int nDims = means.length / nComponents;
         
         if(means.length != nComponents*nDims)
         {
            // TODO THROW SOME ERROR
         }

         if(covar.length != nComponents*nComponents*nDims)
         {
            // TODO THROW SOME ERROR
         }

         //*********************************************************************
         // Copy component parameters into list of ROS Gaussian messages
         //*********************************************************************
         List<Gaussian> components = new ArrayList<Gaussian>(nComponents);
         for(int k=0; k<nComponents; ++k)
         {
            //******************************************************************
            // Make ROS Gaussian object for the current component
            //******************************************************************
            Gaussian curGaussian = msgFactory_i.newFromType(Gaussian._TYPE);

            //******************************************************************
            // Extract parameters
            //******************************************************************
            int meanStart = k*nDims;
            int meanEnd = (k+1)*nDims-1;
            double[] curMean = Arrays.copyOfRange(means,meanStart,meanEnd);
            
            int varStart = k*nDims*nDims;
            int varEnd   = (k+1)*nDims*nDims-1;
            double[] curVar  = Arrays.copyOfRange(covar,varStart,varEnd);

            //******************************************************************
            // Add parameters and stick component into the list
            //******************************************************************
            curGaussian.setMean(curMean);
            curGaussian.setCovar(curVar);
            components.add(curGaussian);

         } // for loop

         //*********************************************************************
         // Create mixture from components and weights
         //*********************************************************************
         GaussianMixture mixture =
            msgFactory_i.newFromType(GaussianMixture._TYPE);

         mixture.setComponents(components);
         mixture.setWeights(weights);
         log_i.info("Returning current model state.");
         return mixture;

      }
      //************************************************************************
      // Deal with matlab errors
      //************************************************************************
      catch(MWException e)
      {
         log_i.error("Caught Exception in InfiniteMixtureModel.observe(): "
               + e.toString());

         throw e;
      }
      //************************************************************************
      // Clean up matlab native resources 
      //************************************************************************
      finally
      {
         if(null!=results)
         {
            MWArray.disposeArray(results);
         }
      }

   } // method getModel

   /**
    * Release native resources.
    */
   protected void finalize() throws Throwable
   {
      if(matlab_i!=null)
      {
         matlab_i.dispose();
      }
      super.finalize();
   }

} // class InfinteMixtureModel

