package org.soton.vdpgm_srv;

public class IMMException extends Exception
{
   public IMMException(String message)
   {
      super(message);
   }

   public IMMException(Throwable cause)
   {
      super(cause);
   }

   public IMMException(String message, Throwable cause)
   {
      super(message,cause);
   }
}
