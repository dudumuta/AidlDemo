package demo.aidl.liuyang.com.aidldemo.binderpool;

import android.os.RemoteException;

import demo.aidl.liuyang.com.aidldemo.aidl.ISecurityCenter;

/**
 * Created by ly on 2018/12/10.
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {

    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String content) throws RemoteException {
        return encrypt(content);
    }
}
