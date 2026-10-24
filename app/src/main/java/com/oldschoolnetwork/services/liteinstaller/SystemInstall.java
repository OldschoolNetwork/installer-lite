package com.oldschoolnetwork.services.liteinstaller;
import android.content.*;import android.os.*;import com.oldschoolnetwork.certificates.RootCertificates;import java.io.*;import java.util.*;import org.json.*;
final class SystemInstall {
 static String install(Context context,List<JSONObject> releases,List<File> files,List<GoogleComponents.Entry> removals)throws Exception{
  if(Build.VERSION.SDK_INT>18)throw new IOException("Full Lite installation supports Android 2.2–4.3. Use the regular installer on newer systems.");GoogleComponents.validate(removals);
  long needed=0;for(File f:files)needed+=f.length();StatFs fs=new StatFs("/system");if((long)fs.getAvailableBlocks()*fs.getBlockSize()<needed+262144)throw new IOException("Not enough free space on /system for the selected services.");
  String backup="/data/local/on-lite-backups/"+System.currentTimeMillis();StringBuilder backups=new StringBuilder(),changes=new StringBuilder(),restore=new StringBuilder("#!/system/bin/sh\n"+RootCertificates.systemShellPrelude()+"on_remount rw\ntrap 'on_remount ro || true' EXIT\n");
  for(int i=0;i<releases.size();i++){String id=releases.get(i).getString("id");if(!id.equals("core")&&!id.equals("store"))throw new IOException("Unknown payload service.");String target="/system/app/ON_"+(id.equals("core")?"Core":"Store")+"Lite.apk",saved=backup+"/"+id+".apk";
   restore.append("on_remove ").append(q(target)).append("\nif on_exists ").append(q(saved)).append("; then cat ").append(q(saved)).append(" > ").append(q(target)).append("; chmod 644 ").append(q(target)).append("; fi\n");
   backups.append("if on_exists ").append(q(target)).append("; then cat ").append(q(target)).append(" > ").append(q(saved)).append("; fi\n");
   restore.append("on_remove ").append(q(target+".on-new")).append("\n");
   changes.append("cat ").append(q(files.get(i).getAbsolutePath())).append(" > ").append(q(target+".on-new")).append("\nchmod 644 ").append(q(target+".on-new")).append("\nchown 0:0 ").append(q(target+".on-new")).append("\nmv ").append(q(target+".on-new")).append(" ").append(q(target)).append("\n");
  }
  for(GoogleComponents.Entry item:removals){String saved=backup+"/google-"+item.packageName+".apk";backups.append("cat ").append(q(item.path)).append(" > ").append(q(saved)).append("\n");changes.append("on_remove ").append(q(item.path)).append("\n");restore.append("if on_exists ").append(q(saved)).append("; then cat ").append(q(saved)).append(" > ").append(q(item.path)).append("; chmod 644 ").append(q(item.path)).append("; fi\n");}
  File restoreFile=new File(context.getFilesDir(),"restore-services.sh");Writer out=new OutputStreamWriter(new FileOutputStream(restoreFile),"UTF-8");try{out.write(restore.toString());}finally{out.close();}
  String script=RootCertificates.systemShellPrelude()+"case \"$(id)\" in uid=0\\(*) ;; *) echo 'Root access required'; exit 1;; esac\non_remount rw\ntrap 'on_remount ro || true' EXIT\numask 077\nif ! on_exists /data/local/on-lite-backups; then mkdir /data/local/on-lite-backups; fi\nmkdir "+q(backup)+"\nchmod 700 "+q(backup)+"\ncat "+q(restoreFile.getAbsolutePath())+" > "+q(backup+"/restore.sh")+"\nchmod 700 "+q(backup+"/restore.sh")+"\n"+backups+"rollback() { code=$?; trap - EXIT HUP INT TERM; if ! on_zero \"$code\"; then sh "+q(backup+"/restore.sh")+" || true; fi; on_remount ro || true; exit $code; }\ntrap rollback EXIT HUP INT TERM\n"+changes;
  RootCertificates.runRoot(script);return backup+"/restore.sh";
 }
 static String q(String s){return RootCertificates.quote(s);}
}
