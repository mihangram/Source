package org.aspectj.internal.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface ajcPrivileged {}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\aspectj\internal\lang\annotation\ajcPrivileged.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */