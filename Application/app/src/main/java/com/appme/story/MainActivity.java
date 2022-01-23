package com.appme.story;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.CountDownTimer;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.appme.story.engine.app.fragments.FolderStructureFragment.Callback;
import static com.appme.story.engine.app.fragments.FolderStructureFragment.FileActionListener;
import static com.appme.story.engine.app.fragments.FolderStructureFragment.newInstance;

import com.appme.story.engine.app.fragments.FolderStructureFragment;
import com.appme.story.engine.app.folders.file.ProjectFileContract;
import com.appme.story.engine.app.folders.file.ProjectFilePresenter;
import com.appme.story.engine.app.utils.FUtils;
import com.appme.story.settings.AppSetting;

public class MainActivity extends AppCompatActivity implements FileActionListener {
    
    private static final String KEY_PROJECT_FILE = "KEY_PROJECT_FILE";
    protected File mProjectFile;
    protected ProjectFileContract.Presenter mFilePresenter;
    private AppSetting mPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreferences = new AppSetting(this);
        
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        
        new CountDownTimer(1200, 1200){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {  
                mProjectFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "AppMe");    
                mFilePresenter.show(mProjectFile, true);
            }
        }.start();
        
        setupFileView(savedInstanceState);
       
    }

    private void setupFileView(Bundle savedInstanceState) {
        FolderStructureFragment folderStructureFragment = null;
        if (savedInstanceState != null) {
            folderStructureFragment = (FolderStructureFragment)
                getSupportFragmentManager().findFragmentByTag(FolderStructureFragment.TAG);
        }
        if (folderStructureFragment == null) {
            folderStructureFragment = newInstance(mProjectFile);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_file, folderStructureFragment, FolderStructureFragment.TAG).commit();
        mFilePresenter = new ProjectFilePresenter(folderStructureFragment);
    }

    @Override
    public void clickNewModule() {
    }

    @Override
    public boolean clickCreateNewFile(File file, FolderStructureFragment.Callback callBack) {
        return false;
    }

    @Override
    public boolean clickRemoveFile(File file, FolderStructureFragment.Callback callBack) {
        return false;
    }

    @Override
    public void onFileClick(File file, FolderStructureFragment.Callback callBack) {    
            boolean success = openFileByAnotherApp(file);
            if (!success) {
                showFileInfo(file);
            }      
    }

    @Override
    public void onFileLongClick(File file, FolderStructureFragment.Callback callBack) {
        if (FUtils.canRead(file)) {
            showFileInfo(file);
        } else {
            if (!openFileByAnotherApp(file)) {
                showFileInfo(file);
            }
        }
    }
    
    private boolean openFileByAnotherApp(File file) {
        //don't open compiled file
        if (FUtils.hasExtension(file, "class", "dex", "jar")) {
            Toast.makeText(this, "Unable to open file", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            //create intent open file
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String ext = FUtils.fileExt(file.getPath());
            String mimeType = myMime.getMimeTypeFromExtension(ext != null ? ext : "");
            intent.setDataAndType(uri, mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return true;
    }
    

    /**
     * show dialog with file info
     * filePath, path, size, extension ...
     *
     * @param file - file to show info
     */
    private void showFileInfo(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(file.getName());
        builder.setView(R.layout.dialog_view_file);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView txtInfo = dialog.findViewById(R.id.txt_info);
        txtInfo.setText(file.getPath() + "\n" + file.length() + " byte");
        TextView text = dialog.findViewById(R.id.editor_view);
        text.setText(FUtils.readFileToString(file));
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_PROJECT_FILE, mProjectFile);
    }
}
/*don't forget to subscribe my YouTube channel for more Tutorial and mod*/
/*
https://youtube.com/channel/UC_lCMHEhEOFYgJL6fg1ZzQA */
