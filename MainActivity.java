package abongcher.in.filewritereadexternal;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputtext;
    private Button savetext;
    private Button viewtext;
    private TextView displaytext;

    private final String filename = "Cashier.txt";
    StringBuffer stringBuffer;
    File fileToReadAndWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputtext = (EditText) findViewById(R.id.input);
        savetext =(Button) findViewById(R.id.buttonsave);
        viewtext = (Button) findViewById(R.id.buttonread);
        displaytext = (TextView) findViewById(R.id.textViewid);

        savetext.setOnClickListener(MainActivity.this);
        viewtext.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.buttonsave:
                if(isExternalStorageWritable()) {
                    if(getDocumentStoragePath().exists() && getDocumentStoragePath().isDirectory()){
                        if(isFileWritable(getDocumentStoragePath())){
                            writeToFile(getFilename());
                        }else{
                            Toast.makeText(MainActivity.this, "Not Writable dir", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "No directory", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Storage not writable.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buttonread:
                if(isExternalStorageReadable()){
                    if(getDocumentStoragePath().exists() && getDocumentStoragePath().isDirectory()){
                        if(isFileReadable(getDocumentStoragePath())){
                            new readFromFile().execute(getFilename());
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Could not read.", Toast.LENGTH_LONG).show();

                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "External storage not readable.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(MainActivity.this, "No job", Toast.LENGTH_LONG).show();

        }
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    public File getDocumentStoragePath() {
        // Get the directory for the user's public documents directory.
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!path.mkdirs()) {
            path.mkdirs();
        }
        return path;
    }



    public File getFilename(){
        fileToReadAndWrite = new File(getDocumentStoragePath(), filename);
        return fileToReadAndWrite;
    }

    public boolean isFileWritable(File file){
        if(!file.canWrite()){
            return file.setWritable(true);
        }
        return file.canWrite();
    }




    public void writeToFile(File textfile){
        try {

            FileOutputStream fos = new FileOutputStream(textfile);
            fos.write(inputtext.getText().toString().getBytes());
            fos.close();
            Toast.makeText(MainActivity.this, "Finish writing...", Toast.LENGTH_SHORT).show();
            inputtext.setText("");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }finally {
            inputtext.setHint(R.string.input_hint);
        }
    }



    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isFileReadable(File file){
        if(!file.canRead()){
            return file.setReadable(true);
        }
        return file.canRead();
    }



    public class readFromFile extends AsyncTask<File, Void, String> {

        @Override
        protected String doInBackground(File... files) {
            File thefile = (File) files[0];
            stringBuffer = new StringBuffer();

            try {
                FileInputStream fis = new FileInputStream(thefile);
                DataInputStream in = new DataInputStream(fis);
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                String strLine = "";
                while ((strLine = br.readLine()) != null) {
                    stringBuffer.append(strLine);
                }
                fis.close();
                in.close();
                isr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return String.valueOf(stringBuffer);
        }

        @Override
        protected void onPostExecute(String data) {
            displaytext.setText(data);
        }
    }
}
