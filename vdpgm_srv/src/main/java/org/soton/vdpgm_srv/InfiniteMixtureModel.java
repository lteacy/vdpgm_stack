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
   public InfiniteMixtureModel(Log log, MessageFactory factory) throws IMMException
   {
      try
      {
         log_i = log;
         msgFactory_i = factory;
         log_i.info("Setting up Matlab");
         matlab_i = new MatlabWrapper();
         nDims_i = 2;
         nPoints_i = 0;
         dataList_i = new ArrayList<Double>(2000);
      }
      catch(MWException e)
      {
         throw new IMMException("Error encountered during model construction",e);
      }
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
   public synchronized void observe(vdpgm_msgs.Data data) throws IMMException
   {
      log_i.info("Observed new data.");
      double[] values = data.getVals();

      if(data.getNDims()!=nDims_i)
      {
         throw new IMMException("Trying to observe data of dimension " +
               data.getNDims() + " for model of dimension " + nDims_i);
      }

      if(values.length != nDims_i*data.getNPoints())
      {
         throw new IMMException("Observed data vector as inconsistent length: " +
               values.length + "!=" + nDims_i + "x" + data.getNPoints());
      }

      observe(values);

   } // method observe 

   /**
    * Update model with given data in primitive array.
    */
   public synchronized void observe(double[] data) throws IMMException
   {
     if(0!=(data.length % nDims_i))
     {
        throw new IMMException("Observed data vector length not divisible by "
              + "number of dimensions!");
     }

      for(double datum : data)
      {
         dataList_i.add(datum);
      }
      nPoints_i = dataList_i.size() / nDims_i;

   } // method observe

   /**
    * Returns expected mixture model, given data observed so far.
    */
   public synchronized vdpgm_msgs.GaussianMixture getModel() throws IMMException
   {
      Object[] results = null;
      try
      {
         //*********************************************************************
         // Currently our matlab code only works if we have at least two
         // observations
         //*********************************************************************
         if(2>nPoints_i)
         {
            throw new IMMException("Need at least 2 data points to estimate" +
                  " model, but got only " + nPoints_i);
         }

         //*********************************************************************
         // Try to call matlab to get model
         //*********************************************************************
         log_i.info("Getting Model from Matlab...");
         log_i.debug("matlab: " + matlab_i);
         log_i.debug("data  : " + dataList_i);
         results = matlab_i.getIMM(3,dataList_i.toArray(),nDims_i,nPoints_i);

         //*********************************************************************
         // Extract data from matlab result
         //*********************************************************************
         double[] means   = ((MWNumericArray) results[0]).getDoubleData();
         double[] covar   = ((MWNumericArray) results[1]).getDoubleData();
         double[] weights = ((MWNumericArray) results[2]).getDoubleData();

         log_i.debug("GOT...");
         log_i.debug("means:\n" + results[0]);
         log_i.debug("covar:\n" + results[1]);
         log_i.debug("weights:\n" + results[2]);
         log_i.debug("means length:\n" + means.length);
         log_i.debug("covar length:\n" + covar.length);
         log_i.debug("weights length:\n" + weights.length);

         //*********************************************************************
         // Check that number of components and dimensions are consistent
         //*********************************************************************
         int nComponents = weights.length;
         int nDims = means.length / nComponents;
         log_i.debug("weights length:\n" + weights.length);
         log_i.debug("nComponents:\n" + nComponents);
         log_i.debug("nDims:\n" + nDims);
         log_i.debug("nDims_i:\n" + nDims_i);

         if(nDims != nDims_i)
         {
            throw new IMMException("Matlab returned " + nDims + " dimensions "
                  + " but we expected " + nDims_i);
         }
         
         if(means.length != nComponents*nDims_i)
         {
            throw new IMMException("Matlab returned wrong number of means");
         }

         if(covar.length != nComponents*nDims_i*nDims_i)
         {
            throw new IMMException("Matlab returned wrong number of " 
                  + "covariances");
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
            log_i.debug("making component " + k);
            int meanStart = k*nDims;
            int meanEnd = (k+1)*nDims;
            double[] curMean = Arrays.copyOfRange(means,meanStart,meanEnd);
            
            int varStart = k*nDims*nDims;
            int varEnd   = (k+1)*nDims*nDims;
            double[] curVar  = Arrays.copyOfRange(covar,varStart,varEnd);

            log_i.debug("mean range: " + meanStart + "-" + meanEnd);
            log_i.debug("var  range: " + varStart + "-" + varEnd);
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

         throw new IMMException(e);
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

