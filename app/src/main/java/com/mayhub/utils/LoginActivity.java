package com.mayhub.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayhub.utils.adapter.BasePagerAdapter;
import com.mayhub.utils.common.FileUtils;
import com.mayhub.utils.common.ToastUtils;
import com.mayhub.utils.download.DownloadListener;
import com.mayhub.utils.download.DownloadTask;
import com.mayhub.utils.download.FileDownloaderManager;
import com.mayhub.utils.download.MultiDownloadTask;
import com.mayhub.utils.test.TestHeadFootAdapter;
import com.mayhub.utils.test.TestInfiniteAdapter;
import com.mayhub.utils.widget.CusViewPager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private static final String HOST = "http://172.18.1.188/ielts/user/register";

    private int index = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private CusViewPager viewPager;
    private RecyclerView recyclerView;
    private TestHeadFootAdapter testHeadFootAdapter;
    private String[] testStr = new String[]{"testStr1","testStr2","testStr3","testStr4","testStr5","testStr6"};
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(testHeadFootAdapter = new TestHeadFootAdapter());
        viewPager = (CusViewPager) findViewById(R.id.viewpager);
//        viewPager.setAdapter(new TestBasePagerAdapter(Arrays.asList(testStr), new BasePagerAdapter.PagerItemClickListener<String>() {
//            @Override
//            public void onItemClick(int pos, String str) {
//                ToastUtils.getInstance().showShortToast(getApplicationContext(), pos + " - " + str);
//            }
//        }));
        viewPager.setAdapter(new TestInfiniteAdapter(Arrays.asList(testStr), new BasePagerAdapter.PagerItemClickListener<String>() {
            @Override
            public void onItemClick(int pos, String s) {
                ToastUtils.getInstance().showShortToast(getApplicationContext(),pos + " - " + s);
            }
        }));

        Log.e("Build",String.format("Device = %s , Model = %s, SDK_INT = %s, RELEASE = %s",Build.DEVICE, Build.MODEL, Build.VERSION.SDK_INT, Build.VERSION.RELEASE));



        viewPager.startAutoSlide();
        viewPager.setDefaultFixedScroller();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                startThread();
//                if(index % 2 == 0) {
//                    View headView = View.inflate(getApplicationContext(), R.layout.layout_list_item_head, null);
//                    TextView textView = (TextView) headView.findViewById(R.id.tv_head);
//                    textView.setText(String.format("Head - %s", testHeadFootAdapter.getHeadViewsCount() + 1));
//                    testHeadFootAdapter.addHeadView(headView);
//                }else{
//                    View headView = View.inflate(getApplicationContext(), R.layout.layout_list_item_foot, null);
//                    TextView textView = (TextView) headView.findViewById(R.id.tv_foot);
//                    textView.setText(String.format("Foot - %s", testHeadFootAdapter.getFootViewsCount() + 1));
//                    testHeadFootAdapter.addFootView(headView);
//                }
//                index ++;
//                Intent intent = new Intent(getApplicationContext(), DialogService.class);
//                if(view.getTag() == null) {
//                    intent.putExtra(DialogService.TASK_PARAMS, DialogService.TASK_LOADING);
//                    view.setTag(1);
//                }else{
//                    intent.putExtra(DialogService.TASK_PARAMS, DialogService.TASK_SHOW_MSG);
//                    intent.putExtra(DialogService.TASK_SHOW_MSG_CONTENT, "this is content from activity");
//                    intent.putExtra(DialogService.TASK_SHOW_MSG_BTN, "got it");
//                    view.setTag(null);
//                }
//                startService(intent);
//                attemptLogin();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    public static String getTpoName(String tpo, int position) {
        String topName = "";
        switch (position) {
            case 1:
                topName = tpo + "_listening_passage1_1.mp3";
                break;

            case 2:
                topName = tpo + "_listening_passage1_2.mp3";
                break;

            case 3:
                topName = tpo + "_listening_passage1_3.mp3";
                break;

            case 4:
                topName = tpo + "_listening_passage2_1.mp3";
                break;

            case 5:
                topName = tpo + "_listening_passage2_2.mp3";
                break;

            case 6:
                topName = tpo + "_listening_passage2_3.mp3";
                break;

        }
        return topName;
    }

    public static String getPrefixByIndex(int index)
    {
        if (index < AppParam.TPO_COUNT)
        {
            return "tpo" + (index + 1);
        }
        else if (index < AppParam.TPO_COUNT + AppParam.TPO_EX_COUNT)
        {
            return "tpoex" + (index - AppParam.TPO_COUNT + 1);
        }
        else
        {
            return "tpoog"
                    + (index - AppParam.TPO_COUNT - AppParam.TPO_EX_COUNT + 1);
        }
    }
    public static String getPrefixByFilename(String filename)
    {
        int index = filename.indexOf("_");
        if (index >= 0)
        {
            return filename.substring(0, index);
        }
        return filename;
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    String postOkhttp(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("info", "fNjkWKIHO+Ti588GcJvhaGf/VeVmzula5xnEBBmXjWfcpTcIZci1ZgM/2w2SO2E47N69uYSu38El0G+tasLRnJghtEetckbqiQawMEgwkSNT1g2Va92NitO071RgCZn9vitlo8/Tm9te5UKIWui3vD1zEJ1awMKjIOkv7u4gQCg0kb+V/PAK288BND9RcdU4c9mjUO6lRG0XH+k9JujXwgLWx2kZ5Y9wa4ojLFmVwV/1FSxfF8yY+lqV6nbUUwHS0lCEnA1G+exqhMPSO+N2Pe9+i5g4K6xZKIajJ+4C2wvg/D2zFKxkKQAD6WLnnwRcxxCezbM/WP0eHdjl5FeTPL3HyJlKvh1UzyfcrjKF/ageCP96CHTMMFlcOMSy5GNDwFEy6yD2x1WXocyJEYlajYvbH1l6LxcAhI9Wa27EO3PnmKToo28S/eg/Bn3OalVS")
                .add("timestamp", "1475226339994")
                .build();

        Request request = new Request.Builder()
                .addHeader("Accept", "application/json; q=0.5")
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }



    long runReq(String url) throws IOException {
        Request request = new Request.Builder()
                .addHeader("Connection","close")
                .addHeader("Accept-Encoding", "none")
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().contentLength();
    }

    private void startThread(){



        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String result = postOkhttp("http://172.19.0.28/ielts/user/register");
                    Log.e("result = ",result);
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                }

                Log.e("okhttp end","-----------------");

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                FileDownloaderManager.DownloadConf conf = new FileDownloaderManager.DownloadConf();
                conf.setCheckLocalFileExist(true);
                conf.setDownloadWay(FileDownloaderManager.DownloadWay.FIFO);
                conf.setEnableLocalCheckFirst(true);
                conf.setMaxRetryCount(2);
                conf.setOverrideLocalFileExist(false);
                FileDownloaderManager.getInstance("mp3").initDownloadConf(conf);
//                        FileDownloaderManager.getInstance("mp4").initDownloadConf(new FileDownloaderManager.DownloadConf(5, FileDownloaderManager.DownloadWay.LIFO));
                List<String> urls = new ArrayList<>();
//                for (int i = 0; i < 34; i++) {
//                    final String prefix = getPrefixByIndex(i);
                    urls.clear();
//                    for (int j = 1; j <= 6; j++) {
//                        String fileName = getTpoName(prefix, j);
//                        String fileUrl = "http://cdn.tiku.zhan.com/"
//                                + getPrefixByFilename(fileName) + "/"
//                                + fileName;
//                        urls.add(fileUrl);
//
//                    }
                    urls.add("http://dl.tiku.zhan.com/ielts%2Faudio%2F1324586%2F078_2_1474365535937_10.m4a");
                    urls.add("http://dl.tiku.zhan.com/ielts/audio/1324586/078_1_1474425636200_19.m4a");
                    DownloadTask downloadTask = new MultiDownloadTask(urls, new DownloadListener() {


                        @Override
                        public void onPrepare(Object tag, String realUrl, int index, int total) {

                        }

                        @Override
                        public void onStart(Object tag, String fileName, String realUrl, int fileLength, int index, int total) {

                        }

                        @Override
                        public void onProgress(Object tag, String realUrl, int progress, int index, int total) {
                            float every = 100f / total;
                            float current = every * progress / 100f;
//                            Log.e("onProgress", String.format("%s =================== %s", tag, every * index + current));
                        }

                        @Override
                        public void onCancel(Object tag, String realUrl, int progress, int index, int total) {

                        }

                        @Override
                        public void onFinish(Object tag, File file, String realUrl, int index, int total) {
                            Log.e("onFinish", String.format("%s ----------------------", tag));
                        }

                        @Override
                        public void onError(Object tag, int status, String error, File file, String realUrl, int index, int total) {
                            Log.e("onError", error + " -----> " + realUrl);
                        }
                    }, Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/");
                    downloadTask.setTag(String.format("TPO %s", 1));
                    FileDownloaderManager.getInstance("mp3").addTaskAndStart(downloadTask);
//                    FileDownloaderManager.getInstance("mp4").addTaskAndStart(downloadTask);
//                }

//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                FileDownloaderManager.getInstance("mp3").cancelAll();
//
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                FileDownloaderManager.getInstance("mp4").cancelAll();

                long fileSize = FileUtils.getFileSize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/");
                boolean existWithSize = FileUtils.isDirExistWithSize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/", fileSize+100);
                boolean existWithFileCount = FileUtils.isDirExistWithFileCount(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/",20);
                Log.e("filesize = " , fileSize + "" );
                Log.e("existWithSize = " , existWithSize + "" );
                Log.e("existWithFileCount = " , existWithFileCount + "" );
            }
        }).start();
    }


    private void mergerTwoFile(){
        //创建一个合并流的对象
        SequenceInputStream sis = null;
        //创建输出流。
        BufferedOutputStream bos = null;
        try {
            //构建流集合。
            Vector<InputStream> vector = new Vector<InputStream>();
            vector.addElement(new FileInputStream("/mnt/sdcard/ieltsword/audioNet/and.mp3"));
            vector.addElement(new FileInputStream("/mnt/sdcard/ieltsword/audioNet/be.mp3"));
            vector.addElement(new FileInputStream("/mnt/sdcard/ieltsword/audioNet/children.mp3"));
            Enumeration<InputStream> e = vector.elements();


            sis = new SequenceInputStream(e);

            bos = new BufferedOutputStream(
                    new FileOutputStream("/mnt/sdcard/ieltsword/audioNet/t1.mp3"));
            //读写数据
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = sis.read(buf)) != -1) {
                bos.write(buf, 0, len);
                bos.flush();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (sis != null)
                    sis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private class AppParam {
        public final static int TPO_COUNT = 34, TPO_EX_COUNT = 2, TPO_OG_COUNT = 3;
    }
}

