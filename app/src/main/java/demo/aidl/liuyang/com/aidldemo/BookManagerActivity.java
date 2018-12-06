package demo.aidl.liuyang.com.aidldemo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.List;

import demo.aidl.liuyang.com.aidldemo.aidl.Book;
import demo.aidl.liuyang.com.aidldemo.aidl.IBookManager;
import demo.aidl.liuyang.com.aidldemo.aidl.IOnNewBookArrivedListener;

/**
 * Created by ly on 2018/12/6.
 */

public class BookManagerActivity extends FragmentActivity {
    private static final String TAG = "BMA";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;
    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            Log.d(TAG, "onNewBookArrived-新书来了,进程名称：" + Utils.getAppName(BookManagerActivity.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName());
            Message message = new Message();
            message.obj = book;
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, message).sendToTarget();
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "receive new book-新书来了,进程名称：" + Utils.getAppName(BookManagerActivity.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName() + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
            mRemoteBookManager = bookManager;
            try {
                List<Book> list = bookManager.getBookList();
                Log.d(TAG, "onServiceConnected getBookList-进程名称：" + Utils.getAppName(BookManagerActivity.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName() + ", list数：" + list.size());
                Book book = new Book(3, "Android 进阶");
                bookManager.addBook(book);
                Log.d(TAG, "onServiceConnected addBook-进程名称：" + Utils.getAppName(BookManagerActivity.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName() + ", list数：" + list.size());
                ;
                bookManager.registerListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                mRemoteBookManager.unRegisterListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
