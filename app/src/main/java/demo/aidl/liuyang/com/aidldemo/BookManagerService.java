package demo.aidl.liuyang.com.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import demo.aidl.liuyang.com.aidldemo.aidl.Book;
import demo.aidl.liuyang.com.aidldemo.aidl.IBookManager;
import demo.aidl.liuyang.com.aidldemo.aidl.IOnNewBookArrivedListener;

/**
 * Created by ly on 2018/12/4.
 */

public class BookManagerService extends Service {

    private static final String TAG = "BMS";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
    private RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.d(TAG, "getBookList-进程名称：" + Utils.getAppName(BookManagerService.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName());
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.d(TAG, "addBook-进程名称：" + Utils.getAppName(BookManagerService.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName());
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
            Log.d(TAG, "registerListener-进程名称：" + Utils.getAppName(BookManagerService.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName() + ", 回调数：" + mListeners.beginBroadcast());
            mListeners.finishBroadcast();
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
            Log.d(TAG, "registerListener-进程名称：" + Utils.getAppName(BookManagerService.this, android.os.Process.myPid()) +", 线程名称：" + Thread.currentThread().getName() + ", 回调数：" + mListeners.beginBroadcast());
            mListeners.finishBroadcast();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener l = mListeners.getBroadcastItem(i);
            if (l != null) {
                l.onNewBookArrived(book);
            }
        }
        mListeners.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId, "new Booke#" + bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
