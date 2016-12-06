package com.example.activitytest.daygram;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by apple on 2016/9/21.
 */
public class editactivity extends Activity implements View.OnClickListener {
    private TextView editTitle;
    private Button button_done;
    private EditText edit_text;
    Diary tddiary;
    Bitmap bitmap;
    int Screenwidth;
    String[] monthS = {"JANUARY ", "FENRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit);
        Intent intent = getIntent();
        tddiary = (Diary) intent.getSerializableExtra("today");
        int Year = intent.getIntExtra("Year", -1);
        String month = monthS[Integer.parseInt(tddiary.getmonth()) - 1];
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Screenwidth = dm.widthPixels;

        String edit_title = tddiary.getdaycount() + ". /" + month + " " + tddiary.getdayNum() + " / " + String.valueOf(Year);
        editTitle = (TextView) findViewById(R.id.edit_title);
        editTitle.setText(edit_title);

        button_done = (Button) findViewById(R.id.edit_done);
        button_done.setOnClickListener(this);

        //显示日记内容，图片地址转换显示
        edit_text = (EditText) findViewById(R.id.edit_text);
        SpannableString ss = new SpannableString(tddiary.getdiaryText());
        Pattern p= Pattern.compile("<img src=\"(.*?)\" />"); //寻找图片
        Matcher m=p.matcher(tddiary.getdiaryText());
        while(m.find()){
            Bitmap bm = BitmapFactory.decodeFile(m.group(1));
                Bitmap rbm = resizeImage(bm, 400,400);
                bm=rbm;
            ImageSpan span = new ImageSpan(this, bm);
            ss.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        edit_text.setText(ss);
        //edit_text.setText(tddiary.getdiaryText());

        //插入当前时间
        Button button_clock = (Button) findViewById(R.id.edit_clock);
        button_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertText(edit_text, gettime());
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Button button_insert=(Button)findViewById(R.id.insert_button);
        button_insert.setOnClickListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_done: //完成日记，返回主界面
                String inputText = edit_text.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("new_diary", inputText);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.insert_button: //插入图片
                Intent getImage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, 1);
                break;
        }
    }

    //获取当前时间
    private String gettime() {
        String timenow;
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        timenow = String.valueOf(hour) + ":" + String.valueOf(minute) + " ";
        return timenow;
    }
    //加载图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri originalUri = intent.getData();
                try {
                   //Bitmap originalBitmap
                     bitmap= BitmapFactory.decodeStream(resolver
                            .openInputStream(originalUri));
                        Bitmap rbm = resizeImage(bitmap, 400,400);
                        bitmap=rbm;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    insertIntoEditText(getBitmapMime(bitmap, originalUri));
                }
            }
        }
    }
    //获取图片对象
    private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
        String path = getPhotoPathFromContentUri(this,uri);
        String longpath="<img src=\"" + path + "\" />";
        SpannableString ss = new SpannableString(longpath);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, longpath.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

  //插入图片到光标位置
    private void insertIntoEditText(SpannableString ss) {
        Editable et = edit_text.getText();// 先获取Edittext中的内容
        int start = edit_text.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        edit_text.setText(et);// 把et添加到Edittext中
        edit_text.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    //改变图像大小
    public static Bitmap resizeImage(Bitmap bgimage, double newWidth,double newHeight
                                  ) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
    //图片uri转真实地址
    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if(context == null || uri == null) {
            return photoPath;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if(isExternalStorageDocument(uri)) {
                String [] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    if("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            else if(isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            }
            else if(isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[] { split[1] };
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        }
        else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }
    private int getEditTextCursorIndex(EditText mEditText) {
        return mEditText.getSelectionStart();
    }

    private void insertText(EditText mEditText, String mText) {
        mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
    }
}
