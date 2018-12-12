package demo.aidl.liuyang.com.aidldemo.binderpool;

import android.os.RemoteException;

import demo.aidl.liuyang.com.aidldemo.aidl.ICompute;

/**
 * Created by ly on 2018/12/10.
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
