package com.oldschoolnetwork.services.liteinstaller;
import android.content.Context;import java.io.*;import java.util.*;import org.json.*;import com.oldschoolnetwork.certificates.RootCertificates;
public final class SystemPayloadSmoke {
 public static void main(String[] args)throws Exception {
  if(android.os.Build.VERSION.SDK_INT!=8||!"goldfish".equals(android.os.Build.HARDWARE))throw new Exception("Isolated API8 emulator only");
  android.os.Looper.prepare();Class<?> type=Class.forName("android.app.ActivityThread");java.lang.reflect.Method system=type.getDeclaredMethod("systemMain");system.setAccessible(true);Object instance=system.invoke(null);java.lang.reflect.Method get=type.getDeclaredMethod("getSystemContext");get.setAccessible(true);Context c=((Context)get.invoke(instance)).createPackageContext("com.oldschoolnetwork.services.liteinstaller",Context.CONTEXT_IGNORE_SECURITY);
  File core=new File("/system/app/ON_CoreLite.apk"),store=new File("/system/app/ON_StoreLite.apk");if(core.exists()||store.exists())throw new Exception("QA system already contains ON Lite; refusing to overwrite");
  List<JSONObject> entries=Payload.entries(c,true);List<File> files=Payload.prepare(c,entries);String restore=null;
  try {restore=SystemInstall.install(c,entries,files,Collections.<GoogleComponents.Entry>emptyList());if(!core.isFile()||!store.isFile())throw new Exception("System APK staging failed");for(int i=0;i<entries.size();i++){File target=entries.get(i).getString("id").equals("core")?core:store;if(!digest(target).equals(entries.get(i).getString("sha256")))throw new Exception("Staged APK checksum mismatch");}
   restore(restore);restore=null;if(core.exists()||store.exists())throw new Exception("Restore did not remove staged files");Writer out=new FileWriter("/data/local/tmp/on-api8-payload-result.txt");out.write("PASS: signed Lite payload validated, both system APKs staged and rollback restored original system state\n");out.close();
  } finally {if(restore!=null)restore(restore);}
 }
 static void restore(String path)throws Exception {Process p=Runtime.getRuntime().exec(new String[]{"/system/bin/sh",path});if(p.waitFor()!=0)throw new Exception("Restore process failed");}
 static String digest(File f)throws Exception{java.security.MessageDigest d=java.security.MessageDigest.getInstance("SHA-256");InputStream in=new FileInputStream(f);try{byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)d.update(b,0,n);}finally{in.close();}return Payload.hex(d.digest());}
}
