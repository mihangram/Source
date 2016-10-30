package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker
{
  static StringMaker longStringMaker;
  static StringMaker middleStringMaker;
  static StringMaker shortStringMaker = new StringMaker();
  int cacheOffset;
  boolean includeArgs = true;
  boolean includeEnclosingPoint = true;
  boolean includeJoinPointTypeName = true;
  boolean includeModifiers = false;
  boolean includeThrows = false;
  boolean shortKindName = true;
  boolean shortPrimaryTypeNames = false;
  boolean shortTypeNames = true;
  
  static
  {
    shortStringMaker.shortTypeNames = true;
    shortStringMaker.includeArgs = false;
    shortStringMaker.includeThrows = false;
    shortStringMaker.includeModifiers = false;
    shortStringMaker.shortPrimaryTypeNames = true;
    shortStringMaker.includeJoinPointTypeName = false;
    shortStringMaker.includeEnclosingPoint = false;
    shortStringMaker.cacheOffset = 0;
    middleStringMaker = new StringMaker();
    middleStringMaker.shortTypeNames = true;
    middleStringMaker.includeArgs = true;
    middleStringMaker.includeThrows = false;
    middleStringMaker.includeModifiers = false;
    middleStringMaker.shortPrimaryTypeNames = false;
    shortStringMaker.cacheOffset = 1;
    longStringMaker = new StringMaker();
    longStringMaker.shortTypeNames = false;
    longStringMaker.includeArgs = true;
    longStringMaker.includeThrows = false;
    longStringMaker.includeModifiers = true;
    longStringMaker.shortPrimaryTypeNames = false;
    longStringMaker.shortKindName = false;
    longStringMaker.cacheOffset = 2;
  }
  
  public void addSignature(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null) {
      return;
    }
    if (!this.includeArgs)
    {
      if (paramArrayOfClass.length == 0)
      {
        paramStringBuffer.append("()");
        return;
      }
      paramStringBuffer.append("(..)");
      return;
    }
    paramStringBuffer.append("(");
    addTypeNames(paramStringBuffer, paramArrayOfClass);
    paramStringBuffer.append(")");
  }
  
  public void addThrows(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if ((!this.includeThrows) || (paramArrayOfClass == null) || (paramArrayOfClass.length == 0)) {
      return;
    }
    paramStringBuffer.append(" throws ");
    addTypeNames(paramStringBuffer, paramArrayOfClass);
  }
  
  public void addTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    int i = 0;
    while (i < paramArrayOfClass.length)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(makeTypeName(paramArrayOfClass[i]));
      i += 1;
    }
  }
  
  String makeKindName(String paramString)
  {
    int i = paramString.lastIndexOf('-');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  String makeModifiersString(int paramInt)
  {
    if (!this.includeModifiers) {
      return "";
    }
    String str = Modifier.toString(paramInt);
    if (str.length() == 0) {
      return "";
    }
    return str + " ";
  }
  
  public String makePrimaryTypeName(Class paramClass, String paramString)
  {
    return makeTypeName(paramClass, paramString, this.shortPrimaryTypeNames);
  }
  
  public String makeTypeName(Class paramClass)
  {
    return makeTypeName(paramClass, paramClass.getName(), this.shortTypeNames);
  }
  
  String makeTypeName(Class paramClass, String paramString, boolean paramBoolean)
  {
    if (paramClass == null) {
      return "ANONYMOUS";
    }
    if (paramClass.isArray())
    {
      paramClass = paramClass.getComponentType();
      return makeTypeName(paramClass, paramClass.getName(), paramBoolean) + "[]";
    }
    if (paramBoolean) {
      return stripPackageName(paramString).replace('$', '.');
    }
    return paramString.replace('$', '.');
  }
  
  String stripPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\runtime\reflect\StringMaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */