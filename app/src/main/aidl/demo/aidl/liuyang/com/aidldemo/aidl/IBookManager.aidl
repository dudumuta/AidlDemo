// IBookManager.aidl
package demo.aidl.liuyang.com.aidldemo.aidl;

// Declare any non-default types here with import statements
import demo.aidl.liuyang.com.aidldemo.aidl.Book;
import demo.aidl.liuyang.com.aidldemo.aidl.IOnNewBookArrivedListener;
interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unRegisterListener(IOnNewBookArrivedListener listener);
}
