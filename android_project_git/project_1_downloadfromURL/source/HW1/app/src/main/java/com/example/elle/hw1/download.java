package com.example.elle.hw1;

/**
 * Created by Elle on 9/27/14.
 */
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.File;
import  java.text.SimpleDateFormat;
import java.util.Date;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.database.Cursor;
        import android.graphics.drawable.Drawable;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Environment;
        import android.text.format.DateFormat;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.content.DialogInterface;
        import android.widget.EditText;
        import  android.app.DownloadManager;
        import  android.widget.TextView;






public class download extends Activity{
    // button to show progress dialog
    Button btnShowProgress;    //Define all the objects and constants;
    Button btnShowInternet;
    Boolean isInternetPresent=false;
    Button downLoadUrl;
    EditText mEdit;
    String file_url1;
    String serviceString = Context.DOWNLOAD_SERVICE;
    DownloadManager downloadManager;



    // Progress Dialog
    private ProgressDialog pDialog;
    ImageView my_image;
    TextView latency;
    long dif;
    boolean finished = false;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url = "http://www.winlab.rutgers.edu/~janne/mobisys14gesturesecurity.pdf";

    ConnectionDetector cd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // show progress bar button
        btnShowProgress = (Button) findViewById(R.id.btnProgressBar);
        btnShowInternet = (Button) findViewById(R.id.btnNetWork);
        mEdit = (EditText)this.findViewById(R.id.editText3);
        downLoadUrl=(Button) findViewById(R.id.buttonurl);
        file_url1 = "http://www.writerscentre.com.au/wp-content/uploads/2013/12/Writing-Picture-Books-grid.jpg";
        downloadManager = (DownloadManager)getSystemService(serviceString);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        // Image view to show image after downloading
        my_image = (ImageView) findViewById(R.id.my_image);
        latency = new TextView(this);
        latency=(TextView)findViewById(R.id.showlatency);

       cd =new ConnectionDetector(getApplicationContext());


        /**
         * Show Progress bar click event
         * */
        btnShowProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // starting new Async Task
                new DownloadFileFromURL().execute(file_url);

                //setContentView(latency);
            }
        });

        downLoadUrl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // starting new Async Task
                //new DownloadFileFromURL().execute(file_url1);
                String apkUrl= file_url1;

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
                request.setDestinationInExternalPublicDir("download","tu.jpg");

                request.setTitle("tu");
                request.setDescription("download desc");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                long downloadId = downloadManager.enqueue(request);











            }
        });
        btnShowInternet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent){
                    showDialog(download.this,"Internet found","Your internet connected",true);
                }
                else{
                    showDialog(download.this,"No internet found","Your internet doesn‘t connected",false);
                }

            }
        });
    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            Date   curDate   =   new   Date(System.currentTimeMillis());


            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();

                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
               Date endDate   =   new   Date(System.currentTimeMillis());
               dif = endDate.getTime() - curDate.getTime();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                File folder = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                File file = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/downloadfile.pdf");
                folder.mkdir();
                file.createNewFile();

                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                finished = true;


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/downloadfile.pdf";
            // setting downloaded into image view
            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
            latency.setText("latency:" + dif + "ms");
        }

    }

    public  void showDialog(Context context,String title, String message, Boolean status){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.ic_launcher: R.drawable.ic_launcher);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();



    }

}

