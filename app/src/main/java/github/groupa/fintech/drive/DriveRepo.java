package github.groupa.fintech.drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import github.groupa.fintech.statistics.StatisticsModelClass;

public class DriveRepo {
    final Context context;
    final DriveServiceHelper dsh;
    final ProgressDialog prog;

    public DriveRepo(Context context, DriveServiceHelper dsh, ProgressDialog prog) {
        this.context = context;
        this.dsh = dsh;
        this.prog = prog;
    }

    public void uploadFile(String fileName, StatisticsModelClass statistics) throws IOException, InterruptedException {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        CSVWriter writer = null;

        try {
            file.createNewFile();
            writer = new CSVWriter(new FileWriter(file.getAbsolutePath()));
            String[] entries = {"Id", "Repeat", "Icon", "Type", "Amount", "Date", "Time", "Comment"};
            writer.writeNext(entries);
            writer.writeNext(new String[]{String.valueOf(statistics.getId()),
                    String.valueOf(statistics.getRepeat()),
                    String.valueOf(statistics.getTvAmount()),
                    String.valueOf(statistics.getIvIcon()),
                    statistics.getTvType(),
                    statistics.getDate(),
                    statistics.getTime(),
                    statistics.getComment()});
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("THIS IS JUST TO SEE WHAT THE APPLICATIONS OUTPUT SHOULD BEE" + file.getTotalSpace());
        dsh.createFile(file.getAbsolutePath(), fileName).addOnCompleteListener(task -> {
            prog.dismiss();
        });
    }

    public void updateFile(String fileUrl, StatisticsModelClass statistics, String fileId) throws IOException {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(fileUrl, true));
            writer.writeNext(new String[]{String.valueOf(statistics.getId()),
                    String.valueOf(statistics.getRepeat()),
                    String.valueOf(statistics.getTvAmount()),
                    String.valueOf(statistics.getIvIcon()),
                    statistics.getTvType(),
                    statistics.getDate(),
                    statistics.getTime(),
                    statistics.getComment()});
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dsh.updateCsvFile(fileId, fileUrl).addOnCompleteListener(task -> {
            prog.dismiss();
        });
    }


}
