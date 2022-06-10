package github.groupa.fintech.drive;

import android.content.Context;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor executor= Executors.newSingleThreadExecutor();
    private Drive driveService;

    public DriveServiceHelper(Drive driveService) {
        this.driveService = driveService;
    }

   public Task<String> createFile(String filePath,String fileName) {
        return Tasks.call(executor,()->{
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            System.out.println("file name: "+fileMetadata.getName());
            java.io.File file = new java.io.File(filePath);
            FileContent mediaContent = new FileContent("text/csv", file);
            File file1 =null;
            try {
                file1 = driveService.files().create(fileMetadata, mediaContent).execute();
                System.out.println("File ID: " + file1.getId());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(file1!=null) {
                throw new IOException("An error occurred: " + file1.getName());
            }
            else{
                System.out.println("File ID: " + file1.getId());
                return file1.getId();
            }

        });
    }

    public Task<Boolean> checkFile(String fileName) {
        return Tasks.call(executor,()->{
            boolean bChecker = false;
            try {
                bChecker= driveService.files().list().setQ("name = '"+fileName+"'").execute().getFiles().size()>0;
            } catch (IOException e) {
                e.printStackTrace();
            }
           return bChecker;
        });
    }
  public Task<String> getFileId(String fileName) {
        return Tasks.call(executor,()->{
            File file = null;
            try {
                file = driveService.files().list().setQ("name = '"+fileName+"'").execute().getFiles().get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file.getId();
        });
    }

    public Task<Void> updateCsvFile(String fileId,String filePath) {
        return Tasks.call(executor,()->{
            java.io.File file = new java.io.File(filePath);
            FileContent mediaContent = new FileContent("text/csv", file);
            driveService.files().update(fileId,null,mediaContent).execute();
            return null;
        });
    }
 public Task<String> downloadFile(String fileId, Context context) {
        return Tasks.call(executor,()->{
            File file = null;
            try {
                file = driveService.files().get(fileId).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert file != null;
            String fileName = file.getName();
            String mimeType = file.getMimeType();
            String fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+fileName+"."+fileExtension;
            OutputStream outputStream = new java.io.FileOutputStream(filePath);
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
            return filePath;
        });
    }
}
