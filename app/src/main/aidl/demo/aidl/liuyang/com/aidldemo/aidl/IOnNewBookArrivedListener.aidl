// IOnNewBookArrivedListener.aidl
package demo.aidl.liuyang.com.aidldemo.aidl;

// Declare any non-default types here with import statements
import demo.aidl.liuyang.com.aidldemo.aidl.Book;
interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}
