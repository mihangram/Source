package org.aspectj.runtime.internal;

import org.aspectj.runtime.internal.cflowstack.ThreadCounter;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactory;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl;
import org.aspectj.runtime.internal.cflowstack.ThreadStackFactoryImpl11;

public class CFlowCounter
{
  private static ThreadStackFactory tsFactory;
  private ThreadCounter flowHeightHandler = tsFactory.getNewThreadCounter();
  
  static {}
  
  private static String getSystemPropertyWithoutSecurityException(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = System.getProperty(paramString1, paramString2);
      return paramString1;
    }
    catch (SecurityException paramString1) {}
    return paramString2;
  }
  
  private static ThreadStackFactory getThreadLocalStackFactory()
  {
    return new ThreadStackFactoryImpl();
  }
  
  private static ThreadStackFactory getThreadLocalStackFactoryFor11()
  {
    return new ThreadStackFactoryImpl11();
  }
  
  public static String getThreadStackFactoryClassName()
  {
    return tsFactory.getClass().getName();
  }
  
  private static void selectFactoryForVMVersion()
  {
    String str = getSystemPropertyWithoutSecurityException("aspectj.runtime.cflowstack.usethreadlocal", "unspecified");
    if (str.equals("unspecified"))
    {
      if (System.getProperty("java.class.version", "0.0").compareTo("46.0") >= 0) {}
      for (i = 1; i != 0; i = 0)
      {
        tsFactory = getThreadLocalStackFactory();
        return;
      }
    }
    if ((str.equals("yes")) || (str.equals("true"))) {}
    for (int i = 1;; i = 0) {
      break;
    }
    tsFactory = getThreadLocalStackFactoryFor11();
  }
  
  public void dec()
  {
    this.flowHeightHandler.dec();
    if (!this.flowHeightHandler.isNotZero()) {
      this.flowHeightHandler.removeThreadCounter();
    }
  }
  
  public void inc()
  {
    this.flowHeightHandler.inc();
  }
  
  public boolean isValid()
  {
    return this.flowHeightHandler.isNotZero();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\internal\CFlowCounter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */