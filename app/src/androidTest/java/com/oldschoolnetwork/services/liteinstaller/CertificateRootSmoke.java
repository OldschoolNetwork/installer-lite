package com.oldschoolnetwork.services.liteinstaller;
import android.content.Context;import java.io.*;import java.security.*;import java.util.*;import com.oldschoolnetwork.certificates.RootCertificates;
public final class CertificateRootSmoke {
 static byte[] hash()throws Exception{MessageDigest d=MessageDigest.getInstance("SHA-256");InputStream in=new FileInputStream("/system/etc/security/cacerts.bks");try{byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)d.update(b,0,n);}finally{in.close();}return d.digest();}
 public static void main(String[] args)throws Exception{
  if(android.os.Build.VERSION.SDK_INT!=8||!"goldfish".equals(android.os.Build.HARDWARE))throw new Exception("Isolated API8 emulator only");
  android.os.Looper.prepare();Class<?> activity=Class.forName("android.app.ActivityThread");java.lang.reflect.Method system=activity.getDeclaredMethod("systemMain");system.setAccessible(true);Object instance=system.invoke(null);java.lang.reflect.Method method=activity.getDeclaredMethod("getSystemContext");method.setAccessible(true);Context context=((Context)method.invoke(instance)).createPackageContext("com.oldschoolnetwork.services.liteinstaller",Context.CONTEXT_IGNORE_SECURITY);
  byte[] before=hash();RootCertificates.Log log=new RootCertificates.Log(){public void write(String text){}};
  try{String recovery=RootCertificates.install(context,log);if(recovery==null||Arrays.equals(before,hash()))throw new Exception("BKS was not updated");DataInputStream in=new DataInputStream(new FileInputStream("/system/etc/security/cacerts.bks"));try{if(in.readInt()!=1)throw new Exception("BKS is not version1");}finally{in.close();}RootCertificates.restore(context,log);if(!Arrays.equals(before,hash()))throw new Exception("BKS was not restored byte-for-byte");Writer result=new FileWriter("/data/local/tmp/on-api8-ca-result.txt");result.write("PASS: Mozilla roots merged into BKS v1 and original Android2.2 store restored byte-for-byte\n");result.close();}finally{if(RootCertificates.canRestore(context))RootCertificates.restore(context,log);}
 }
}
